package c.e.beatrich.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;

public class EntityUtils {
    private static final Minecraft mc = Minecraft.getInstance();
    public static void SwapUse(int i) {
        Inventory inv = mc.player.getInventory();
        int invs = inv.selected;
        if (i == invs) {
            mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
        } else if (i < 9) {
            inv.selected = i;
            mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            inv.selected = invs;
        } else {
            mc.gameMode.handleInventoryMouseClick(
                    mc.player.inventoryMenu.containerId, i, invs, ClickType.SWAP, mc.player);
            mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            mc.gameMode.handleInventoryMouseClick(
                    mc.player.inventoryMenu.containerId, i, invs, ClickType.SWAP, mc.player);
        }
    }

}
