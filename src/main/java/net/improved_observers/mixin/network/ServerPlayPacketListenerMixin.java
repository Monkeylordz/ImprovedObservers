package net.improved_observers.mixin.network;

import net.improved_observers.network.listener.AdvancedObserverServerPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayPacketListener.class)
public interface ServerPlayPacketListenerMixin extends AdvancedObserverServerPlayPacketListener {

}