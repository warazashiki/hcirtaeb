package c.e.beatrich.module.modules.render;

import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import c.e.beatrich.setting.types.BoolSetting;

public class NoRender extends Module {
    public static final BoolSetting NoBlindness = new BoolSetting("NoBlindness", "移除失明", false);
    public static final BoolSetting NoFog = new BoolSetting("NoFog", "去雾", false);

    public NoRender() {
        super("NoRender", "移除渲染", Category.RENDER);
        addSetting(NoBlindness);
        addSetting(NoFog);
    }
    public static boolean enabled() {
        return ModuleManager.get().getByName("NoRender")
                .map(Module::isActive).orElse(false);
    }
}
