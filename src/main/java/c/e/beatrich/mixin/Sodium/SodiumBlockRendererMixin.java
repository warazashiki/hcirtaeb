package c.e.beatrich.mixin.Sodium;

import c.e.beatrich.module.modules.render.XRay;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockRenderer.class)
public abstract class SodiumBlockRendererMixin {
    @Inject(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/model/color/ColorProviderRegistry;getColorProvider(Lnet/minecraft/world/level/block/Block;)Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;", shift = At.Shift.AFTER), cancellable = true)
    private void onRenderModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo info) {
        if (XRay.enabled() && XRay.isBlockOutList(state, pos)) info.cancel();
    }
}