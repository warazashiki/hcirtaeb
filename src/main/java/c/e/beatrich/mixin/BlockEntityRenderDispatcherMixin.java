package c.e.beatrich.mixin;

import c.e.beatrich.event.events.RenderBlockEntityEvent;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void onRender(
            E blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        RenderBlockEntityEvent event = new RenderBlockEntityEvent(blockEntity);
        for (Module m : ModuleManager.get().getActive()) m.onRenderBlockEntity(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
