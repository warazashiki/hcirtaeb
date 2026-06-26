package c.e.beatrich.mixin;

import c.e.beatrich.mixininterface.IServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerboundMovePlayerPacket.class)
public class ServerboundMovePlayerPacketMixin implements IServerboundMovePlayerPacket {
    @Unique
    private int hcirtaeb$tag;

    @Override
    public void hcirtaeb$setTag(int tag) { this.hcirtaeb$tag = tag; }

    @Override
    public int hcirtaeb$getTag() { return this.hcirtaeb$tag; }
}
