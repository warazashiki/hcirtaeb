package c.e.beatrich.mixin.Sodium;

import c.e.beatrich.module.modules.render.XRay;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(value = BlockOcclusionCache.class, remap = false)
public abstract class SodiumBlockOcclusionCacheMixin {
    @ModifyReturnValue(method = "shouldDrawSide", at = @At("RETURN"))
    private boolean shouldDrawSide(boolean original, BlockState selfState, BlockGetter view, BlockPos selfPos, Direction facing) {
        if (XRay.enabled()) {
            return XRay.RenderFace(selfState, view, facing, selfPos, original);
        }
        return original;
    }
}