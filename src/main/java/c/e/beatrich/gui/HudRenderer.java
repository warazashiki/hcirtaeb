package c.e.beatrich.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * HUD 渲染器 — 模仿 Meteor Client 的 HUD。
 * <p>
 * 由 {@link c.e.beatrich.mixin.GuiMixin} 在每帧渲染后调用。<br>
 * </p>
 */
public class HudRenderer {

    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * 在屏幕上绘制 HUD（由 mixin 在渲染时调用）。
     *
     * @param guiGraphics GuiGraphics
     * @param deltaTracker 帧时间追踪器
     */
    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (mc.options.hideGui) return;

        Font font = mc.font;
        if (font == null) return;
    }
}
