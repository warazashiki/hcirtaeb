package c.e.beatrich.setting.types;

import c.e.beatrich.setting.Setting;

/**
 * 布尔类型设置
 */
public class BoolSetting extends Setting<Boolean> {

    public BoolSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }

    public void toggle() {
        value = !value;
        fireChange();
    }

    /**
     * 静默设置值（不触发 onChange 回调）— 仅供 ConfigManager 加载时使用。
     */
    public void setSilent(boolean val) {
        this.value = val;
    }
}
