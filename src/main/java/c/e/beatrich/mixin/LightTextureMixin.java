package c.e.beatrich.mixin;

import c.e.beatrich.module.modules.render.XRay;
import c.e.beatrich.module.modules.world.FullBright;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {
    @ModifyArgs(method = "updateLightTexture", at = @At(value = "INVOKE",
        target = "Lcom/mojang/blaze3d/platform/NativeImage;setPixelRGBA(III)V"))
    private void updateLightTexture(Args args) {
        if ((FullBright.enabled() && FullBright.GammaOverride.get()) || XRay.enabled()) {
            args.set(2, 0xFFFFFFFF);
        }
    }
}
