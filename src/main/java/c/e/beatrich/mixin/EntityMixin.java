package c.e.beatrich.mixin;

import c.e.beatrich.module.modules.movement.NoFall;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyReturnValue(method = "isSuppressingBounce", at = @At("RETURN"))
    private boolean cancelBounce(boolean original) {
        return (NoFall.enabled() && NoFall.NoBounce.get()) || original;
    }
}
