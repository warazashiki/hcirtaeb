package c.e.beatrich.gui.widget;

import c.e.beatrich.gui.screen.BlockListEditScreen;
import c.e.beatrich.gui.theme.Theme;
import c.e.beatrich.setting.types.BlockListSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;

/**
 * 方块列表设置组件 — 点击打开独立编辑页面。
 */
public class BlockListSettingWidget extends Widget {

    private static final int PAD_LEFT = 1;

    private final BlockListSetting setting;
    private final Font font;
    private boolean hovered;

    public BlockListSettingWidget(BlockListSetting setting, Font font, int x, int y, int width) {
        super(x, y, width, Theme.SETTING_HEIGHT);
        this.setting = setting;
        this.font = font;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        hovered = isMouseOver(mouseX, mouseY);
        int textY = y + (height - font.lineHeight) / 2;

        Color bg = hovered ? Theme.MODULE_BG_HOVER : Theme.SETTING_BG;
        g.fill(x, y, x + width, y + height, bg.getRGB());

        g.drawString(font, Component.literal(setting.name), x + PAD_LEFT, textY, Theme.TEXT_SECONDARY.getRGB());

        String count = "[" + setting.get().size() + "]";
        g.drawString(font, Component.literal(count),
                x + width - font.width(count) - 1, textY, Theme.TEXT_PRIMARY.getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || button != 0) return false;
        if (isMouseOver(mouseX, mouseY)) {
            Minecraft.getInstance().setScreen(new BlockListEditScreen(setting));
            return true;
        }
        return false;
    }
}
