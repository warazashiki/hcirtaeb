package c.e.beatrich;

import c.e.beatrich.config.ConfigManager;
import c.e.beatrich.gui.ClickGuiScreen;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = hcirtaeb.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = hcirtaeb.MODID, value = Dist.CLIENT)
public class hcirtaebClient {

    public static Minecraft mc;

    public static final KeyMapping OPEN_CLICK_GUI = new KeyMapping(
            "key.hcirtaeb.open_click_gui",
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "key.categories.hcirtaeb"
    );

    public hcirtaebClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Register client tick events for module processing
        NeoForge.EVENT_BUS.addListener(this::onClientTickPre);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
        // Register world render event for ESP/BlockESP/XRay overlay rendering
        NeoForge.EVENT_BUS.addListener(this::onRenderLevelStage);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        hcirtaeb.LOGGER.info("HELLO FROM CLIENT SETUP");
        hcirtaeb.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        // 初始化配置系统
        ConfigManager.init();

        // 加载已保存的模块配置
        var modules = ModuleManager.get().getAll();
        ConfigManager.loadModules(modules);

        // 预加载 ClickGUI 面板位置
        ConfigManager.loadPanelPositions();

        // 为所有模块附加自动保存回调
        for (Module module : modules) {
            module.onStateChanged(() -> ConfigManager.saveModules(ModuleManager.get().getAll()));
            ConfigManager.attachSaveCallbacks(module, modules);
        }
    }

    /**
     * 客户端 Pre tick — 在输入处理/发包之前分发给所有激活的模块。
     */
    private void onClientTickPre(ClientTickEvent.Pre event) {
        ModuleManager.get().onPreTick(event);
    }

    @SubscribeEvent
    static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_CLICK_GUI);
    }

    /**
     * 客户端 Post tick — 模块 tick + ClickGUI KeyMapping 检测
     */
    private void onClientTick(ClientTickEvent.Post event) {
        ModuleManager.get().onTick(event);

        var mc = Minecraft.getInstance();
        while (OPEN_CLICK_GUI.consumeClick()) {
            if (mc.screen == null) {
                mc.setScreen(new ClickGuiScreen());
            } else if (mc.screen instanceof ClickGuiScreen) {
                mc.screen.onClose();
            }
        }
    }

    /**
     * 世界渲染阶段 — 在透明层之后分发，使 ESP 覆盖层可穿墙可见。
     */
    private void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            ModuleManager.get().onRenderWorld(event);
        }
    }

}
