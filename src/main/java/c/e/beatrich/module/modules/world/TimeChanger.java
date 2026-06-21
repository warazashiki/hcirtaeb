package c.e.beatrich.module.modules.world;

import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.setting.types.DoubleSetting;

public class TimeChanger extends Module {
    public TimeChanger() {
        super("TimeChanger", "", Category.WORLD);
    }

    public final DoubleSetting emit = doubleSetting("time", "客户端时间", 0.0, -20000.0, 20000.0);

    public long getEmit() {
        return emit.get().longValue();
    }
}