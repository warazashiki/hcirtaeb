package c.e.beatrich.mixin;

import c.e.beatrich.module.modules.player.Multitask;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow@Final
    public Options options;
    @Shadow@Nullable
    public LocalPlayer player;
    @Shadow@Nullable
    public MultiPlayerGameMode gameMode;
    @ModifyExpressionValue(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isDestroying()Z"))
    private boolean startUseItemModifyIsDestorying(boolean orginal) {
        return !Multitask.enabled() && orginal;
    }
    @ModifyExpressionValue(method = "continueAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean continueAttackModifyIsUsingItem(boolean orginal) {
        return !Multitask.enabled() && orginal;
    }
    @ModifyExpressionValue(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z", ordinal = 0))
    private boolean handleKeybindsModifyIsUsingItem(boolean orginal) {
        return !(Multitask.enabled() && Multitask.Attack.get()) && orginal;
    }
    @Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void handleKeybindsInjectIsusingItem(CallbackInfo ci) {
        if (Multitask.enabled() && Multitask.Attack.get() && player.isUsingItem()) {
            if (!options.keyUse.isDown()) gameMode.releaseUsingItem(player);
            while (options.keyUse.consumeClick());
        }
    }
}
