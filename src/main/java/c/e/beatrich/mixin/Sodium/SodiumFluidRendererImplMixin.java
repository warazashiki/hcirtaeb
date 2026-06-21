package c.e.beatrich.mixin.Sodium;

import c.e.beatrich.module.modules.render.XRay;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.neoforge.render.FluidRendererImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FluidRendererImpl.class, remap = false)
public abstract class SodiumFluidRendererImplMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(LevelSlice level,
                          BlockState blockState,
                          FluidState fluidState,
                          BlockPos blockPos,
                          BlockPos offset,
                          TranslucentGeometryCollector collector,
                          ChunkBuildBuffers buffers,
                          CallbackInfo info) {
        if (XRay.enabled() && XRay.isBlockOutList(fluidState.createLegacyBlock(), blockPos)) info.cancel();
    }
}
