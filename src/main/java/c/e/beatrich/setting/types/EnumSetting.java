package c.e.beatrich.setting.types;

import c.e.beatrich.setting.Setting;

/**
 * 枚举类型设置 — 在几个固定选项中循环切换。
 *
 * @param <E> 枚举类型
 */
public class EnumSetting<E extends Enum<E>> extends Setting<E> {

    private final E[] values;

    @SuppressWarnings("unchecked")
    public EnumSetting(String name, String description, E defaultValue) {
        super(name, description, defaultValue);
        this.values = (E[]) defaultValue.getClass().getEnumConstants();
    }

    /** 切换到下一个枚举值（循环） */
    public void cycle() {
        int idx = (value.ordinal() + 1) % values.length;
        value = values[idx];
        fireChange();
    }

    /** 获取所有可选值 */
    public E[] getValues() {
        return values;
    }

    /** 按索引直接设置值 */
    public void setRaw(int index) {
        if (index >= 0 && index < values.length) {
            value = values[index];
            fireChange();
        }
    }
}
