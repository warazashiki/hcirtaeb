package c.e.beatrich.module.modules.render;

import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import c.e.beatrich.setting.types.BoolSetting;

public class NoRender extends Module {
    public static final BoolSetting NoBlindness = new BoolSetting("NoBlindness", "NoFog", false);

    public NoRender() {
        super("NoRender", "Norender", Category.RENDER);
        addSetting(NoBlindness);
    }
    public static boolean enabled() {
        return ModuleManager.get().getByName("NoRender")
                .map(Module::isActive).orElse(false);
    }
}
