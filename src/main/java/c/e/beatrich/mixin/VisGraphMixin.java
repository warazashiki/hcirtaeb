package c.e.beatrich.mixin;

import c.e.beatrich.event.events.VisGraphEvent;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public abstract class VisGraphMixin {
    @Inject(method = "setOpaque", at = @At("HEAD"), cancellable = true)
    private void onSetOpaque(BlockPos pos, CallbackInfo ci) {
        VisGraphEvent event = new VisGraphEvent();
        for (Module m : ModuleManager.get().getActive()) m.onVisGraph(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
