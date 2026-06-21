package c.e.beatrich.event.events;

/**
 * 发送聊天消息事件 — 可用于命令系统拦截
 */
public class SendMessageEvent {
    public final String message;
    private boolean cancelled = false;

    public SendMessageEvent(String message) {
        this.message = message;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }
}
