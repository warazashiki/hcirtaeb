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

public class ClickGuiScreen extends Screen {

    private static final int PANEL_GAP_X = 3;
    private static final int START_X = 3;
    private static final int START_Y = 30;
    private static final int MAX_PANELS_PER_ROW = 4;
    private final List<Panel> panels = new ArrayList<>();
    private Panel draggedPanel = null;
    private long lastFrameTime;

    public ClickGuiScreen() {
        super(GameNarrator.NO_TITLE);
        initPanels();
        lastFrameTime = System.nanoTime();
    }
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
            int px = currentX, py = currentY;
            if (savedPositions.containsKey(cat.name)) {
                int[] saved = savedPositions.get(cat.name);
                px = saved[0];
                py = saved[1];
            }
            Panel panel = new Panel(cat, mc.font, px, py);
            panels.add(panel);
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
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        long now = System.nanoTime();
        float delta = (now - lastFrameTime) / 1_000_000_000f;
        delta = Math.clamp(delta, 0.0f, 0.1f); // 防止帧率异常
        lastFrameTime = now;
        if (draggedPanel != null && draggedPanel.isDragging()) draggedPanel.updateDrag(mouseX, mouseY);
        for (Panel panel : panels) {
            panel.render(guiGraphics, mouseX, mouseY, delta);
        }
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel panel = panels.get(i);
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                panels.remove(i);
                panels.add(panel);
                if (panel.isDragging()) draggedPanel = panel;
                if (button == GLFW.GLFW_MOUSE_BUTTON_2) handleExpansion(panel);
                return true;
            }
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 || button == GLFW.GLFW_MOUSE_BUTTON_2) collapseAllExpansions();
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggedPanel != null) {
            draggedPanel.mouseReleased(mouseX, mouseY, button);
            if (!draggedPanel.isDragging()) draggedPanel = null;
            return true;
        }
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel panel = panels.get(i);
            if (panel.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel panel = panels.get(i);
            if (panel.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Panel panel : panels) if (panel.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (int i = panels.size() - 1; i >= 0; i--) if (panels.get(i).charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }
    @Override
    public void onClose() {
        Map<String, int[]> positions = new LinkedHashMap<>();
        for (Panel panel : panels) positions.put(panel.getTitle(), new int[]{panel.x, panel.y});
        ConfigManager.savePanelPositions(positions);
        super.onClose();
        draggedPanel = null;
        collapseAllExpansions();
    }
    private void handleExpansion(Panel clickedPanel) {
        for (Panel panel : panels) if (panel != clickedPanel) panel.collapseAllModules();
    }
    private void collapseAllExpansions() {
        for (Panel panel : panels) panel.collapseAllModules();
    }
}
