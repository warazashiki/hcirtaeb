package c.e.beatrich.setting;

/**
 * 泛型设置基类 — 模仿 Meteor Client 的 Setting 系统。
 * 每个设置有一个名称、描述、当前值和默认值。
 */
public abstract class Setting<T> {

    public final String name;
    public final String description;
    protected T value;
    protected final T defaultValue;

    /** 值变更回调（用于自动保存配置） */
    private Runnable onChange;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        fireChange();
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void reset() {
        this.value = defaultValue;
        fireChange();
    }

    /** 注册值变更回调（由 ConfigManager 使用） */
    public void onChanged(Runnable callback) {
        this.onChange = callback;
    }

    /** 触发变更回调 */
    protected void fireChange() {
        if (onChange != null) {
            onChange.run();
        }
    }
}
