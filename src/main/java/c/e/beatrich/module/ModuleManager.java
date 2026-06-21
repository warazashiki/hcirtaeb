package c.e.beatrich.module;

import c.e.beatrich.module.modules.combat.Criticals;
import c.e.beatrich.module.modules.misc.test;
import c.e.beatrich.module.modules.player.Fireworks;
import c.e.beatrich.module.modules.render.NoRender;
import c.e.beatrich.module.modules.render.XRay;
import c.e.beatrich.module.modules.world.FullBright;
import c.e.beatrich.module.modules.world.TimeChanger;
import c.e.beatrich.event.events.PacketEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 模块管理器 — 模仿 Meteor Client 的 ModuleManager。
 * 负责注册、查找和分发事件给所有模块。
 */
public class ModuleManager {

    private static ModuleManager INSTANCE;

    private final List<Module> modules = new ArrayList<>();

    private ModuleManager() {
        initModules();
        // 按名称排序
        modules.sort(Comparator.comparing(m -> m.name));
    }

    public static ModuleManager get() {
        if (INSTANCE == null) {
            INSTANCE = new ModuleManager();
        }
        return INSTANCE;
    }

    /** 初始化所有模块 */
    private void initModules() {
        // Combat
        modules.add(new Criticals());

        // Movement

        // Player
        modules.add(new Fireworks());

        // World
        modules.add(new FullBright());
        modules.add(new TimeChanger());

        // Render
        modules.add(new XRay());
        modules.add(new NoRender());

        // Misc
    }

    /** 获取所有模块 */
    public List<Module> getAll() {
        return modules;
    }

    /** 获取激活的模块列表 */
    public List<Module> getActive() {
        return modules.stream().filter(Module::isActive).collect(Collectors.toList());
    }

    /** 按分类获取模块 */
    public List<Module> getByCategory(Category category) {
        return modules.stream().filter(m -> m.category == category).collect(Collectors.toList());
    }

    /** 按名称查找模块 */
    public Optional<Module> getByName(String name) {
        return modules.stream().filter(m -> m.name.equalsIgnoreCase(name)).findFirst();
    }

    /**
     * 将 ClientTickEvent.Pre 分发给所有激活的模块。
     * 在输入处理/发包之前调用，适合需要伪造数据包的模块。
     */
    public void onPreTick(ClientTickEvent.Pre event) {
        for (Module module : modules) {
            if (module.isActive()) {
                try {
                    module.onPreTick(event);
                } catch (Exception e) {
                    // 防止单个模块崩溃影响其他模块
                }
            }
        }
    }

    /**
     * 将 ClientTickEvent.Post 分发给所有激活的模块。
     * 由 mixin 或客户端事件处理器调用。
     */
    public void onTick(ClientTickEvent.Post event) {
        for (Module module : modules) {
            if (module.isActive()) {
                try {
                    module.onTick(event);
                } catch (Exception e) {
                    // 防止单个模块崩溃影响其他模块
                }
            }
        }
    }

    /**
     * 处理按键事件，触发模块快捷键。
     * 由 KeyboardInputMixin / MouseHandlerMixin 调用。
     *
     * @param keyCode GLFW 键码（键盘按键或鼠标按钮）
     * @return true 表示该按键已被模块消费，应取消后续处理
     */
    public boolean onKeyPress(int keyCode) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN) return false;

        for (Module module : modules) {
            if (module.getKeybind() == keyCode) {
                module.toggle();
                return true;
            }
        }
        return false;
    }

    /**
     * 将世界渲染事件分发给所有激活的模块。
     * 由 hcirtaebClient 的 RenderLevelStageEvent 监听器调用。
     */
    public void onRenderWorld(RenderLevelStageEvent event) {
        for (Module module : modules) {
            if (module.isActive()) {
                try { module.onRenderWorld(event); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * 将发包事件分发给所有激活的模块。
     * 由 ConnectionMixin 调用。
     */
    public void onPacketSend(PacketEvent event) {
        for (Module module : modules) {
            if (module.isActive()) {
                try { module.onPacketSend(event); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * 将收包事件分发给所有激活的模块。
     * 由 ConnectionMixin channelRead0 拦截调用。
     */
    public void onPacketReceive(PacketEvent event) {
        for (Module module : modules) {
            if (module.isActive()) {
                try { module.onPacketReceive(event); } catch (Exception ignored) {}
            }
        }
    }
}
