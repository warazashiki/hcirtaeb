package c.e.beatrich.module.modules.world;

import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import c.e.beatrich.setting.types.BoolSetting;
import c.e.beatrich.setting.types.DoubleSetting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class FullBright extends Module {

    public final DoubleSetting override = doubleSetting("gamma", "伽马值", 1.0, 0.0, 1.0);
    public static final BoolSetting GammaOverride = new BoolSetting("GammaOverride", "伽马覆写", false);
    public final BoolSetting NightVision = boolSetting("NightVision", "夜视", true);

    private double previousGamma;

    public FullBright() {
        super("FullBright", "夜视", Category.WORLD);
        addSetting(GammaOverride);
    }
    public static boolean enabled() {
        return ModuleManager.get().getByName("FullBright")
                .map(Module::isActive).orElse(false);
    }
    @Override
    public void onTick(ClientTickEvent.Post event) {
        if (mc.player == null) return;
        if (GammaOverride.get()) mc.options.gamma().set(override.get());
        if (NightVision.get()) mc.player.addEffect(
                new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false, false));
    }

    @Override
    public void onActivate() {
        if (mc.player == null) return;
        previousGamma = mc.options.gamma().get();
    }

    @Override
    public void onDeactivate() {
        if (mc.player != null) {
            mc.player.removeEffect(MobEffects.NIGHT_VISION);
        }
        mc.options.gamma().set(previousGamma);
    }
}
