package net.improved_observers.mixin.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.improved_observers.block.entity.AdvancedObserverBlockEntity;
import net.improved_observers.network.listener.AdvancedObserverServerPlayPacketListener;
import net.improved_observers.network.packet.c2s.play.UpdateAdvancedObserverC2SPacket;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener {
    @Shadow
    public ServerPlayerEntity player;

    public void onAdvancedObserverUpdate(UpdateAdvancedObserverC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
        BlockPos blockPos = packet.getBlockPos();
        BlockState blockState = this.player.world.getBlockState(blockPos);
        BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
        if (blockEntity instanceof AdvancedObserverBlockEntity) {
            AdvancedObserverBlockEntity advancedObserverBlockEntity = (AdvancedObserverBlockEntity)blockEntity;
            advancedObserverBlockEntity.setOutputDirection(packet.getOutputDirectionIndex());
            advancedObserverBlockEntity.setDelay(packet.getDelay());
            advancedObserverBlockEntity.setPulseLength(packet.getPulseLength());
            advancedObserverBlockEntity.setRepeaterMode(packet.getRepeaterMode());
            advancedObserverBlockEntity.markDirty();
            this.player.world.updateListeners(blockPos, blockState, blockState, 3);
        }
    }
}

