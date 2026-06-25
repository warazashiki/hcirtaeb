package c.e.beatrich.gui.widget;

import net.minecraft.client.gui.GuiGraphics;

public abstract class Widget {

    public int x;
    public int y;
    public int width;
    public int height;
    public boolean visible = true;

    protected Widget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GuiGraphics g, int mouseX, int mouseY, float delta);
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }
    public void loseFocus() {
    }
    public boolean isMouseOver(double mx, double my) {
        return visible && mx >= x && mx < x + width && my >= y && my < y + height;
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
