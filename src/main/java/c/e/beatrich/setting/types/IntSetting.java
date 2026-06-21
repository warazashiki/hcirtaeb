package c.e.beatrich.setting.types;

import c.e.beatrich.setting.Setting;

/**
 * 整数类型设置，带范围限制
 */
public class IntSetting extends Setting<Integer> {

    private final int min;
    private final int max;

    public IntSetting(String name, String description, int defaultValue, int min, int max) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public void set(Integer value) {
        this.value = Math.clamp(value, min, max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
