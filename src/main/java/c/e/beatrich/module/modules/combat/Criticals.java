package c.e.beatrich.module.modules.combat;

import c.e.beatrich.mixininterface.IServerboundMovePlayerPacket;
import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import c.e.beatrich.setting.types.BoolSetting;
import c.e.beatrich.setting.types.DoubleSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.MaceItem;

public class Criticals extends Module {

    public final BoolSetting maceSpoof = boolSetting("MaceSpoof", "重锤平地暴击", false);
    public final DoubleSetting FallDisdance = doubleSetting("FallDisdance", "下落高度", 1.51, 0.0, 15.0);
    public final BoolSetting autoSwap = boolSetting("AutoSwap", "自动切锤", false);
    private boolean didSwap = false;
    private boolean HasMace = false;
    private int previousSlot = 0;
    private static final double CRIT_HEIGHT = 0.0625;

    public Criticals() {
        super("Criticals", "刀爆", Category.COMBAT);
    }

    public void preAttack(Player player, Entity target) {
        if (!isActive() || mc.player == null || mc.getConnection() == null) return;
        if (autoSwap.get()) swapMace();
        if (mc.player.isFallFlying()) return;
        double height = mc.player.fallDistance > CRIT_HEIGHT ? 0.0 : CRIT_HEIGHT;
        if (maceSpoof.get() && HasMace && mc.player.fallDistance < FallDisdance.get()) height = FallDisdance.get();
        sendSpoofedMovement(height);
    }

    public void postAttack() {
        if (!isActive()) return;
        swapBack();
    }

    private void sendSpoofedMovement(double height) {
        if (0.0 == height) return;
        var conn = mc.getConnection();
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();
        ServerboundMovePlayerPacket packet1 = new ServerboundMovePlayerPacket.Pos(x, y, z, false);
        ServerboundMovePlayerPacket packet2 = new ServerboundMovePlayerPacket.Pos(x, y + height, z, false);
        ((IServerboundMovePlayerPacket)packet1).hcirtaeb$setTag(1337);
        ((IServerboundMovePlayerPacket)packet2).hcirtaeb$setTag(1337);
        conn.send(packet1);
        conn.send(packet2);
        conn.send(packet1);
    }

    private void swapMace() {
        if (mc.player == null) return;
        if (mc.player.getMainHandItem().getItem() instanceof MaceItem) {
            HasMace = true;
            return;
        }

        var inv = mc.player.getInventory();

        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).getItem() instanceof MaceItem) {
                previousSlot = inv.selected;
                inv.selected = i;
                didSwap = true;
                HasMace = true;
                return;
            }
        }
        didSwap = false;
    }

    private void swapBack() {
        if (!didSwap || mc.player == null) return;
        didSwap = false;
        HasMace = false;
        mc.player.getInventory().selected = previousSlot;
    }
    public static boolean enabled() {
        return ModuleManager.get().getByName("Criticals")
                .map(Module::isActive).orElse(false);
    }
    @Override
    public void onDeactivate() {
        swapBack();
    }
}
