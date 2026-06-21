package c.e.beatrich.gui.screen;

import c.e.beatrich.gui.theme.Theme;
import c.e.beatrich.setting.types.BlockListSetting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * 方块列表编辑 — 左右分栏：已选中 / 未选中
 */
public class BlockListEditScreen extends Screen {

    private static final int TITLE_H = 14;
    private static final int SEARCH_H = 14;
    private static final int ROW_H = 13;
    private static final int SCROLLBAR_W = 4;
    private static final int GAP = 3;

    private final BlockListSetting setting;
    private final List<Block> selected = new ArrayList<>();
    private final List<Block> unselected = new ArrayList<>();
    private String filter = "";
    private int scrollL, scrollR, totalHL, totalHR;
    private boolean searchFocused;
    private int cursorTick;
    private int lx, rx, lw, rw, listY, listH;
    private int marginX, marginTop;

    public BlockListEditScreen(BlockListSetting setting) {
        super(Component.literal(setting.name));
        this.setting = setting;

        Set<Block> current = setting.get();
        for (Block b : BuiltInRegistries.BLOCK) {
            if (current.contains(b)) selected.add(b);
            else unselected.add(b);
        }
        selected.sort(Comparator.comparing(b -> b.getName().getString()));
        unselected.sort(Comparator.comparing(b -> b.getName().getString()));
    }

    @Override
    protected void init() {
        // 整体缩小 25% — 水平留白各 12.5%，垂直各 12.5%
        marginX = width / 8;
        marginTop = height / 8;
        int contentW = width - marginX * 2;
        int contentH = height - marginTop * 2;

        lw = (contentW - GAP) / 2;
        rw = contentW - lw - GAP;
        lx = marginX;
        rx = lx + lw + GAP;
        listY = marginTop + TITLE_H + SEARCH_H + ROW_H + GAP;
        listH = contentH - TITLE_H - SEARCH_H - ROW_H - GAP * 2;

        recalcLayout();
    }

    private void recalcLayout() {
        int rowH = ROW_H;
        totalHL = selected.size() * rowH;
        totalHR = 0;
        for (Block b : unselected) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
            if (id == null) continue;
            if (filter.isEmpty() || id.toString().toLowerCase(Locale.ROOT).contains(filter)
                || b.getName().getString().toLowerCase(Locale.ROOT).contains(filter))
                totalHR += rowH;
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        // === 背景 ===
        g.fill(0, 0, width, height, 0x80000000);

        int contentW = width - marginX * 2;
        int contentH = height - marginTop * 2;
        int contentB = marginTop + contentH;

        // === 内容区背景 ===
        Theme.renderBackground(g, marginX, marginTop, contentW, contentH, Theme.BACKGROUND, Theme.OUTLINE);

        // === 标题栏 ===
        g.fill(marginX + 2, marginTop + 2, marginX + contentW - 2, marginTop + TITLE_H, Theme.ACCENT.getRGB());
        String title = setting.name + " [" + selected.size() + "]";
        g.drawCenteredString(font, title, marginX + contentW / 2,
                marginTop + (TITLE_H - font.lineHeight) / 2, Theme.TEXT_TITLE.getRGB());

        // === 搜索框 ===
        int searchY = marginTop + TITLE_H;
        g.fill(marginX + 2, searchY, marginX + contentW - 2, searchY + SEARCH_H, Theme.MODULE_BG.getRGB());
        String prompt = searchFocused
            ? "Search: " + filter + (cursorTick / 20 % 2 == 0 ? "|" : " ")
            : (filter.isEmpty() ? "Click to search..." : "Search: " + filter);
        g.drawString(font, Component.literal(prompt), marginX + 4,
                searchY + (SEARCH_H - font.lineHeight) / 2, Theme.TEXT_PRIMARY.getRGB());

        // === 列标题 ===
        int colHeaderY = searchY + SEARCH_H;
        g.fill(lx, colHeaderY, lx + lw, colHeaderY + ROW_H, Theme.MODULE_ACTIVE_BG.getRGB());
        g.drawString(font, Component.literal("Selected"), lx + 2,
                colHeaderY + (ROW_H - font.lineHeight) / 2, Theme.TEXT_PRIMARY.getRGB());
        g.fill(rx, colHeaderY, rx + rw, colHeaderY + ROW_H, Theme.MODULE_ACTIVE_BG.getRGB());
        g.drawString(font, Component.literal("Unselected"), rx + 2,
                colHeaderY + (ROW_H - font.lineHeight) / 2, Theme.TEXT_SECONDARY.getRGB());

        // === 裁剪到列表区域 ===
        g.enableScissor(marginX, listY, marginX + contentW, listY + listH);

        // === 左列：已选中 ===
        int maxSL = Math.max(0, totalHL - listH);
        for (int i = 0; i < selected.size(); i++) {
            int ey = listY + i * ROW_H - scrollL;
            if (ey + ROW_H < listY || ey > listY + listH) continue;
            Block b = selected.get(i);
            boolean hov = mouseX >= lx && mouseX < lx + lw && mouseY >= ey && mouseY < ey + ROW_H;
            int bg = hov ? 0xFF404040 : Theme.MODULE_BG.getRGB();
            g.fill(lx, ey, lx + lw, ey + ROW_H, bg);
            g.drawString(font, b.getName().getString(), lx + 2, ey + (ROW_H - font.lineHeight) / 2,
                    Theme.TEXT_PRIMARY.getRGB());
        }
        if (maxSL > 0) drawScrollbar(g, lx + lw - SCROLLBAR_W, listY, listH, scrollL, totalHL);

        // === 右列：未选中 ===
        int maxSR = Math.max(0, totalHR - listH);
        int ri = 0;
        for (Block b : unselected) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
            if (id == null) continue;
            if (!filter.isEmpty() && !id.toString().toLowerCase(Locale.ROOT).contains(filter)
                && !b.getName().getString().toLowerCase(Locale.ROOT).contains(filter))
                continue;
            int ey = listY + ri * ROW_H - scrollR;
            ri++;
            if (ey + ROW_H < listY || ey > listY + listH) continue;
            boolean hov = mouseX >= rx && mouseX < rx + rw && mouseY >= ey && mouseY < ey + ROW_H;
            int bg = hov ? 0xFF404040 : Theme.MODULE_BG.getRGB();
            g.fill(rx, ey, rx + rw, ey + ROW_H, bg);
            g.drawString(font, b.getName().getString(), rx + 2, ey + (ROW_H - font.lineHeight) / 2,
                    Theme.TEXT_SECONDARY.getRGB());
        }
        if (maxSR > 0) drawScrollbar(g, rx + rw - SCROLLBAR_W, listY, listH, scrollR, totalHR);

