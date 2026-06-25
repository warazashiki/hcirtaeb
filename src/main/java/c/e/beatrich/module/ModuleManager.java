package c.e.beatrich.module;

import c.e.beatrich.module.modules.combat.Criticals;
import c.e.beatrich.module.modules.player.Fireworks;
import c.e.beatrich.module.modules.player.Multitask;
import c.e.beatrich.module.modules.render.BlockEsp;
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

public class ModuleManager {

    private static ModuleManager INSTANCE;
    private final List<Module> modules = new ArrayList<>();

    private ModuleManager() {
        initModules();
        modules.sort(Comparator.comparing(m -> m.name));
    }
    public static ModuleManager get() {
        if (INSTANCE == null) {
            INSTANCE = new ModuleManager();
        }
        return INSTANCE;
    }
    private void initModules() {
        // Combat
        modules.add(new Criticals());

        // Movement

        // Player
        modules.add(new Fireworks());
        modules.add(new Multitask());

        // World
        modules.add(new FullBright());
        modules.add(new TimeChanger());

        // Render
        modules.add(new XRay());
        modules.add(new NoRender());
        modules.add(new BlockEsp());

        // Misc
    }
    public List<Module> getAll() {
        return modules;
    }
    public List<Module> getActive() {
        return modules.stream().filter(Module::isActive).collect(Collectors.toList());
    }
    public List<Module> getByCategory(Category category) {
        return modules.stream().filter(m -> m.category == category).collect(Collectors.toList());
    }
    public Optional<Module> getByName(String name) {
        return modules.stream().filter(m -> m.name.equalsIgnoreCase(name)).findFirst();
    }
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
    public void onRenderWorld(RenderLevelStageEvent event) {
        for (Module module : modules) {
            if (module.isActive()) try { module.onRenderWorld(event); } catch (Exception ignored) {}
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
