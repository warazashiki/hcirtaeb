package c.e.beatrich.module.modules.player;

import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.setting.types.BoolSetting;
import c.e.beatrich.utils.EntityUtils;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class Fireworks extends Module {
    public final BoolSetting autolaunch = boolSetting("autolaunch", "自动起飞", false);
    private boolean ac = false;
    private static int FSlot = -1;
    private static int ESlot = -1;
    private static int t = -1;

    public Fireworks() {
        super("Fireworks", "烟花", Category.PLAYER, false);
    }

    @Override
    public void onTick(ClientTickEvent.Post event) {
        if (mc.player == null) return;
        if (ac) {
            switch (t) {
                case 0:
                    EntityUtils.SwapUse(ESlot);
                    t = mc.player.onGround() ? 1 : 2;
                    break;
                case 1:
                    mc.player.jumpFromGround();
                    t = 2;
                    break;
                case 2:
                    if (!mc.player.onGround()) {
                        mc.player.connection.send(new ServerboundPlayerCommandPacket(
                                mc.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
                        t = 3;
                    } else {
                        t = 1;
                    }
                    break;
                case 3:
                    EntityUtils.SwapUse(FSlot);
                    t = 4;
                default:
                    deactivate();
                    break;
            }
        } else {
            if (isActive()) deactivate();
        }
    }
    private int getFSlot(Inventory inv) {
        int i = -1;
        for (int j = 0; j < 36; j++) {
            if (inv.getItem(j).getItem() instanceof FireworkRocketItem) {
                i = j;
                break;
            }
        }
        return i;
    }
    private int getESlot(Inventory inv) {
        int i = -1;
        if (inv.getItem(38).getItem() instanceof ElytraItem) {
            i = 38;
        } else {
            for (int j = 0; j < 36; j++) {
                if (inv.getItem(j).getItem() instanceof ElytraItem) {
                    i = j;
                    break;
                }
            }
        }
        return i;
    }
    @Override
    public void onActivate() {
        if (mc.player == null) {
            deactivate();
            return;
        }
        Inventory inv = mc.player.getInventory();
        ac = true;
        if (mc.player.isFallFlying()) {
            FSlot = getFSlot(inv);
            if (-1 == FSlot) deactivate();
            else t = 3;
        } else if (autolaunch.get()) {
            ESlot = getESlot(inv);
            FSlot = getFSlot(inv);
            if (-1 == ESlot || -1 == FSlot) deactivate();
            else t = 38 == ESlot ? 1 : 0;
        } else {
            deactivate();
        }
    }

    @Override
    public void onDeactivate() {
        ac = false;
        FSlot = -1;
        ESlot = -1;
        t = -1;
    }
}