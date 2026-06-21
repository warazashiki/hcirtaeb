package c.e.beatrich.setting.types;

import c.e.beatrich.setting.Setting;

/**
 * 浮点类型设置，带范围限制
 */
public class DoubleSetting extends Setting<Double> {

    private final double min;
    private final double max;

    public DoubleSetting(String name, String description, double defaultValue, double min, double max) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    public void set(Double value) {
        this.value = Math.clamp(value, min, max);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
