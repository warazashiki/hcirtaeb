package c.e.beatrich.gui;

import c.e.beatrich.config.ConfigManager;
import c.e.beatrich.gui.widget.Panel;
import c.e.beatrich.module.Category;
import c.e.beatrich.module.ModuleManager;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Click GUI — Meteor Client 风格的可拖拽浮动窗口式模块管理界面。
 * <p>
 * 特性：<br>
 * - 每个 {@link Category} 对应一个可拖拽的 {@link Panel} 浮动窗口<br>
 * - 紫色标题栏、暗黑半透明背景、2px 黑色边框（Meteor 配色）<br>
 * - 模块行 hover 动画、激活模块左侧紫色竖条动画<br>
 * - 布尔设置 → 动画复选框，数字设置 → 紫色滑块<br>
 * - 左键模块名 → 切换开关，右键模块名 → 展开/折叠设置<br>
 * - 点击面板标题栏可拖拽，点击折叠按钮可收起面板<br>
 * - 内容溢出时显示滚动条<br>
 * - 按右 Shift 打开/关闭<br>
 * - 同时只能展开一个模块的设置（右键展开时自动收起其他）
 * </p>
 */
public class ClickGuiScreen extends Screen {

    /** 面板之间的水平间隔 */
    private static final int PANEL_GAP_X = 3;
    /** 起始 X 偏移 */
    private static final int START_X = 3;
    /** 起始 Y 偏移 */
    private static final int START_Y = 30;
    /** 每行最大面板数 */
    private static final int MAX_PANELS_PER_ROW = 4;

    private final List<Panel> panels = new ArrayList<>();
    private Panel draggedPanel = null;

    private long lastFrameTime;

    public ClickGuiScreen() {
        super(GameNarrator.NO_TITLE);
        initPanels();
        lastFrameTime = System.nanoTime();
    }

    // ======================== 初始化 ========================

    private void initPanels() {
        Minecraft mc = Minecraft.getInstance();

        Category[] categories = Category.values();
        Map<String, int[]> savedPositions = ConfigManager.loadPanelPositions();

        int col = 0;
        int currentX = START_X;
        int currentY = START_Y;

        for (Category cat : categories) {
            List<c.e.beatrich.module.Module> modules = ModuleManager.get().getByCategory(cat);
            if (modules.isEmpty()) continue;

            // 优先使用已保存的位置
            int px = currentX, py = currentY;
            if (savedPositions.containsKey(cat.name)) {
                int[] saved = savedPositions.get(cat.name);
                px = saved[0];
                py = saved[1];
            }

            Panel panel = new Panel(cat, mc.font, px, py);
            panels.add(panel);

            // 仅对未保存的 panel 使用自动布局
            if (!savedPositions.containsKey(cat.name)) {
                currentX += panel.width + PANEL_GAP_X;
                col++;
                if (col >= MAX_PANELS_PER_ROW) {
                    col = 0;
                    currentX = START_X;
                    currentY += 150;
                }
            }
        }
    }

    // ======================== 渲染 ========================

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 半透明背景
        this.renderTransparentBackground(guiGraphics);

        // 计算 delta（帧时间，单位秒）
        long now = System.nanoTime();
        float delta = (now - lastFrameTime) / 1_000_000_000f;
        delta = Math.clamp(delta, 0.0f, 0.1f); // 防止帧率异常
        lastFrameTime = now;

        // 拖拽更新
        if (draggedPanel != null && draggedPanel.isDragging()) {
            draggedPanel.updateDrag(mouseX, mouseY);
        }

        // 渲染所有 panel
        for (Panel panel : panels) {
            panel.render(guiGraphics, mouseX, mouseY, delta);
        }
    }

    // ======================== 鼠标事件 ========================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 从上层到下层分发（后渲染的在上面，所以倒序遍历）
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel panel = panels.get(i);
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                // 将点击的 panel 提到最前面
                panels.remove(i);
                panels.add(panel);

                // 如果是拖拽开始，记录
                if (panel.isDragging()) {
                    draggedPanel = panel;
                }

                // 如果展开了模块，收起其他 panel 的展开
                if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
                    handleExpansion(panel);
                }

                return true;
            }
        }

        // 点击空白区域 → 收起所有展开的模块
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2) {
            collapseAllExpansions();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggedPanel != null) {
            draggedPanel.mouseReleased(mouseX, mouseY, button);
            if (!draggedPanel.isDragging()) {
                draggedPanel = null;
            }
            return true;
        }

        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel panel = panels.get(i);
            if (panel.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // 分发给鼠标所在的 panel（从上层到下层）
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel panel = panels.get(i);
            if (panel.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    // ======================== 键盘事件 ========================

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 先分发给 panel（文本输入控件可能消费 ESC/Enter/Backspace 等）
        for (Panel panel : panels) {
            if (panel.keyPressed(keyCode, scanCode, modifiers)) return true;
        }

        // ESC 关闭（同时停用 Gui 模块）
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // 分发给 panel（文本输入控件需要字符事件）
        for (int i = panels.size() - 1; i >= 0; i--) {
            if (panels.get(i).charTyped(codePoint, modifiers)) return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    // ======================== 其他 Screen 方法 ========================

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        // 保存面板位置
        Map<String, int[]> positions = new LinkedHashMap<>();
        for (Panel panel : panels) {
            positions.put(panel.getTitle(), new int[]{panel.x, panel.y});
        }
        ConfigManager.savePanelPositions(positions);

        super.onClose();
        // 清理状态
        draggedPanel = null;
        collapseAllExpansions();
    }

    // ======================== 辅助 ========================

    /**
     * 处理模块展开互斥逻辑。
     * 当某个 panel 中有模块被右键展开时，收起其他 panel 中已展开的模块。
     */
    private void handleExpansion(Panel clickedPanel) {
        for (Panel panel : panels) {
            if (panel != clickedPanel) {
                panel.collapseAllModules();
            }
        }
    }

    /** 收起所有 panel 中的所有展开 */
    private void collapseAllExpansions() {
        for (Panel panel : panels) {
            panel.collapseAllModules();
        }
    }
}
