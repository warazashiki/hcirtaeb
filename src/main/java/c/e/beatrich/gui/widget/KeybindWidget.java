package c.e.beatrich.gui.widget;

import c.e.beatrich.gui.theme.Theme;
import c.e.beatrich.setting.types.KeybindSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class KeybindWidget extends Widget {

    private static final int PAD_LEFT = 1;
    private static final int PAD_RIGHT = 1;
    private static final int VALUE_BOX_MIN_WIDTH = 40;

    private final KeybindSetting setting;
    private final Font font;

    /** 是否处于监听模式 */
    private boolean listening = false;
    private float pulseTimer = 0f;
    private boolean hovered = false;

    // 值区域布局
    private int valueBoxX, valueBoxW;

    public KeybindWidget(KeybindSetting setting, Font font, int x, int y, int width) {
        super(x, y, width, Theme.SETTING_HEIGHT);
        this.setting = setting;
        this.font = font;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        hovered = isMouseOver(mouseX, mouseY);
        pulseTimer += delta;

        int textY = y + (height - font.lineHeight) / 2;

        // === 名称 ===
        g.drawString(font, Component.literal("KeyBind"), x + PAD_LEFT, textY, Theme.TEXT_SECONDARY.getRGB());

        // === 值区域 ===
        String display;
        if (listening) {
            // 脉冲动画：[...] ↔ [ · ]
            display = ((int) (pulseTimer * 3)) % 2 == 0 ? "[...]" : "[ · ]";
        } else {
            display = KeybindSetting.getKeyName(setting.getKey());
        }

        valueBoxW = Math.max(font.width(display) + 2, VALUE_BOX_MIN_WIDTH);
        valueBoxX = x + width - valueBoxW - PAD_RIGHT;

        // 背景框
        Color boxBg = listening ? new Color(60, 50, 20, 220) // 黄色暗底 — 监听中
                : (hovered ? new Color(35, 35, 40, 200) : new Color(25, 25, 30, 160));
        g.fill(valueBoxX, y, valueBoxX + valueBoxW, y + height, boxBg.getRGB());

        // 边框
        Color boxOutline = listening ? new Color(255, 200, 50) // 金色 — 监听中
                : (hovered ? Theme.ACCENT : Theme.OUTLINE);
        Theme.renderOutline(g, valueBoxX, y, valueBoxW, height, boxOutline);

        // 文本
        Color textColor = listening ? new Color(255, 220, 80)
                : (hovered ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);
        g.drawString(font, Component.literal(display), valueBoxX + 1, textY, textColor.getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;

        // 已在监听模式 → 捕获鼠标按键
        if (listening) {
            setting.setKey(button);
            listening = false;
            return true;
        }

        if (button != 0) return false;

        boolean inBox = mouseX >= valueBoxX && mouseX < valueBoxX + valueBoxW
                && mouseY >= y && mouseY < y + height;

        if (inBox) {
            listening = true;
            pulseTimer = 0;
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!visible || !listening) return false;

        // ESC → 清除快捷键
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            setting.setKey(GLFW.GLFW_KEY_UNKNOWN);
            listening = false;
            return true;
        }

        // 捕获按键
        setting.setKey(keyCode);
        listening = false;
        return true;
    }

    public boolean isListening() {
        return listening;
    }

    @Override
    public void loseFocus() {
        if (listening) {
            listening = false;
        }
    }
}
