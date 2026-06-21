package c.e.beatrich.mixin;

import c.e.beatrich.module.modules.render.XRay;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {

    private static final String TESS_DESC =
        "(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;"
        + "Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;"
        + "Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        + "ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;"
        + "Lnet/minecraft/client/renderer/RenderType;)V";

    @Inject(method = {"tesselateWithAO" + TESS_DESC, "tesselateWithoutAO" + TESS_DESC},
            at = @At("HEAD"), cancellable = true)
    private void onTesselate(BlockAndTintGetter level, BakedModel model, BlockState state,
                              BlockPos pos, PoseStack poseStack, VertexConsumer consumer,
                              boolean checkSides, RandomSource random, long seed,
                              int packedOverlay, net.neoforged.neoforge.client.model.data.ModelData modelData,
                              net.minecraft.client.renderer.RenderType renderType, CallbackInfo ci) {
        if (XRay.enabled() && XRay.isBlockOutList(state, pos)) ci.cancel();
    }
}
