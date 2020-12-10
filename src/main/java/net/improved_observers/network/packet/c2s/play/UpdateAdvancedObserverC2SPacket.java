package net.improved_observers.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.improved_observers.network.listener.AdvancedObserverServerPlayPacketListener;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

public class UpdateAdvancedObserverC2SPacket implements Packet<ServerPlayPacketListener> {
    private BlockPos pos;
    private int outputDirectionIndex;
    private int delay;
    private int pulseLength;
    private boolean repeaterMode;

    @Environment(EnvType.CLIENT)
    public UpdateAdvancedObserverC2SPacket(BlockPos pos, int outputDirectionIndex, int delay, int pulseLength, boolean repeaterMode) {
        this.pos = pos;
        this.outputDirectionIndex = outputDirectionIndex;
        this.delay = delay;
        this.pulseLength = pulseLength;
        this.repeaterMode = repeaterMode;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
        this.outputDirectionIndex = buf.readUnsignedByte();
        this.delay = buf.readUnsignedByte();
        this.pulseLength = buf.readUnsignedByte();
        this.repeaterMode = buf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.outputDirectionIndex);
        buf.writeByte(this.delay);
        buf.writeByte(this.pulseLength);
        buf.writeBoolean(this.repeaterMode);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ((AdvancedObserverServerPlayPacketListener) listener).onAdvancedObserverUpdate(this);
    }

    public BlockPos getBlockPos() {
        return pos;
    }

    public int getOutputDirectionIndex() {
        return outputDirectionIndex;
    }

    public int getDelay() {
        return delay;
    }

    public int getPulseLength() {
        return pulseLength;
    }

    public boolean getRepeaterMode() {
        return repeaterMode;
    }
}
