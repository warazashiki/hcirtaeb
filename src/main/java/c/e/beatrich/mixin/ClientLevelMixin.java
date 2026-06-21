package c.e.beatrich.mixin;

import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import c.e.beatrich.module.modules.world.TimeChanger;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    @ModifyVariable(method = "setDayTime", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private long onSetDayTime(long time) {
        for (Module m : ModuleManager.get().getActive()) {
            if (m instanceof TimeChanger) {
                return ((TimeChanger) m).getEmit();
            }
        }
        return time;
    }
}
