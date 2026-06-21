package c.e.beatrich.setting.types;

import c.e.beatrich.setting.Setting;

/**
 * 颜色设置 — 存储 ARGB int 值。
 * <p>
 * 各通道通过 getter/setter 独立访问：
 * {@code getRed()/getGreen()/getBlue()/getAlpha()}。
 * </p>
 */
public class ColorSetting extends Setting<Integer> {

    public ColorSetting(String name, String description, int defaultARGB) {
        super(name, description, defaultARGB);
    }

    public int getRed()   { return (value >> 16) & 0xFF; }
    public int getGreen() { return (value >> 8) & 0xFF; }
    public int getBlue()  { return value & 0xFF; }
    public int getAlpha() { return (value >> 24) & 0xFF; }

    public void setRGBA(int r, int g, int b, int a) {
        set((a << 24) | (r << 16) | (g << 8) | b);
    }

    /** 静默设置值（不触发 onChange）— 供 ConfigManager 加载使用 */
    public void setSilent(int val) {
        this.value = val;
    }
}
