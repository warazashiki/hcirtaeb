package c.e.beatrich.gui.widget;

import c.e.beatrich.gui.theme.Theme;
import c.e.beatrich.setting.types.BoolSetting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;

/**
 * 布尔设置行组件 — 以行背景颜色表示开关状态。
 * <p>
 * true  → 淡紫色背景 ({@code 100, 55, 170, 130}) + 白色文字<br>
 * false → 默认暗色背景 + 灰色文字<br>
 * 点击整行切换值
 * </p>
 */
public class BoolSettingWidget extends Widget {

    private static final int TEXT_PAD_LEFT = 1;

    /** true 态背景 — 淡紫色 */
    private static final Color BG_TRUE = new Color(100, 55, 170, 130);
    /** true 态 hover — 稍亮 */
    private static final Color BG_TRUE_HOVER = new Color(120, 70, 190, 150);
    /** false 态背景 — 默认暗色 */
    private static final Color BG_FALSE = new Color(18, 18, 20, 100);
    /** false 态 hover — 稍亮 */
    private static final Color BG_FALSE_HOVER = new Color(30, 30, 35, 120);

    /** true→false 过渡动画进度 */
    private float anim = 0f;

    private final BoolSetting setting;
    private final Font font;
    private boolean hovered = false;

    public BoolSettingWidget(BoolSetting setting, Font font, int x, int y, int width) {
        super(x, y, width, Theme.SETTING_HEIGHT);
        this.setting = setting;
        this.font = font;
        this.anim = setting.get() ? 1f : 0f;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        hovered = isMouseOver(mouseX, mouseY);
        anim = Theme.animate(anim, setting.get(), delta, 10f);

        // 行背景：在 true/false 色之间插值
        Color fromBg = hovered ? BG_FALSE_HOVER : BG_FALSE;
        Color toBg = hovered ? BG_TRUE_HOVER : BG_TRUE;
        Color bgColor = Theme.lerpColor(fromBg, toBg, anim);
        g.fill(x, y, x + width, y + height, bgColor.getRGB());

        // 文本
        int textY = y + (height - font.lineHeight) / 2;
        Color textColor = anim > 0.5f ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY;
        g.drawString(font, Component.literal(setting.description), x + TEXT_PAD_LEFT, textY, textColor.getRGB());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || button != 0) return false;
        if (isMouseOver(mouseX, mouseY)) {
            setting.toggle();
            return true;
        }
        return false;
    }
}
