package c.e.beatrich.module.modules.player;

import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import c.e.beatrich.setting.types.BoolSetting;

public class Multitask extends Module {
    public static BoolSetting Attack = new BoolSetting("Attack", "攻击实体", false);
    public Multitask() {
        super("Multitask", "双手动作", Category.PLAYER);
        addSetting(Attack);
    }
    public static boolean enabled() {
        return ModuleManager.get().getByName("Multitask")
                .map(Module::isActive).orElse(false);
    }
}
