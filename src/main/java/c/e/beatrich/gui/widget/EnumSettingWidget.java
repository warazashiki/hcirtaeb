package c.e.beatrich.gui.widget;

import c.e.beatrich.gui.theme.Theme;
import c.e.beatrich.setting.types.EnumSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;

/**
 * 枚举设置组件 — 右键展开下拉列表选择。
 */
public class EnumSettingWidget extends Widget {

    private static final int PAD_LEFT = 1;
    private static final int DROP_H = Theme.SETTING_HEIGHT;

    private final EnumSetting<?> setting;
    private final Font font;
    private boolean hovered;
    private boolean expanded;
    private int hoveredOption = -1;
    private final int closedH = Theme.SETTING_HEIGHT;
    private final int dropLen;

    public EnumSettingWidget(EnumSetting<?> setting, Font font, int x, int y, int width) {
        super(x, y, width, Theme.SETTING_HEIGHT);
        this.setting = setting;
        this.font = font;
        this.dropLen = setting.getValues().length;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (!visible) return;
        hovered = isMouseOver(mouseX, mouseY);
        int textY = y + (closedH - font.lineHeight) / 2;

        // 行背景（仅标题行，不包含下拉区域）
        Color bg = expanded ? Theme.MODULE_ACTIVE_BG
                : (hovered ? Theme.MODULE_BG_HOVER : Theme.SETTING_BG);
        g.fill(x, y, x + width, y + closedH, bg.getRGB());

        // 名称
        g.drawString(font, Component.literal(setting.description), x + PAD_LEFT, textY, Theme.TEXT_SECONDARY.getRGB());
        if (expanded) {
            Object[] values = setting.getValues();
            int dropY = y + closedH;
            hoveredOption = -1;
            for (int i = 0; i < values.length; i++) {
                int optY = dropY + i * DROP_H;
                boolean optHovered = mouseX >= x && mouseX < x + width
                        && mouseY >= optY && mouseY < optY + DROP_H;
                if (optHovered) hoveredOption = i;

                Color optBg = values[i].equals(setting.get())
                        ? Theme.ACCENT : (optHovered ? Theme.MODULE_BG_HOVER : Theme.MODULE_BG);
                g.fill(x, optY, x + width, optY + DROP_H, optBg.getRGB());
                g.drawString(font, Component.literal(values[i].toString()),
                        x + PAD_LEFT + 3, optY + (DROP_H - font.lineHeight) / 2,
                        optHovered ? Theme.TEXT_PRIMARY.getRGB() : Theme.TEXT_SECONDARY.getRGB());
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;

        // 下拉列表中的点击
        if (expanded && hoveredOption >= 0 && button == 0) {
            Object[] values = setting.getValues();
            if (hoveredOption < values.length) {
                setting.setRaw(hoveredOption);
            }
            expanded = false;
            height = closedH;
            return true;
        }

        if (!isMouseOver(mouseX, mouseY)) {
            if (expanded) { expanded = false; height = closedH; return true; }
            return false;
        }

        if (button == 0 || button == 1) {
            expanded = !expanded;
            height = expanded ? closedH + dropLen * DROP_H : closedH;
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mx, double my) {
        if (mx >= x && mx < x + width && my >= y && my < y + height) return true;
        if (expanded) {
            int dropLen = setting.getValues().length;
            int dropY = y + height;
            return mx >= x && mx < x + width && my >= dropY && my < dropY + dropLen * DROP_H;
        }
        return false;
    }

    @Override
    public void loseFocus() {
        expanded = false;
        height = closedH;
        hoveredOption = -1;
    }
}
