package c.e.beatrich.event;

import c.e.beatrich.event.events.ModuleToggleEvent;
import c.e.beatrich.event.events.KeyPressEvent;
import c.e.beatrich.event.events.SendMessageEvent;
import c.e.beatrich.event.events.RenderBlockEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {

    private static EventBus INSTANCE;

    // === 监听器列表 ===
    private final List<Consumer<ModuleToggleEvent>> moduleToggleListeners = new ArrayList<>();
    private final List<Consumer<KeyPressEvent>> keyPressListeners = new ArrayList<>();
    private final List<Consumer<SendMessageEvent>> sendMessageListeners = new ArrayList<>();
    private final List<Consumer<RenderBlockEntityEvent>> renderListeners = new ArrayList<>();

    private EventBus() {}

    public static EventBus get() {
        if (INSTANCE == null) {
            INSTANCE = new EventBus();
        }
        return INSTANCE;
    }

    // === 订阅方法 ===

    public void onModuleToggle(Consumer<ModuleToggleEvent> listener) {
        moduleToggleListeners.add(listener);
    }

    public void onKeyPress(Consumer<KeyPressEvent> listener) {
        keyPressListeners.add(listener);
    }

    public void onSendMessage(Consumer<SendMessageEvent> listener) {
        sendMessageListeners.add(listener);
    }

    public void onRender(Consumer<RenderBlockEntityEvent> listener) {
        renderListeners.add(listener);
    }

    // === 发布方法（占位实现，事件仅打印日志或简单处理） ===

    public void postModuleToggle(ModuleToggleEvent event) {
        for (var listener : moduleToggleListeners) {
            listener.accept(event);
        }
    }

    public boolean postKeyPress(KeyPressEvent event) {
        for (var listener : keyPressListeners) {
            listener.accept(event);
        }
        return false; // 占位
    }

    public boolean postSendMessage(SendMessageEvent event) {
        for (var listener : sendMessageListeners) {
            listener.accept(event);
        }
        return event.isCancelled();
    }

    public void postRender(RenderBlockEntityEvent event) {
        for (var listener : renderListeners) {
            listener.accept(event);
        }
    }
}
