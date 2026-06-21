package c.e.beatrich.mixin;

import c.e.beatrich.event.events.PacketEvent;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V",
            at = @At("HEAD"), cancellable = true)
    public void onSend(Packet<?> packet, PacketSendListener listener, CallbackInfo ci) {
        PacketEvent event = new PacketEvent(packet);
        for (Module m : ModuleManager.get().getActive()) m.onPacketSend(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
            at = @At("HEAD"), cancellable = true)
    public void onChannelRead0(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        PacketEvent event = new PacketEvent(packet);
        for (Module m : ModuleManager.get().getActive()) m.onPacketReceive(event);
        if (event.isCancelled()) ci.cancel();
    }
}
