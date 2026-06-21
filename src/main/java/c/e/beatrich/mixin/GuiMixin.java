package c.e.beatrich.mixin;

import c.e.beatrich.gui.HudRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * GUI Mixin — 在游戏 HUD 渲染后绘制 hcirtaeb 的 HUD 叠加层
 */
@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        HudRenderer.render(guiGraphics, deltaTracker);
    }
}
