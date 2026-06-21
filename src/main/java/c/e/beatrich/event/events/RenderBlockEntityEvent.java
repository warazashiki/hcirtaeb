package c.e.beatrich.event.events;

import net.minecraft.world.level.block.entity.BlockEntity;

public class RenderBlockEntityEvent {
    public BlockEntity blockEntity;
    private boolean cancelled = false;
    public RenderBlockEntityEvent(BlockEntity e) { this.blockEntity = e; }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    public boolean isCancelled() {
        return cancelled;
    }
    public void cancel() {
        this.cancelled = true;
    }
}
