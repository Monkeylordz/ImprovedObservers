package net.improved_observers.mixin.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.improved_observers.block.entity.AdvancedObserverBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener {
    @Shadow
    private MinecraftClient client;

    @Inject(method = "onBlockEntityUpdate", at = @At("TAIL"))
    public void onBlockEntityUpdateInject(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
        BlockPos blockPos = packet.getPos();
        BlockEntity blockEntity = this.client.world.getBlockEntity(blockPos);
        if (blockEntity instanceof AdvancedObserverBlockEntity) {
            blockEntity.fromTag(this.client.world.getBlockState(blockPos), packet.getCompoundTag());
        }
    }
}
