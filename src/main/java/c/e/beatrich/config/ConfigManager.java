package c.e.beatrich.config;

import c.e.beatrich.module.Module;
import c.e.beatrich.setting.Setting;
import c.e.beatrich.setting.types.*;
import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 配置管理器 — 自动将模块状态、设置值、ClickGUI 面板位置持久化到 JSON 文件。
 * <p>
 * 文件位置：{@code <game_dir>/config/hcirtaeb/}<br>
 * - {@code modules.json} — 模块开关、快捷键、设置值<br>
 * - {@code clickgui.json} — ClickGUI 面板位置<br>
 * </p>
 * <p>
 * 保存时机：模块开关 / 设置值变化 / GUI 关闭 / 游戏退出<br>
 * 加载时机：客户端启动时
 * </p>
 */
public final class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("hcirtaeb|Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configDir;

    /** 加载中标志 — 防止加载时触发的 set() 又写回文件（无限循环） */
    private static boolean loading = false;

    /** ClickGUI 面板位置的静态缓存 */
    private static final Map<String, int[]> panelPositions = new LinkedHashMap<>();

    private ConfigManager() {
    }

    // ======================== 初始化 ========================

    /** 确保配置目录存在。在客户端启动时尽早调用。 */
    public static void init() {
        configDir = FMLPaths.GAMEDIR.get().resolve("config").resolve("hcirtaeb");
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            LOGGER.error("Failed to create config directory: {}", configDir, e);
        }
    }

    // ======================== 模块配置 ========================

    /**
     * 从文件加载所有模块状态。
     *
     * @param modules 所有已注册的模块列表
     */
    public static void loadModules(List<Module> modules) {
        if (configDir == null) init();

        Path file = configDir.resolve("modules.json");
        if (!Files.exists(file)) {
            return;
        }

        loading = true;
        try {
            String raw = Files.readString(file, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(raw).getAsJsonObject();
            JsonArray arr = root.getAsJsonArray("modules");
            if (arr == null) { loading = false; return; }

            for (JsonElement elem : arr) {
                JsonObject obj = elem.getAsJsonObject();
                String name = obj.get("name").getAsString();

                // 查找对应模块
                Module module = modules.stream()
                        .filter(m -> m.name.equals(name))
                        .findFirst().orElse(null);
                if (module == null) continue;

                // 恢复激活状态
                if (obj.has("active")) {
                    boolean active = obj.get("active").getAsBoolean();
                    if (active && !module.isActive()) {
                        module.activate();
                    } else if (!active && module.isActive()) {
                        module.deactivate();
                    }
                }

                // 恢复快捷键
                if (obj.has("keybind")) {
                    module.setKeybind(obj.get("keybind").getAsInt());
                }

                // 恢复设置值
                if (obj.has("settings")) {
                    JsonObject settings = obj.getAsJsonObject("settings");
                    for (Setting<?> setting : module.getSettings()) {
                        if (!settings.has(setting.name)) continue;
                        JsonElement val = settings.get(setting.name);
                        loadSettingValue(setting, val);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load modules.json", e);
        } finally {
            loading = false;
        }
    }

    /** 保存所有模块状态到文件。 */
    public static void saveModules(List<Module> modules) {
        if (loading) return; // 不在加载时保存
        if (configDir == null) init();

        try {
            JsonObject root = new JsonObject();
            JsonArray arr = new JsonArray();

            for (Module module : modules) {
                JsonObject obj = new JsonObject();
                obj.addProperty("name", module.name);
                obj.addProperty("active", module.isActive());
                obj.addProperty("keybind", module.getKeybind());

                // 设置值
                JsonObject settings = new JsonObject();
                for (Setting<?> setting : module.getSettings()) {
                    addSettingValue(settings, setting);
                }
                obj.add("settings", settings);

                arr.add(obj);
            }

            root.add("modules", arr);

            Path file = configDir.resolve("modules.json");
            Files.writeString(file, GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("Failed to save modules.json", e);
        }
    }

    /** 为模块及其所有设置附加自动保存回调。 */
    public static void attachSaveCallbacks(Module module, List<Module> allModules) {
        // 设置变化 → 保存
        for (Setting<?> setting : module.getSettings()) {
            setting.onChanged(() -> saveModules(allModules));
        }
        // 模块切换和快捷键变化通过 Module 方法内联处理
    }

    // ======================== ClickGUI 面板位置 ========================

    /**
     * 加载 ClickGUI 面板位置。
     *
     * @return 分类名 → [x, y] 的映射
     */
    public static Map<String, int[]> loadPanelPositions() {
        if (configDir == null) init();
        panelPositions.clear();

        Path file = configDir.resolve("clickgui.json");
        if (!Files.exists(file)) return panelPositions;

        try {
            String raw = Files.readString(file, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(raw).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                JsonObject pos = entry.getValue().getAsJsonObject();
                int x = pos.get("x").getAsInt();
                int y = pos.get("y").getAsInt();
                panelPositions.put(entry.getKey(), new int[]{x, y});
            }
            LOGGER.info("Loaded {} panel positions", panelPositions.size());
        } catch (Exception e) {
            LOGGER.error("Failed to load clickgui.json", e);
        }
        return panelPositions;
    }

    /** 保存 ClickGUI 面板位置。 */
    public static void savePanelPositions(Map<String, int[]> positions) {
        if (configDir == null) init();
        panelPositions.putAll(positions);

        try {
            JsonObject root = new JsonObject();
            for (Map.Entry<String, int[]> entry : panelPositions.entrySet()) {
                JsonObject pos = new JsonObject();
                pos.addProperty("x", entry.getValue()[0]);
                pos.addProperty("y", entry.getValue()[1]);
                root.add(entry.getKey(), pos);
            }

            Path file = configDir.resolve("clickgui.json");
            Files.writeString(file, GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("Failed to save clickgui.json", e);
        }
    }

    // ======================== 辅助 ========================

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void loadSettingValue(Setting<?> setting, JsonElement val) {
        if (setting instanceof BoolSetting bs) {
            bs.setSilent(val.getAsBoolean());
        } else if (setting instanceof KeybindSetting ks) {
            ks.setSilent(val.getAsInt());
        } else if (setting instanceof IntSetting is) {
            is.set(val.getAsInt());
        } else if (setting instanceof DoubleSetting ds) {
            ds.set(val.getAsDouble());
        } else if (setting instanceof EnumSetting es) {
            String name = val.getAsString();
            for (Object e : es.getValues()) {
                if (((Enum<?>) e).name().equals(name)) {
                    es.set(e);
                    break;
                }
            }
        } else if (setting instanceof BlockListSetting bls) {
            Set<Block> set = new LinkedHashSet<>();
            for (JsonElement e : val.getAsJsonArray()) {
                Block b = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(e.getAsString()));
                if (b != null) set.add(b);
            }
            bls.set(set);
        } else if (setting instanceof ColorSetting cs) {
            cs.setSilent(val.getAsInt());
        }
    }

    private static void addSettingValue(JsonObject obj, Setting<?> setting) {
        if (setting instanceof BoolSetting bs) {
            obj.addProperty(setting.name, bs.get());
        } else if (setting instanceof KeybindSetting ks) {
            obj.addProperty(setting.name, ks.getKey());
        } else if (setting instanceof IntSetting is) {
            obj.addProperty(setting.name, is.get());
        } else if (setting instanceof DoubleSetting ds) {
            obj.addProperty(setting.name, ds.get());
        } else if (setting instanceof EnumSetting<?> es) {
            obj.addProperty(setting.name, (es.get()).name());
        } else if (setting instanceof BlockListSetting bls) {
            JsonArray arr = new JsonArray();
            for (Block b : bls.get()) {
                arr.add(BuiltInRegistries.BLOCK.getKey(b).toString());
            }
            obj.add(setting.name, arr);
        } else if (setting instanceof ColorSetting cs) {
            obj.addProperty(setting.name, cs.get());
        }
    }
}
