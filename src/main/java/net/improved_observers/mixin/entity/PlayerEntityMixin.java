package net.improved_observers.mixin.entity;

import net.improved_observers.block.entity.AdvancedObserverBlockEntity;
import net.improved_observers.entity.AdvancedObserverUser;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements AdvancedObserverUser {
    @Override
    public void openAdvancedObserverScreen(AdvancedObserverBlockEntity advancedObserver) {
    }
}
