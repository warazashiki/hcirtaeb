package c.e.beatrich.gui.widget;

import c.e.beatrich.gui.theme.Theme;
import c.e.beatrich.setting.Setting;
import c.e.beatrich.setting.types.DoubleSetting;
import c.e.beatrich.setting.types.IntSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

/**
 * 文本输入设置组件 — 直接输入数值而非滑块。
 * <p>
 * 左侧：设置名称标签（灰色）<br>
 * 右侧：数值文本（带浅色背景框），点击后进入编辑模式<br>
 * 编辑模式：捕获键盘输入，Enter 确认，Esc 取消，Backspace 删除<br>
 * 点击组件外部自动确认并退出编辑
 * </p>
 */
public class TextInputWidget extends Widget {

    private static final int NAME_MAX_WIDTH = 0;
    private static final int PAD_LEFT = 1;
    private static final int PAD_RIGHT = 1;
    private static final int VALUE_BOX_MIN_WIDTH = 30;

    private final Setting<?> setting;
    private final Font font;
    private final boolean isDouble;

    /** 是否处于编辑模式 */
    private boolean editing = false;
    /** 编辑中的文本缓冲区 */
    private String editBuffer = "";
    /** 光标闪烁计时器 */
    private float cursorTimer = 0f;
    private boolean hovered = false;

    // 值区域布局（每帧计算）
    private int valueBoxX, valueBoxW;

    public TextInputWidget(Setting<?> setting, Font font, int x, int y, int width) {
        super(x, y, width, Theme.SETTING_HEIGHT);
        this.setting = setting;
        this.font = font;
        this.isDouble = setting instanceof DoubleSetting;
    }

    // ======================== 渲染 ========================

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        hovered = isMouseOver(mouseX, mouseY);
        cursorTimer += delta;

        int textY = y + (height - font.lineHeight) / 2;

        // === 名称标签 ===
        int nameX = x + PAD_LEFT;
        String name = truncateName(setting.name, NAME_MAX_WIDTH);
        g.drawString(font, Component.literal(name), nameX, textY, Theme.TEXT_SECONDARY.getRGB());

        // === 值区域 ===
        String displayText = editing ? editBuffer : formatValue();
        int valTextW = font.width(displayText.isEmpty() ? "0.0" : displayText);
        valueBoxW = Math.max(valTextW + 2, VALUE_BOX_MIN_WIDTH);
        valueBoxX = x + width - valueBoxW - PAD_RIGHT;

        // 值背景框
        Color boxBg = editing
                ? new Color(40, 40, 50, 200)
                : (hovered ? new Color(35, 35, 40, 200) : new Color(25, 25, 30, 160));
        g.fill(valueBoxX, y, valueBoxX + valueBoxW, y + height, boxBg.getRGB());

        // 2px 边框（浅色，区别于组件外框）
        Color boxOutline = editing ? Theme.ACCENT : (hovered ? Theme.TEXT_SECONDARY : Theme.OUTLINE);
        Theme.renderOutline(g, valueBoxX, y, valueBoxW, height, boxOutline);

        // 值文本
        int valTextX = valueBoxX + 1;
        Color valColor = editing ? Theme.TEXT_PRIMARY : (hovered ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);
        g.drawString(font, Component.literal(displayText), valTextX, textY, valColor.getRGB());

        // 编辑模式光标
        if (editing) {
            int cursorX = valTextX + font.width(editBuffer);
            boolean showCursor = ((int) (cursorTimer * 1.75f)) % 2 == 0;
            if (showCursor) {
                g.fill(cursorX, textY, cursorX + 1, textY + font.lineHeight, Theme.TEXT_PRIMARY.getRGB());
            }
        }
    }

    // ======================== 鼠标 ========================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || button != 0) return false;

        boolean inValueBox = mouseX >= valueBoxX && mouseX < valueBoxX + valueBoxW
                && mouseY >= y && mouseY < y + height;

        if (inValueBox) {
            // 点击值区域 → 进入编辑模式
            if (!editing) {
                editing = true;
                editBuffer = formatValue();
                cursorTimer = 0;
                // 全选效果：直接清空 buffer 方便输入新值
            }
            return true;
        }

        // 点击名称区域或其他地方 → 确认并退出编辑
        if (editing) {
            commitEdit();
        }
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    // ======================== 键盘 ========================

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!visible || !editing) return false;

        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                commitEdit();
                return true;

            case GLFW.GLFW_KEY_ESCAPE:
                cancelEdit();
                return true;

            case GLFW.GLFW_KEY_BACKSPACE:
                if (!editBuffer.isEmpty()) {
                    editBuffer = editBuffer.substring(0, editBuffer.length() - 1);
                }
                cursorTimer = 0;
                return true;

            case GLFW.GLFW_KEY_DELETE:
                editBuffer = "";
                cursorTimer = 0;
                return true;

            case GLFW.GLFW_KEY_LEFT:
            case GLFW.GLFW_KEY_RIGHT:
                // 忽略方向键（不移动光标，简化实现）
                return true;

            default:
                return false;
        }
    }

    /**
     * 字符输入 — 由上级容器的 charTyped 转发。
     */
    public boolean charTyped(char codePoint, int modifiers) {
        if (!visible || !editing) return false;

        // 允许数字、负号、小数点
        if (codePoint >= '0' && codePoint <= '9') {
            editBuffer += codePoint;
            cursorTimer = 0;
            return true;
        }

        if (codePoint == '-' && editBuffer.isEmpty()) {
            editBuffer += '-';
            cursorTimer = 0;
            return true;
        }

        if (codePoint == '.' && isDouble && !editBuffer.contains(".")) {
            if (editBuffer.isEmpty() || editBuffer.equals("-")) {
                editBuffer += "0.";
            } else {
                editBuffer += '.';
            }
            cursorTimer = 0;
            return true;
        }

        return false;
    }

    // ======================== 聚焦管理 ========================

    public boolean isEditing() {
        return editing;
    }

    /**
     * 强制退出编辑模式（不提交） — 由外部调用。
     */
    public void loseFocus() {
        if (editing) {
            commitEdit();
        }
    }

    // ======================== 辅助 ========================

    private void commitEdit() {
        if (!editing) return;
        editing = false;
        try {
            String trimmed = editBuffer.trim();
            if (trimmed.isEmpty() || trimmed.equals("-")) {
                // 清空 → 恢复默认值
                return;
            }
            if (isDouble) {
                double val = Double.parseDouble(trimmed);
                ((DoubleSetting) setting).set(val);
            } else {
                int val = Integer.parseInt(trimmed);
                ((IntSetting) setting).set(val);
            }
        } catch (NumberFormatException e) {
            // 无效输入 → 恢复原值（不修改 editBuffer，下次点击仍显示原值）
        }
        editBuffer = "";
    }

    private void cancelEdit() {
        editing = false;
        editBuffer = "";
    }

    private String formatValue() {
        if (isDouble) {
            return String.format("%.2f", ((DoubleSetting) setting).get());
        }
        return String.valueOf(((IntSetting) setting).get());
    }

    private String truncateName(String name, int maxWidth) {
        if (maxWidth <= 0 || font.width(name) <= maxWidth) return name;
        String trimmed = name;
        while (font.width(trimmed + "...") > maxWidth && trimmed.length() > 1) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed + "...";
    }
}
