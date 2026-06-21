package c.e.beatrich.mixin;

import c.e.beatrich.module.ModuleManager;
import c.e.beatrich.module.modules.combat.Criticals;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttackHead(Player player, Entity targetEntity, CallbackInfo ci) {
        ModuleManager.get().getByName("Criticals").ifPresent(module -> {
            if (module instanceof Criticals c) {
                c.preAttack(player, targetEntity);
            }
        });
    }

    @Inject(method = "attack", at = @At("TAIL"))
    private void onAttackTail(Player player, Entity targetEntity, CallbackInfo ci) {
        ModuleManager.get().getByName("Criticals").ifPresent(module -> {
            if (module instanceof Criticals c) {
                c.postAttack();
            }
        });
    }
}