package c.e.beatrich.event.events;

import net.minecraft.network.protocol.Packet;

/**
 * 发包事件 — 在 {@code Connection.send()} 被调用时触发。
 * 设置 cancelled=true 可阻止该包发出。
 */
public class PacketEvent {

    public final Packet<?> packet;
    private boolean cancelled;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
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
