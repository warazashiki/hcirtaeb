package c.e.beatrich.module.modules.movement;

import c.e.beatrich.event.events.PacketEvent;
import c.e.beatrich.mixin.LocalPlayerAccessor;
import c.e.beatrich.mixin.ServerboundMovePlayerPacketAccessor;
import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import c.e.beatrich.setting.types.BoolSetting;
import c.e.beatrich.setting.types.DoubleSetting;
import c.e.beatrich.setting.types.EnumSetting;
import c.e.beatrich.setting.types.IntSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.minecraft.world.entity.Entity;

public class Flight extends Module {

    public static EnumSetting<mode> Mode = new EnumSetting<>("Mode", "模式", mode.Abilities);
    public static EnumSetting<antikick> AntiKick = new EnumSetting<>("Mode", "模式", antikick.Packet);
    public DoubleSetting Speed = doubleSetting("Speed", "Speed", 0.1, 0.0, 1.0);
    public BoolSetting VSpeedMatch = boolSetting("VSpeedMatch", "VSpeedMatch", false);
    public IntSetting Delay = intSetting("Delay", "间隔", 20, 1, 200);
    public IntSetting OffTime = intSetting("OffTime", "OffTime", 1, 1, 200);
    public BoolSetting NoSneak = boolSetting("NoSneak", "NoSneak", false);
    private int delayLeft = Delay.get();
    private int offLeft = OffTime.get();
    private boolean flip;
    private float lastYRot;
    private double lastPacketY = Double.MAX_VALUE;
    public Flight() {
        super("Flight", "飞行", Category.MOVEMENT);
        addSetting(Mode);
    }
    @Override
    public void onPreTick(ClientTickEvent.Pre event) {
        if (mc.player == null) return;
        float currentYRot = mc.player.getYRot();
        if (mc.player.fallDistance >= 3f && currentYRot == lastYRot && mc.player.getDeltaMovement().length() < 0.003d) {
            mc.player.setYRot(currentYRot + (flip ? 1 : -1));
            flip = !flip;
        }
        lastYRot = currentYRot;
    }
    @Override
    public void onTick(ClientTickEvent.Post event) {
        if (mc.player == null) return;
        if (delayLeft > 0) delayLeft--;
        if (offLeft <= 0 && delayLeft <= 0) {
            delayLeft = Delay.get();
            offLeft = OffTime.get();
            if (AntiKick.get() == antikick.Packet) {
                // Resend movement packets
                ((LocalPlayerAccessor) mc.player).setPoSitionReminder(20);
            }
        } else if (delayLeft <= 0) {
            boolean shouldReturn = false;
            if (AntiKick.get() == antikick.Normal) {
                if (Mode.get() == mode.Abilities) {
                    abilitiesOff();
                    shouldReturn = true;
                }
            } else if (AntiKick.get() == antikick.Packet && offLeft == OffTime.get()) {
                // Resend movement packets
                ((LocalPlayerAccessor) mc.player).setPoSitionReminder(20);
            }
            offLeft--;
            if (shouldReturn) return;
        }
        if (mc.player.getYRot() != lastYRot) mc.player.setYRot(lastYRot);
        switch (Mode.get()) {
            case Velocity -> {
                mc.player.getAbilities().flying = false;
                mc.player.setDeltaMovement(0, 0, 0);
                Vec3 playerDeltaMovement = mc.player.getDeltaMovement();
                if (mc.options.keyJump.isDown())
                    playerDeltaMovement = playerDeltaMovement.add(0, Speed.get() * (VSpeedMatch.get() ? 10f : 5f), 0);
                if (mc.options.keyShift.isDown())
                    playerDeltaMovement = playerDeltaMovement.subtract(0, Speed.get() * (VSpeedMatch.get() ? 10f : 5f), 0);
                mc.player.setDeltaMovement(playerDeltaMovement);
                if (NoSneak.get()) {
                    mc.player.setOnGround(false);
                }
            }
            case Abilities -> {
                if (mc.player.isSpectator()) return;
                mc.player.getAbilities().setFlyingSpeed(Speed.get().floatValue());
                mc.player.getAbilities().flying = true;
                if (mc.player.getAbilities().instabuild) return;
                mc.player.getAbilities().mayfly = true;
            }
        }
    }
    private void antiKickPacket(ServerboundMovePlayerPacket packet, double currentY) {
        // maximum time we can be "floating" is 80 ticks, so 4 seconds max
        if (this.delayLeft <= 0 && this.lastPacketY != Double.MAX_VALUE && shouldFlyDown(currentY, this.lastPacketY) && isEntityOnAir(mc.player))
            // actual check is for >= -0.03125D, but we have to do a bit more than that
            // due to the fact that it's a bigger or *equal* to, and not just a bigger than
            ((ServerboundMovePlayerPacketAccessor) packet).setY(lastPacketY - 0.03130D);
        else lastPacketY = currentY;
    }
    private boolean shouldFlyDown(double currentY, double lastY) {
        if (currentY >= lastY) return true;
        else return lastY - currentY < 0.03130D;
    }
    // Copied from ServerPlayNetworkHandler#isEntityOnAir
    private boolean isEntityOnAir(Entity entity) {
        return entity.getCommandSenderWorld().getBlockStates(entity.getBoundingBox().inflate(0.0625).expandTowards(0.0, -0.55, 0.0)).allMatch(BlockBehaviour.BlockStateBase::isAir);
    }
    @Override
    public void onPacketSend(PacketEvent event) {
        if (!(event.packet instanceof ServerboundMovePlayerPacket packet) || AntiKick.get() != antikick.Packet) return;
        double currentY = packet.getY(Double.MAX_VALUE);
        if (currentY != Double.MAX_VALUE) {
            antiKickPacket(packet, currentY);
        } else {
            // if the packet is a LookAndOnGround packet or an OnGroundOnly packet then we need to
            // make it a Full packet or a PositionAndOnGround packet respectively, so it has a Y value
            ServerboundMovePlayerPacket fullPacket;
            if (packet.hasRotation()) {
                fullPacket = new ServerboundMovePlayerPacket.PosRot(
                        mc.player.getX(),
                        mc.player.getY(),
                        mc.player.getZ(),
                        packet.getYRot(0),
                        packet.getXRot(0),
                        packet.isOnGround()
                );
            } else {
                fullPacket = new ServerboundMovePlayerPacket.Pos(
                        mc.player.getX(),
                        mc.player.getY(),
                        mc.player.getZ(),
                        packet.isOnGround()
                );
            }
            event.cancel();
            antiKickPacket(fullPacket, mc.player.getY());
            mc.getConnection().send(fullPacket);
        }
    }
    private void abilitiesOff() {
        mc.player.getAbilities().flying = false;
        mc.player.getAbilities().setFlyingSpeed(0.05f);
        if (mc.player.getAbilities().instabuild) return;
        mc.player.getAbilities().mayfly = false;
    }
    @Override
    public void onActivate() {
        if (mc.player == null) {deactivate();return;}
        if (Mode.get() == mode.Abilities && !mc.player.isSpectator()) {
            mc.player.getAbilities().flying = true;
        }
    }
    @Override
    public void onDeactivate() {
        if (mc.player == null) return;
        if (Mode.get() == mode.Abilities && !mc.player.isSpectator()) {
            abilitiesOff();
        }
    }
    public static boolean enabled() {
        return ModuleManager.get().getByName("Flight")
                .map(Module::isActive).orElse(false);
    }
    public enum mode {
        Abilities,
        Velocity
    }
    public enum antikick {
        Normal,
        Packet,
        None
    }
}
