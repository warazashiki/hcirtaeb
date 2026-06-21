package c.e.beatrich.mixin;

import c.e.beatrich.module.modules.render.NoRender;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "getPriorityFogFunction", at = @At("HEAD"), cancellable = true)
    private static void onGetPriorityFogFunction(Entity entity, float partialTick, CallbackInfoReturnable<Object> info) {
        if (NoRender.enabled() && NoRender.NoBlindness.get()) info.setReturnValue(null);
    }
}
