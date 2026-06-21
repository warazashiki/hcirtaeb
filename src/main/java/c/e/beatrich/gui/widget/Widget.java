package c.e.beatrich.gui.widget;

import net.minecraft.client.gui.GuiGraphics;

/**
 * ClickGUI 组件基类 — 模仿 Meteor Client 的 WWidget。
 * <p>
 * 每个 Widget 持有一个边界矩形（{@link #x}, {@link #y}, {@link #width}, {@link #height}）和
 * 可见性标志。子类覆写渲染、鼠标、键盘事件方法来实现具体交互。
 * <p>
 * 坐标系统：所有坐标相对于父容器（Panel 或 Screen）。在 Panel 内部，y 坐标需要加上滚动偏移。
 */
public abstract class Widget {

    /** 组件左上角 X 坐标（父容器空间） */
    public int x;
    /** 组件左上角 Y 坐标（父容器空间） */
    public int y;
    /** 组件宽度 */
    public int width;
    /** 组件高度 */
    public int height;
    /** 是否可见（不可见的组件不渲染也不响应事件） */
    public boolean visible = true;

    protected Widget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * 渲染此组件。
     *
     * @param g      GuiGraphics
     * @param mouseX 鼠标 X（Screen 坐标，需自行转换到组件空间）
     * @param mouseY 鼠标 Y（Screen 坐标）
     * @param delta  帧时间（秒），用于平滑动画
     */
    public abstract void render(GuiGraphics g, int mouseX, int mouseY, float delta);

    /**
     * 鼠标点击事件。
     *
     * @param mouseX Screen 空间鼠标 X
     * @param mouseY Screen 空间鼠标 Y
     * @param button  鼠标按钮（GLFW 常量）
     * @return true 表示事件已消费（阻止向底层组件传递）
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * 鼠标释放事件。
     */
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * 鼠标滚轮事件。
     */
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    /**
     * 键盘按键事件。
     *
     * @return true 表示事件已消费
     */
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    /**
     * 字符输入事件 — 由 Screen.charTyped 转发而来。
     *
     * @return true 表示事件已消费
     */
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    /**
     * 失去键盘焦点 — 点击其他地方时由父容器调用。
     */
    public void loseFocus() {
    }

    /**
     * 判断给定点是否在此组件内部。
     *
     * @param mx 测试点 X
     * @param my 测试点 Y
     * @return true 如果在内部
     */
    public boolean isMouseOver(double mx, double my) {
        return visible && mx >= x && mx < x + width && my >= y && my < y + height;
    }

    /**
     * 更新组件位置。
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 更新组件尺寸。
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
