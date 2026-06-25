package c.e.beatrich.gui.theme;

import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public final class Theme {

    private Theme() {
        // 静态工具类，禁止实例化
    }

    // ========================= 强调色 =========================

    /** 紫色强调色 — 标题栏、滑块手柄、复选框填充、激活条 */
    public static final Color ACCENT = new Color(145, 61, 226, 255);

    /** 强调色 hover 变体 — 比 ACCENT 稍亮 */
    public static final Color ACCENT_HOVER = new Color(160, 80, 240, 255);

    // ========================= 背景 =========================

    /** 面板 / 窗口背景（半透明暗黑，模仿 Meteor 的 backgroundColor） */
    public static final Color BACKGROUND = new Color(20, 20, 20, 200);

    /** 模块行背景 — 普通态 */
    public static final Color MODULE_BG = new Color(25, 25, 30, 200);

    /** 模块行背景 — hover 态 */
    public static final Color MODULE_BG_HOVER = new Color(40, 40, 45, 200);

    /** 模块行背景 — 激活态（浅灰，模仿 Meteor moduleBackground） */
    public static final Color MODULE_ACTIVE_BG = new Color(50, 50, 50, 255);

    /** 设置项缩进背景 */
    public static final Color SETTING_BG = new Color(18, 18, 20, 180);

    // ========================= 边框 =========================

    /** 2px 黑色边框 — Meteor 标志性风格 */
    public static final Color OUTLINE = new Color(0, 0, 0, 255);

    /** 边框 hover 变体 */
    public static final Color OUTLINE_HOVER = new Color(10, 10, 10, 255);

    // ========================= 文字 =========================

    /** 主文字 — 纯白 */
    public static final Color TEXT_PRIMARY = new Color(255, 255, 255, 255);

    /** 次文字 — 灰色，用于设置名称、描述 */
    public static final Color TEXT_SECONDARY = new Color(150, 150, 150, 255);

    /** 标题文字 */
    public static final Color TEXT_TITLE = new Color(255, 255, 255, 255);

    /** 激活模块文字 */
    public static final Color TEXT_ACTIVE = new Color(255, 255, 255, 255);

    /** 非激活模块文字 */
    public static final Color TEXT_INACTIVE = new Color(180, 180, 180, 255);

    // ========================= 功能色 =========================

    /** 红色 — 关闭/减号/布尔 false */
    public static final Color RED = new Color(255, 50, 50, 255);

    /** 绿色 — 开启/加号/布尔 true */
    public static final Color GREEN = new Color(50, 255, 50, 255);

    /** 金色 — 收藏（预留） */
    public static final Color GOLD = new Color(250, 215, 0, 255);

    // ========================= 复选框 =========================

    /** 复选框填充（勾选时） */
    public static final Color CHECKBOX_FILL = new Color(145, 61, 226, 255);

    // ========================= 滚动条 =========================

    /** 滚动条轨道背景 */
    public static final Color SCROLLBAR_BG = new Color(15, 15, 15, 150);

    /** 滚动条手柄 */
    public static final Color SCROLLBAR_HANDLE = new Color(60, 60, 60, 200);

    /** 滚动条手柄 hover */
    public static final Color SCROLLBAR_HANDLE_HOVER = new Color(80, 80, 80, 220);

    // ========================= 布局常量 =========================

    /** 标题栏高度 */
    public static final int TITLE_HEIGHT = 14;

    /** 模块行高度 */
    public static final int MODULE_HEIGHT = 13;

    /** 设置项行高度 */
    public static final int SETTING_HEIGHT = 13;

    /** 面板内边距 */
    public static final int PANEL_PADDING = 0;

    /** 面板最小宽度 */
    public static final int MIN_PANEL_WIDTH = 85;

    /** 面板最大宽度 */
    public static final int MAX_PANEL_WIDTH = 120;

    /** 滚动条宽度 */
    public static final int SCROLLBAR_WIDTH = 4;

    /** 动画速度 — 通用 */
    public static final float ANIM_SPEED = 14.0f;

    // ========================= 三态颜色 =========================

    /**
     * 三态颜色容器 — 模仿 Meteor 的 ThreeStateColorSetting 概念。
     * 用于背景、边框等在 normal / hovered / pressed 三种状态下需要不同颜色的组件。
     */
    public static class ThreeStateColor {
        public final Color normal;
        public final Color hovered;
        public final Color pressed;

        public ThreeStateColor(Color normal, Color hovered, Color pressed) {
            this.normal = normal;
            this.hovered = hovered;
            this.pressed = pressed;
        }

        /**
         * 根据状态获取颜色。
         *
         * @param isPressed 是否被按下
         * @param isHovered 是否被悬停
         * @return 对应的颜色
         */
        public Color get(boolean isPressed, boolean isHovered) {
            if (isPressed) return pressed;
            if (isHovered) return hovered;
            return normal;
        }

        /** 仅区分 hover / normal */
        public Color get(boolean isHovered) {
            return get(false, isHovered);
        }
    }

    // ========================= 预定义三态颜色 =========================

    /** 模块按钮背景三态 */
    public static final ThreeStateColor MODULE_BG_STATE = new ThreeStateColor(
            MODULE_BG, MODULE_BG_HOVER, MODULE_ACTIVE_BG
    );

    /** 滚动条手柄三态 */
    public static final ThreeStateColor SCROLLBAR_HANDLE_STATE = new ThreeStateColor(
            SCROLLBAR_HANDLE, SCROLLBAR_HANDLE_HOVER, SCROLLBAR_HANDLE_HOVER
    );

    // ========================= 渲染辅助 =========================

    /**
     * 渲染带 2px 黑色边框的背景矩形 — Meteor 风格标准组件外观。
     * <p>
     * 外层 2px 黑色边框 + 内层填充色（向内缩 2px）。
     *
     * @param g      GuiGraphics
     * @param x      左上角 X
     * @param y      左上角 Y
     * @param w      宽度
     * @param h      高度
     * @param fill   填充颜色
     * @param outline 边框颜色
     */
    public static void renderBackground(GuiGraphics g, int x, int y, int w, int h, Color fill, Color outline) {
        // 填充整个区域作为边框基底
        g.fill(x, y, x + w, y + h, outline.getRGB());
        // 内部填充（2px 内缩）
        g.fill(x + 2, y + 2, x + w - 2, y + h - 2, fill.getRGB());
    }

    /**
     * 渲染 2px 黑色边框（不含填充背景，仅四周边框）。
     */
    public static void renderOutline(GuiGraphics g, int x, int y, int w, int h) {
        renderOutline(g, x, y, w, h, OUTLINE);
    }

    /**
     * 渲染指定颜色的 2px 边框。
     */
    public static void renderOutline(GuiGraphics g, int x, int y, int w, int h, Color color) {
        int argb = color.getRGB();
        // 上
        g.fill(x, y, x + w, y + 2, argb);
        // 下
        g.fill(x, y + h - 2, x + w, y + h, argb);
        // 左
        g.fill(x, y + 2, x + 2, y + h - 2, argb);
        // 右
        g.fill(x + w - 2, y + 2, x + w, y + h - 2, argb);
    }

    /**
     * 线性插值颜色（ARGB 各通道分别插值）。
     */
    public static Color lerpColor(Color from, Color to, float progress) {
        int a = (int) (from.getAlpha() + (to.getAlpha() - from.getAlpha()) * progress);
        int r = (int) (from.getRed() + (to.getRed() - from.getRed()) * progress);
        int g = (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * progress);
        int b = (int) (from.getBlue() + (to.getBlue() - from.getBlue()) * progress);
        return new Color(
                Math.clamp(r, 0, 255),
                Math.clamp(g, 0, 255),
                Math.clamp(b, 0, 255),
                Math.clamp(a, 0, 255)
        );
    }

    /**
     * 平滑动画步进 — 模仿 Meteor 的动画模式。
     *
     * @param current 当前进度
     * @param target  目标值（true → 1, false → 0）
     * @param delta   帧时间（秒）
     * @param speed   动画速度（Meteor 常用 4、6、14）
     * @return 更新后的进度值 [0, 1]
     */
    public static float animate(float current, boolean target, float delta, float speed) {
        current += (target ? 1 : -1) * delta * speed;
        return Math.clamp(current, 0f, 1f);
    }

    /**
     * 带明确目标值的动画步进。
     *
     * @param current 当前进度
     * @param target  目标值
     * @param delta   帧时间（秒）
     * @param speed   动画速度
     * @return 更新后的进度值，向 target 收敛
     */
    public static float animate(float current, float target, float delta, float speed) {
        if (Math.abs(current - target) < 0.001f) return target;
        current += (target > current ? 1 : -1) * delta * speed;
        if (Math.abs(current - target) < 0.001f) return target;
        return Math.clamp(current, 0f, 1f);
    }
}
