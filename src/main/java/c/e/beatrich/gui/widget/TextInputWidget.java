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

public class TextInputWidget extends Widget {

    private static final int NAME_MAX_WIDTH = 0;
    private static final int PAD_LEFT = 1;
    private static final int PAD_RIGHT = 1;
    private static final int VALUE_BOX_MIN_WIDTH = 30;

    private final Setting<?> setting;
    private final Font font;
    private final boolean isDouble;

    private boolean editing = false;
    private String editBuffer = "";
    private float cursorTimer = 0f;
    private boolean hovered = false;

    private int valueBoxX, valueBoxW;

    public TextInputWidget(Setting<?> setting, Font font, int x, int y, int width) {
        super(x, y, width, Theme.SETTING_HEIGHT);
        this.setting = setting;
        this.font = font;
        this.isDouble = setting instanceof DoubleSetting;
    }
    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        hovered = isMouseOver(mouseX, mouseY);
        cursorTimer += delta;

        int textY = y + (height - font.lineHeight) / 2;

        int nameX = x + PAD_LEFT;
        String name = truncateName(setting.description, NAME_MAX_WIDTH);
        g.drawString(font, Component.literal(name), nameX, textY, Theme.TEXT_SECONDARY.getRGB());

        String displayText = editing ? editBuffer : formatValue();
        int valTextW = font.width(displayText.isEmpty() ? "0.0" : displayText);
        valueBoxW = Math.max(valTextW + 2, VALUE_BOX_MIN_WIDTH);
        valueBoxX = x + width - valueBoxW - PAD_RIGHT;

        Color boxBg = editing
                ? new Color(40, 40, 50, 200)
                : (hovered ? new Color(35, 35, 40, 200) : new Color(25, 25, 30, 160));
        g.fill(valueBoxX, y, valueBoxX + valueBoxW, y + height, boxBg.getRGB());

        Color boxOutline = editing ? Theme.ACCENT : (hovered ? Theme.TEXT_SECONDARY : Theme.OUTLINE);
        Theme.renderOutline(g, valueBoxX, y, valueBoxW, height, boxOutline);

        int valTextX = valueBoxX + 1;
        Color valColor = editing ? Theme.TEXT_PRIMARY : (hovered ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);
        g.drawString(font, Component.literal(displayText), valTextX, textY, valColor.getRGB());

        if (editing) {
            int cursorX = valTextX + font.width(editBuffer);
            boolean showCursor = ((int) (cursorTimer * 1.75f)) % 2 == 0;
            if (showCursor) {
                g.fill(cursorX, textY, cursorX + 1, textY + font.lineHeight, Theme.TEXT_PRIMARY.getRGB());
            }
        }
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || button != 0) return false;
        boolean inValueBox = mouseX >= valueBoxX && mouseX < valueBoxX + valueBoxW && mouseY >= y && mouseY < y + height;
        if (inValueBox) {
            if (!editing) {
                editing = true;
                editBuffer = formatValue();
                cursorTimer = 0;
            }
            return true;
        }
        if (editing) commitEdit();
        return isMouseOver(mouseX, mouseY);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
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
                if (!editBuffer.isEmpty()) editBuffer = editBuffer.substring(0, editBuffer.length() - 1);
                cursorTimer = 0;
                return true;
            case GLFW.GLFW_KEY_DELETE:
                editBuffer = "";
                cursorTimer = 0;
                return true;
            case GLFW.GLFW_KEY_LEFT:
            case GLFW.GLFW_KEY_RIGHT:
                return true;
            default:
                return false;
        }
    }
    public boolean charTyped(char codePoint, int modifiers) {
        if (!visible || !editing) return false;
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
            if (editBuffer.isEmpty() || editBuffer.equals("-")) editBuffer += "0.";
            else editBuffer += '.';
            cursorTimer = 0;
            return true;
        }
        return false;
    }
    public void loseFocus() {
        if (editing) commitEdit();
    }
    private void commitEdit() {
        if (!editing) return;
        editing = false;
        try {
            String trimmed = editBuffer.trim();
            if (trimmed.isEmpty() || trimmed.equals("-")) return;
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
        if (isDouble) return String.format("%.2f", ((DoubleSetting) setting).get());
        return String.valueOf(((IntSetting) setting).get());
    }
    private String truncateName(String name, int maxWidth) {
        if (maxWidth <= 0 || font.width(name) <= maxWidth) return name;
        String trimmed = name;
        while (font.width(trimmed + "...") > maxWidth && trimmed.length() > 1) trimmed = trimmed.substring(0, trimmed.length() - 1);
        return trimmed + "...";
    }
}
