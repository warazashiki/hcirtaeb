package c.e.beatrich.module.modules.combat;

import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.setting.types.BoolSetting;

public class Aura extends Module {
    public BoolSetting nf = boolSetting("NotFinished", "未完成", false);
    public Aura() {
        super("Aura", "杀戮光环", Category.COMBAT);
    }
}
