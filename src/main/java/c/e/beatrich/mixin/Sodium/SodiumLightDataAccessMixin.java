package c.e.beatrich.mixin.Sodium;

import c.e.beatrich.module.modules.render.XRay;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LightDataAccess.class, remap = false)
public abstract class SodiumLightDataAccessMixin {
    @Shadow
    protected BlockAndTintGetter level;
    @Shadow @Final
    private BlockPos.MutableBlockPos pos;

    @ModifyVariable(method = "compute", at = @At(value = "TAIL"), name = "bl")
    private int compute_modifyBL(int bl) {
        if (XRay.enabled()) {
            BlockState state = level.getBlockState(pos);
            if (!XRay.isBlockOutList(state, pos)) return 15 | 15 << 4 | 15 << 8;
        }

        return bl;
    }
}
