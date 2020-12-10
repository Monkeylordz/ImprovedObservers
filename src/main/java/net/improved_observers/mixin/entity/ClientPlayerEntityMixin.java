package net.improved_observers.mixin.entity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.improved_observers.block.entity.AdvancedObserverBlockEntity;
import net.improved_observers.client.gui.screen.ingame.AdvancedObserverScreen;
import net.improved_observers.entity.AdvancedObserverUser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements AdvancedObserverUser {

    @Final
    @Shadow
    protected MinecraftClient client;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    public void openAdvancedObserverScreen(AdvancedObserverBlockEntity advancedObserver) {
        this.client.openScreen(new AdvancedObserverScreen(advancedObserver));
    }
}
