package net.improved_observer.block_entities;

import net.improved_observer.ImprovedObserverMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

public class ImprovedObserverBlockEntity extends BlockEntity {
    private int delay = 1;
    private int pulseLength = 1;
    private boolean repeaterMode = false;

    public ImprovedObserverBlockEntity() {
        super(ImprovedObserverMod.improvedObserverBlockEntity);
    }

    // Serialize - call markDirty() force call
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        // Save the current value of the number to the tag
        tag.putInt("delay", delay);
        tag.putInt("pulseLength", pulseLength);
        tag.putBoolean("repeaterMode", repeaterMode);

        return tag;
    }

    // Deserialize
    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        delay = tag.getInt("delay");
        pulseLength = tag.getInt("pulseLength");
        repeaterMode = tag.getBoolean("repeaterMode");
    }
}