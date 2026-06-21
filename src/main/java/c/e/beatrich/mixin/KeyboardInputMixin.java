package c.e.beatrich.mixin;

import c.e.beatrich.module.ModuleManager;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 键盘输入 Mixin — 将按键事件分发给模块管理器。
 * <p>
 * 该模块的快捷键（默认 Right Shift）通过 {@link ModuleManager#onKeyPress} 触发。
 * </p>
 */
@Mixin(KeyboardHandler.class)
public abstract class KeyboardInputMixin {

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (windowPointer != mc.getWindow().getWindow()) return;
        if (action != GLFW.GLFW_PRESS) return;

        // 分发给模块管理器处理快捷键（包括 Gui 模块的 RShift → 打开 ClickGUI）
        if (ModuleManager.get().onKeyPress(key)) {
            ci.cancel();
        }
    }
}
