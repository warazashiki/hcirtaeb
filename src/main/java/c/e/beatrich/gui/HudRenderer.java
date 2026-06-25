package c.e.beatrich.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class HudRenderer {

    private static final Minecraft mc = Minecraft.getInstance();

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (mc.options.hideGui) return;

        Font font = mc.font;
        if (font == null) return;
    }
}
