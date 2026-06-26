package c.e.beatrich.setting.types;

import c.e.beatrich.setting.Setting;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {

    private final E[] values;

    @SuppressWarnings("unchecked")
    public EnumSetting(String name, String description, E defaultValue) {
        super(name, description, defaultValue);
        this.values = (E[]) defaultValue.getClass().getEnumConstants();
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
