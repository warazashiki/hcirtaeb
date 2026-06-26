package c.e.beatrich.module.modules.movement;

import c.e.beatrich.event.events.PacketEvent;
import c.e.beatrich.mixin.ServerboundMovePlayerPacketAccessor;
import c.e.beatrich.mixininterface.IServerboundMovePlayerPacket;
import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import c.e.beatrich.setting.types.BoolSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFall extends Module {
    public static BoolSetting NoBounce = new BoolSetting("NoBounce", "防止弹跳", false);
    public NoFall() {
        super("NoFall", "防摔伤", Category.MOVEMENT);
        addSetting(NoBounce);
    }
    public static boolean enabled() {
        return ModuleManager.get().getByName("NoFall")
                .map(Module::isActive).orElse(false);
    }
    @Override
    public void onPacketSend(PacketEvent event) {
        if (mc.player != null) {
            if (mc.player.getAbilities().instabuild
                    || !(event.packet instanceof ServerboundMovePlayerPacket)
                    || ((IServerboundMovePlayerPacket) event.packet).hcirtaeb$getTag() == 1337) return;
            if (!Flight.enabled()) {
                if (mc.player.isFallFlying()) return;
                if (mc.player.getDeltaMovement().y > -0.5) return;
                ((ServerboundMovePlayerPacketAccessor) event.packet).setOnGround(true);
            } else {
                ((ServerboundMovePlayerPacketAccessor) event.packet).setOnGround(true);
            }
        }
    }
}
