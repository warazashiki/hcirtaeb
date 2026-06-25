package c.e.beatrich.mixin;

import c.e.beatrich.module.modules.render.NoRender;
import c.e.beatrich.module.modules.render.XRay;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void onSetupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean shouldCreateFog, float partialTick, CallbackInfo ci) {
        if ((NoRender.enabled() && NoRender.NoFog.get()) || XRay.enabled()) {
            if (fogMode == FogRenderer.FogMode.FOG_TERRAIN) {
                RenderSystem.setShaderFogStart(farPlaneDistance * 4);
                RenderSystem.setShaderFogEnd(farPlaneDistance * 4.35f);
            }
        }
    }
    @Inject(method = "getPriorityFogFunction", at = @At("HEAD"), cancellable = true)
    private static void onGetPriorityFogFunction(Entity entity, float partialTick, CallbackInfoReturnable<Object> info) {
        if (NoRender.enabled() && NoRender.NoBlindness.get()) info.setReturnValue(null);
    }
}
