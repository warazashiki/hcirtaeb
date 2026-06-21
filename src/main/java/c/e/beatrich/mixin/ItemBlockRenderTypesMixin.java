package c.e.beatrich.mixin;

import c.e.beatrich.module.modules.render.XRay;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin {
    @Inject(method = "getChunkRenderType", at = @At("HEAD"), cancellable = true)
    private static void onGetChunkRenderType(BlockState state, CallbackInfoReturnable<RenderType> info) {
        if (XRay.enabled() && XRay.isBlockOutList(state, null)) info.setReturnValue(RenderType.translucent());
    }
}
