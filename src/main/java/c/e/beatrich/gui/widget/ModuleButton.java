package c.e.beatrich.gui.widget;

import c.e.beatrich.gui.theme.Theme;
import c.e.beatrich.module.Module;
import c.e.beatrich.setting.Setting;
import c.e.beatrich.setting.types.BlockListSetting;
import c.e.beatrich.setting.types.BoolSetting;
import c.e.beatrich.setting.types.DoubleSetting;
import c.e.beatrich.setting.types.EnumSetting;
import c.e.beatrich.setting.types.IntSetting;
import c.e.beatrich.setting.types.KeybindSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 模块切换按钮 — Meteor 风格。
 * <p>
 * 特性：<br>
 * - hover 背景过渡动画<br>
 * - 激活模块左侧紫色竖条（从下往上动画展开）<br>
 * - 右侧 ▼/▶ 箭头（有设置项时）<br>
 * - 展开后显示设置 widget 列表<br>
 * - 左键切换开关，右键展开/折叠设置
 */
public class ModuleButton extends Widget {

    private static final int LEFT_BAR_WIDTH = 1;
    private static final int TEXT_PAD_LEFT = 1;
    private static final int ARROW_PAD_RIGHT = 1;

    private final Module module;
    private final Font font;
    private final int panelWidth;

    /** hover 动画进度 */
    private float hoverAnim = 0f;
    /** 激活条动画进度 */
    private float activeBarAnim = 0f;
    /** 是否展开设置 */
    private boolean expanded = false;
    /** 当前帧是否被鼠标悬停 */
    private boolean hovered = false;

    /** 设置 widget 列表（懒加载，展开时创建） */
    private List<Widget> settingWidgets = null;

    public ModuleButton(Module module, Font font, int x, int y, int panelWidth) {
        super(x, y, panelWidth, Theme.MODULE_HEIGHT);
        this.module = module;
        this.font = font;
        this.panelWidth = panelWidth;
        this.activeBarAnim = module.isActive() ? 1f : 0f;
    }

    // ======================== 公共接口 ========================

    public Module getModule() {
        return module;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        if (expanded && settingWidgets == null) {
            buildSettingWidgets();
        }
    }

    /** 获取展开后额外占用的高度（设置项总高度 + 间距） */
    public int getExpandedHeight() {
        if (!expanded || settingWidgets == null) return 0;
        int h = 0;
        for (Widget w : settingWidgets) {
            if (w.visible) h += w.height;
        }
        return h;
    }

    // ======================== 渲染 ========================

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        hovered = isMouseOver(mouseX, mouseY);

        // 更新动画
        hoverAnim = Theme.animate(hoverAnim, hovered || module.isActive(), delta, 4f);
        activeBarAnim = Theme.animate(activeBarAnim, module.isActive(), delta, 6f);

        // === 行背景（带 hover 过渡） ===
        Color bgNormal = module.isActive() ? Theme.MODULE_ACTIVE_BG : Theme.MODULE_BG;
        Color bgHover = module.isActive() ? Theme.MODULE_ACTIVE_BG : Theme.MODULE_BG_HOVER;
        Color bgColor = Theme.lerpColor(bgNormal, bgHover, hoverAnim);
        g.fill(x, y, x + width, y + height, bgColor.getRGB());

        // === 激活条（左侧紫色竖条，从下往上动画） ===
        if (activeBarAnim > 0.01f) {
            int barHeight = (int) (height * activeBarAnim);
            int barY = y + height - barHeight;
            g.fill(x, barY, x + LEFT_BAR_WIDTH, y + height, Theme.ACCENT.getRGB());
        }

        // === 模块名 ===
        int textX = x + TEXT_PAD_LEFT + LEFT_BAR_WIDTH;
        int textY = y + (height - font.lineHeight) / 2;
        Color textColor = module.isActive() ? Theme.TEXT_ACTIVE : Theme.TEXT_INACTIVE;
        g.drawString(font, Component.literal(module.name), textX, textY, textColor.getRGB());