        g.disableScissor();
    }

    private void drawScrollbar(GuiGraphics g, int sx, int sy, int sh, int scroll, int total) {
        int max = Math.max(0, total - sh);
        if (max <= 0) return;
        int h = Math.max(8, sh * sh / total);
        int y = sy + scroll * (sh - h) / max;
        g.fill(sx, y, sx + SCROLLBAR_W, y + h, Theme.SCROLLBAR_HANDLE.getRGB());
    }

    @Override
    public void tick() { cursorTick++; }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        // 搜索框
        if (my >= marginTop + TITLE_H && my < marginTop + TITLE_H + SEARCH_H
            && mx >= marginX && mx < marginX + (width - marginX * 2)) {
            searchFocused = true;
            return true;
        }
        searchFocused = false;
        if (button != 0) return false;
        if (my < listY || my > listY + listH) return false;

        // 左列点击 → 移出
        if (mx >= lx && mx < lx + lw) {
            int idx = (int)(my - listY + scrollL) / ROW_H;
            if (idx >= 0 && idx < selected.size()) {
                Block b = selected.remove(idx);
                unselected.add(b);
                unselected.sort(Comparator.comparing(bl -> bl.getName().getString()));
                scrollL = Math.clamp(scrollL, 0, Math.max(0, totalHL - listH));
                recalcLayout();
                return true;
            }
        }
        // 右列点击 → 移入
        if (mx >= rx && mx < rx + rw) {
            int idx = (int)(my - listY + scrollR) / ROW_H;
            int ri = 0;
            Block found = null;
            for (Block b : unselected) {
                ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
                if (id == null) continue;
                if (!filter.isEmpty() && !id.toString().toLowerCase(Locale.ROOT).contains(filter)
                    && !b.getName().getString().toLowerCase(Locale.ROOT).contains(filter))
                    continue;
                if (ri == idx) { found = b; break; }
                ri++;
            }
            if (found != null) {
                unselected.remove(found);
                selected.add(found);
                selected.sort(Comparator.comparing(b -> b.getName().getString()));
                scrollR = Math.clamp(scrollR, 0, Math.max(0, totalHR - listH));
                recalcLayout();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        if (my < listY || my > listY + listH) return false;
        int d = (int)(sy * -20);
        if (mx >= lx && mx < lx + lw) {
            scrollL = Math.clamp(scrollL + d, 0, Math.max(0, totalHL - listH));
        } else {
            scrollR = Math.clamp(scrollR + d, 0, Math.max(0, totalHR - listH));
        }
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            onClose(); return true;
        }
        if (!searchFocused) return false;
        if (key == GLFW.GLFW_KEY_BACKSPACE) {
            if (!filter.isEmpty()) { filter = filter.substring(0, filter.length() - 1); scrollR = 0; recalcLayout(); }
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char c, int mods) {
        if (!searchFocused) return false;
        if (c >= ' ') { filter += c; scrollR = 0; recalcLayout(); return true; }
        return false;
    }

    @Override
    public void onClose() {
        Set<Block> newSet = new LinkedHashSet<>(selected);
        setting.set(newSet);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
