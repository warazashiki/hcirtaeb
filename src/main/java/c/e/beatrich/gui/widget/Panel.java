package c.e.beatrich.gui.widget;

import c.e.beatrich.gui.theme.Theme;
import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 可拖拽浮动分类窗口 — Meteor 风格。
 * <p>
 * 每个 Category 对应一个 Panel。<br>
 * 特性：<br>
 * - 紫色标题栏（{@link Theme#ACCENT}），白色标题文字<br>
 * - 标题栏右侧折叠按钮（点击折叠为仅标题栏）<br>
 * - 内容区：模块按钮垂直列表，内容溢出时显示滚动条<br>
 * - 拖拽标题栏移动窗口位置<br>
 * - 2px 黑色外边框（Meteor 标志性风格）
 */
public class Panel extends Widget {

    private static final int COLLAPSE_BUTTON_WIDTH = 10;

    private final Category category;
    private final String title;
    private final Font font;
    private final List<ModuleButton> moduleButtons = new ArrayList<>();

    private boolean collapsed = false;
    private float collapseAnim = 0f;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;
    private boolean collapseHovered = false;
    private int contentHeight = 0;
    private int panelWidth;

    public Panel(Category category, Font font, int x, int y) {
        super(x, y, 0, 0);
        this.category = category;
        this.title = category.name;
        this.font = font;

        // 计算面板宽度：基于最长模块名
        List<Module> modules = c.e.beatrich.module.ModuleManager.get().getByCategory(category);
        int maxModuleNameWidth = 0;
        for (Module m : modules) {
            int w = font.width(m.name);
            if (w > maxModuleNameWidth) maxModuleNameWidth = w;
        }
        // 宽度 = 最长模块名 + 左侧缩进 + 左侧条 + 右侧箭头空间 + 边框
        this.panelWidth = Math.clamp(maxModuleNameWidth + 8, Theme.MIN_PANEL_WIDTH, Theme.MAX_PANEL_WIDTH);
        this.width = panelWidth;

        // 创建模块按钮
        int by = Theme.TITLE_HEIGHT;
        for (Module m : modules) {
            ModuleButton btn = new ModuleButton(m, font, Theme.PANEL_PADDING, by, panelWidth);
            moduleButtons.add(btn);
            by += btn.height;
            // 注意：展开高度动态计算，不在此处累加
        }

        recomputeHeight();
        this.collapseAnim = collapsed ? 0f : 1f;
    }

    // ======================== 公共接口 ========================

    public Category getCategory() {
        return category;
    }

    /** 返回面板标题（分类名），用于配置持久化。 */
    public String getTitle() {
        return title;
    }

    /** 获取面板总自然高度（标题 + 内容） */
    private void recomputeHeight() {
        int h = Theme.TITLE_HEIGHT;
        for (ModuleButton btn : moduleButtons) {
            if (!btn.visible) continue;
            h += btn.height;
            if (btn.isExpanded()) {
                h += btn.getExpandedHeight();
            }
        }
        this.contentHeight = h - Theme.TITLE_HEIGHT;
        this.height = h;
    }

    private int getRenderHeight() {
        if (collapsed || contentHeight <= 0) return Theme.TITLE_HEIGHT;
        return Theme.TITLE_HEIGHT + contentHeight;
    }

    /** 收起此 panel 中所有展开的 module（当另一个 panel 的模块被展开时调用） */
    public void collapseOtherExpansions(ModuleButton except) {
        for (ModuleButton btn : moduleButtons) {
            if (btn != except && btn.isExpanded()) {
                btn.setExpanded(false);
            }
        }
    }

    // ======================== 渲染 ========================

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        // 更新动画
        collapseAnim = Theme.animate(collapseAnim, !collapsed, delta, Theme.ANIM_SPEED);
        recomputeHeight();

        int renderH = getRenderHeight();
        int contentY = y + Theme.TITLE_HEIGHT;

        // === 面板背景 + 外边框 ===
        Theme.renderBackground(g, x, y, panelWidth, renderH, Theme.BACKGROUND, Theme.OUTLINE);

        // === 标题栏 ===
        boolean titleHovered = mouseX >= x && mouseX < x + panelWidth
                && mouseY >= y && mouseY < y + Theme.TITLE_HEIGHT;
        Color titleBg = titleHovered ? Theme.ACCENT_HOVER : Theme.ACCENT;
        g.fill(x, y, x + panelWidth, y + Theme.TITLE_HEIGHT, titleBg.getRGB());

        // 标题文字
        int titleTextY = y + (Theme.TITLE_HEIGHT - font.lineHeight) / 2;
        g.drawString(font, Component.literal(title), x + 1, titleTextY, Theme.TEXT_TITLE.getRGB());

        // 折叠按钮（标题栏右侧，"-" 或 "+"）
        int btnX = x + panelWidth - COLLAPSE_BUTTON_WIDTH - 1;
        collapseHovered = mouseX >= btnX - 2 && mouseX < btnX + COLLAPSE_BUTTON_WIDTH + 2
                && mouseY >= y && mouseY < y + Theme.TITLE_HEIGHT;
        String collapseLabel = collapsed ? "+" : "-";
        Color collapseColor = collapseHovered ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY;
        g.drawString(font, Component.literal(collapseLabel), btnX, titleTextY, collapseColor.getRGB());

        // === 内容区 ===
        if (collapseAnim > 0.001f) {
            if (contentHeight <= 0) return;

            int renderY = contentY;
            for (ModuleButton btn : moduleButtons) {
                if (!btn.visible) continue;
                int btnH = btn.height;
                if (btn.isExpanded()) btnH += btn.getExpandedHeight();
                btn.setPosition(x, renderY);
                btn.render(g, mouseX, mouseY, delta);
                renderY += btnH;
            }
        }
    }

    // ======================== 交互 ========================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;

        int renderH = getRenderHeight();

        // 标题栏点击
        if (mouseX >= x && mouseX < x + panelWidth
                && mouseY >= y && mouseY < y + Theme.TITLE_HEIGHT) {
            // 折叠按钮
            if (collapseHovered && button == GLFW.GLFW_MOUSE_BUTTON_1) {
                collapsed = !collapsed;
                return true;
            }
            // 开始拖拽
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                dragging = true;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
                return true;
            }
            return false;
        }

        // 面板外点击
        if (mouseX < x || mouseX >= x + panelWidth || mouseY < y || mouseY >= y + renderH) {
            return false;
        }

        if (collapsed) return false;

        for (ModuleButton btn : moduleButtons) {
            if (btn.mouseClicked(mouseX, mouseY, button)) {
                // 如果展开了这个模块，收起其他 panel 的展开
                if (btn.isExpanded()) {
                    // 这个通知由 ClickGuiScreen 处理
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            dragging = false;
            return true;
        }

        for (ModuleButton btn : moduleButtons) {
            if (btn.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!visible || collapsed) return false;

        int renderH = getRenderHeight();
        if (mouseX < x || mouseX >= x + panelWidth || mouseY < y || mouseY >= y + renderH) {
            return false;
        }

        for (ModuleButton btn : moduleButtons) {
            if (btn.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleButton btn : moduleButtons) {
            if (btn.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    /** 字符输入转发给模块按钮 */
    public boolean charTyped(char codePoint, int modifiers) {
        for (ModuleButton btn : moduleButtons) {
            if (btn.charTyped(codePoint, modifiers)) return true;
        }
        return false;
    }

    // ======================== 拖拽更新 ========================

    /**
     * 拖拽中更新位置 — 由 ClickGuiScreen.render 在拖拽期间持续调用。
     */
    public void updateDrag(int mouseX, int mouseY) {
        if (!dragging) return;
        this.x = mouseX - dragOffsetX;
        this.y = mouseY - dragOffsetY;
        // 限制不要拖出屏幕
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() != null) {
            int screenW = mc.getWindow().getGuiScaledWidth();
            int screenH = mc.getWindow().getGuiScaledHeight();
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x + panelWidth > screenW) x = screenW - panelWidth;
            if (y + 10 > screenH) y = screenH - 10; // 至少留标题栏可见
        }
    }

    public boolean isDragging() {
        return dragging;
    }

    // ======================== 辅助 ========================

    /** 收起所有展开的模块 */
    public void collapseAllModules() {
        for (ModuleButton btn : moduleButtons) {
            btn.setExpanded(false);
        }
    }
}