        // === 展开箭头 ===
        if (!module.getSettings().isEmpty()) {
            String arrow = expanded ? "▼" : "▶";
            int arrowColor = expanded ? Theme.ACCENT.getRGB() : Theme.TEXT_SECONDARY.getRGB();
            int arrowX = x + width - font.width(arrow) - ARROW_PAD_RIGHT;
            g.drawString(font, Component.literal(arrow), arrowX, textY, arrowColor);
        }

        // === 渲染展开的设置项 ===
        if (expanded && settingWidgets != null) {
            int sy = y + height;
            for (Widget w : settingWidgets) {
                if (!w.visible) continue;
                w.setPosition(x + 1, sy);
                w.render(g, mouseX, mouseY, delta);
                sy += w.height;
            }
        }
    }

    // ======================== 交互 ========================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;

        // 失去焦点：点击前先通知所有设置项
        if (expanded && settingWidgets != null) {
            // 检查点击是否命中某个设置项
            boolean hitSetting = false;
            for (Widget w : settingWidgets) {
                if (w.isMouseOver(mouseX, mouseY)) {
                    hitSetting = true;
                } else {
                    w.loseFocus(); // 点击别处 → 失去焦点
                }
            }
            // 再分发事件
            for (Widget w : settingWidgets) {
                if (w.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }

        // 模块行本身的点击
        if (!isMouseOver(mouseX, mouseY)) return false;

        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            // 左键 → 切换模块
            module.toggle();
            return true;
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
            // 右键 → 展开/折叠设置
            if (module.getSettings().isEmpty()) return false;
            boolean wasExpanded = expanded;
            expanded = !expanded;
            if (expanded && settingWidgets == null) {
                buildSettingWidgets();
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // 传递给设置项
        if (expanded && settingWidgets != null) {
            for (Widget w : settingWidgets) {
                if (w.mouseReleased(mouseX, mouseY, button)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (expanded && settingWidgets != null) {
            for (Widget w : settingWidgets) {
                if (w.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (expanded && settingWidgets != null) {
            for (Widget w : settingWidgets) {
                if (w.keyPressed(keyCode, scanCode, modifiers)) return true;
            }
        }
        return false;
    }

    /** 字符输入转发给设置项 */
    public boolean charTyped(char codePoint, int modifiers) {
        if (expanded && settingWidgets != null) {
            for (Widget w : settingWidgets) {
                if (w.charTyped(codePoint, modifiers)) return true;
            }
        }
        return false;
    }

    // ======================== 辅助 ========================

    private void buildSettingWidgets() {
        settingWidgets = new ArrayList<>();
        int sw = panelWidth - 2;
        int sy = 0;
        for (Setting<?> setting : module.getSettings()) {
            Widget w = createWidgetForSetting(setting, sw, sy);
            if (w != null) {
                settingWidgets.add(w);
                sy += w.height;
            }
        }
    }

    private Widget createWidgetForSetting(Setting<?> setting, int sw, int sy) {
        if (setting instanceof BoolSetting bs) {
            return new BoolSettingWidget(bs, font, 0, sy, sw);
        } else if (setting instanceof DoubleSetting ds) {
            return new TextInputWidget(ds, font, 0, sy, sw);
        } else if (setting instanceof IntSetting is) {
            return new TextInputWidget(is, font, 0, sy, sw);
        } else if (setting instanceof EnumSetting<?> es) {
            return new EnumSettingWidget(es, font, 0, sy, sw);
        } else if (setting instanceof KeybindSetting ks) {
            return new KeybindWidget(ks, font, 0, sy, sw);
        } else if (setting instanceof BlockListSetting bls) {
            return new BlockListSettingWidget(bls, font, 0, sy, sw);
        }
        return null;
    }

    @Override
    public boolean isMouseOver(double mx, double my) {
        // 检查模块行本身
        if (mx >= x && mx < x + width && my >= y && my < y + height) return true;
        // 检查展开的设置项
        if (expanded && settingWidgets != null) {
            for (Widget w : settingWidgets) {
                if (w.isMouseOver(mx, my)) return true;
            }
        }
        return false;
    }
}
