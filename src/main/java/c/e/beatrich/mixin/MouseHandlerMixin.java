package c.e.beatrich.mixin;

import c.e.beatrich.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 鼠标输入 Mixin — 将鼠标按键事件分发给模块管理器。
 * <p>
 * 使鼠标按键（左/右/中/侧键）也能触发模块快捷键。
 * </p>
 */
@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void onMousePress(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (windowPointer != mc.getWindow().getWindow()) return;
        if (action != 1) return; // GLFW_PRESS = 1

        if (ModuleManager.get().onKeyPress(button)) {
            ci.cancel();
        }
    }
}
