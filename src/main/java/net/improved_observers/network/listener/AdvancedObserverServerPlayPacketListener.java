package net.improved_observers.network.listener;

import net.improved_observers.network.packet.c2s.play.UpdateAdvancedObserverC2SPacket;

public interface AdvancedObserverServerPlayPacketListener {
    void onAdvancedObserverUpdate(UpdateAdvancedObserverC2SPacket packet);
}
