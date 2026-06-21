package c.e.beatrich.module;

import c.e.beatrich.event.events.GetShadeBrightnessEvent;
import c.e.beatrich.event.events.RenderBlockEntityEvent;
import c.e.beatrich.event.events.VisGraphEvent;
import c.e.beatrich.setting.Setting;
import c.e.beatrich.setting.types.*;
import c.e.beatrich.utils.ChatUtils;
import c.e.beatrich.event.events.PacketEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块基类 — 模仿 Meteor Client 的 Module 架构。
 * 每个模块可以切换开关，拥有设置项和快捷键。
 */
public abstract class Module {

    protected static final Minecraft mc = Minecraft.getInstance();

    public final String name;
    public final String description;
    public final Category category;
    private boolean msg = true;

    private boolean active = false;
    /** 快捷键设置 — 自动添加到设置列表末尾，在 ClickGUI 中可捕获按键 */
    protected KeybindSetting keybind;
    protected final List<Setting<?>> settings = new ArrayList<>();

    /** 模块状态变更回调（用于自动保存配置） */
    private Runnable onStateChanged;

    public Module(String name, String description, Category category) {
        this(name, description, category, GLFW.GLFW_KEY_UNKNOWN, true);
    }
    public Module(String name, String description, Category category, boolean msg) {
        this(name, description, category, GLFW.GLFW_KEY_UNKNOWN, msg);
    }
    public Module(String name, String description, Category category, int defaultkeybind) {
        this(name, description, category, defaultkeybind, true);
    }
    public Module(String name, String description, Category category, int defaultKeybind, boolean msg) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.msg = msg;

        // 先让子类初始化自己的设置
        initSettings();

        // 最后添加快捷键设置（在 ClickGUI 列表中排在末尾）
        this.keybind = new KeybindSetting("Keybind", "模块快捷键", defaultKeybind);
        addSetting(keybind);
    }

    /** 子类在此方法中通过 addSetting() 添加设置项 */
    protected void initSettings() {
        // 默认不添加任何设置，子类覆写
    }

    /** 模块激活时调用 */
    public void onActivate() {
        // 占位：子类覆写
    }

    /** 模块停用时调用 */
    public void onDeactivate() {
        // 占位：子类覆写
    }

    /**
     * 每客户端 tick 调用（Pre 阶段，在输入处理/发包之前）。
     * 仅当模块激活时由 ModuleManager 分发。
     * 适合需要在攻击包/移动包发出前注入伪造包的模块（如 Criticals、MaceSpoof）。
     */
    public void onPreTick(ClientTickEvent.Pre event) {
        // 占位：子类覆写
    }

    /** 每客户端 tick 调用（Post 阶段，仅当模块激活时由 ModuleManager 分发） */
    public void onTick(ClientTickEvent.Post event) {
        // 占位：子类覆写
    }

    /**
     * 世界渲染阶段调用（仅当模块激活时由 ModuleManager 分发）。
     * 适合 ESP、BlockESP、XRay 等需要在世界空间绘制覆盖层的模块。
     */
    public void onRenderWorld(RenderLevelStageEvent event) {
        // 占位：子类覆写
    }

    /**
     * 发包前调用（由 ConnectionMixin 触发）。
     * 设置 {@link PacketEvent#cancel()} 可阻止该包发出。
     */
    public void onPacketSend(PacketEvent event) {
        // 占位：子类覆写
    }

    /**
     * 收包时调用（由 ConnectionMixin channelRead0 拦截触发）。
     * 设置 {@link PacketEvent#cancel()} 可阻止该包被处理。
     */
    public void onPacketReceive(PacketEvent event) {
        // 占位：子类覆写
    }

    /*
    由BlickEntityRenderDispatcherMixin调用
     */
    public void onRenderBlockEntity(RenderBlockEntityEvent event) {
        //同上
    }
    /*
    由VisGraphMIxin调用
     */
    public void onVisGraph(VisGraphEvent event) {
        //同上
    }
    /*
    由BlockBehaviour调用
     */
    public void onGetShadeBrightness(GetShadeBrightnessEvent event) {
        //同上
    }

    /** 切换模块开关状态 */
    public void toggle() {
        if (active) {
            deactivate();
        } else {
            activate();
        }
    }

    public void activate() {
        if (active) return;
        active = true;
        if (mc.player != null && msg) {
            ChatUtils.sendMsg("§7[+]§d%s".formatted(name));
        }
        onActivate();
        fireStateChanged();
    }

    public void deactivate() {
        if (!active) return;
        active = false;
        onDeactivate();
        if (mc.player != null && msg) {
            ChatUtils.sendMsg("§7[-]§d%s".formatted(name));
        }
        fireStateChanged();
    }

    // === Getters / Setters ===

    public boolean isActive() {
        return active;
    }

    /** 获取快捷键键码 */
    public int getKeybind() {
        return keybind != null ? keybind.getKey() : GLFW.GLFW_KEY_UNKNOWN;
    }

    /** 设置快捷键键码（触发保存回调） */
    public void setKeybind(int kb) {
        if (keybind != null) {
            keybind.setKey(kb);
            fireStateChanged();
        }
    }

    /** 注册状态变更回调（由 ConfigManager 使用） */
    public void onStateChanged(Runnable callback) {
        this.onStateChanged = callback;
    }

    private void fireStateChanged() {
        if (onStateChanged != null) {
            onStateChanged.run();
        }
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    /** 向设置列表中添加一个设置项 */
    protected void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

    // === 便捷设置工厂方法 ===

    protected BoolSetting boolSetting(String name, String description, boolean defaultValue) {
        BoolSetting s = new BoolSetting(name, description, defaultValue);
        addSetting(s);
        return s;
    }

    protected IntSetting intSetting(String name, String description, int defaultValue, int min, int max) {
        IntSetting s = new IntSetting(name, description, defaultValue, min, max);
        addSetting(s);
        return s;
    }

    protected DoubleSetting doubleSetting(String name, String description, double defaultValue, double min, double max) {
        DoubleSetting s = new DoubleSetting(name, description, defaultValue, min, max);
        addSetting(s);
        return s;
    }

    protected <E extends Enum<E>> EnumSetting<E> enumSetting(String name, String description, E defaultValue) {
        EnumSetting<E> s = new EnumSetting<>(name, description, defaultValue);
        addSetting(s);
        return s;
    }

    protected ColorSetting colorSetting(String name, String description, int defaultARGB) {
        ColorSetting s = new ColorSetting(name, description, defaultARGB);
        addSetting(s);
        return s;
    }

    protected BlockListSetting blockListSetting(String name, String description,
                                                  java.util.Set<net.minecraft.world.level.block.Block> defaults) {
        BlockListSetting s = new BlockListSetting(name, description, defaults);
        addSetting(s);
        return s;
    }
}
