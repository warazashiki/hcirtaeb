package c.e.beatrich.mixin;

import c.e.beatrich.event.events.GetShadeBrightnessEvent;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
    private void onGetShadeBrightness(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> info) {
        GetShadeBrightnessEvent event = new GetShadeBrightnessEvent();
        for (Module m : ModuleManager.get().getActive()) m.onGetShadeBrightness(event);
        if (-1 != event.lightLevel) info.setReturnValue(event.lightLevel);
    }
}
