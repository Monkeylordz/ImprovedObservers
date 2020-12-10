package net.improved_observers.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.improved_observers.ImprovedObserversMod;
import net.improved_observers.block.AdvancedObserverBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class AdvancedObserverBlockEntity extends BlockEntity {
    // We need this enum because packets can't send Directions, so we need to send indexes instead
    public enum FacingDirection {
        NORTH (0, Direction.NORTH),
        SOUTH (1, Direction.SOUTH),
        EAST (2, Direction.EAST),
        WEST (3, Direction.WEST),
        UP (4, Direction.UP),
        DOWN (5, Direction.DOWN);
        private final int index;
        private final Direction direction;
        FacingDirection(int index, Direction direction) {
            this.index = index;
            this.direction = direction;
        }
        public int getIndex() {
            return index;
        }
        public Direction getDirection() {
            return direction;
        }
        public static FacingDirection getFacingDirectionOfIndex(int index) {
            for (FacingDirection dir : FacingDirection.values()) {
                if (dir.index == index) {
                    return dir;
                }
            }
            return NORTH;
        }
        public static FacingDirection getFacingDirectionOfDirection(Direction direction) {
            for (FacingDirection facingDirection : FacingDirection.values()) {
                if (facingDirection.direction.equals(direction)) {
                    return facingDirection;
                }
            }
            return NORTH;
        }
    }

    private FacingDirection inputDirection;
    private FacingDirection outputDirection;
    private int delay;
    private int pulseLength;
    private boolean repeaterMode;

    public AdvancedObserverBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
        inputDirection = FacingDirection.SOUTH;
        outputDirection = FacingDirection.NORTH;
        delay = 1;
        pulseLength = 1;
        repeaterMode = false;
    }

    public AdvancedObserverBlockEntity() {
        this(ImprovedObserversMod.ADVANCED_OBSERVER_BLOCK_ENTITY);
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 0, this.toInitialChunkDataTag());
    }

    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    public void setInputDirection (Direction direction) {
        inputDirection =  FacingDirection.getFacingDirectionOfDirection(direction);
    }

    public void setOutputDirection(Direction direction) {
        setOutputDirection(FacingDirection.getFacingDirectionOfDirection(direction).getIndex());
    }

    public void setOutputDirection (int directionIndex) {
        FacingDirection direction = FacingDirection.getFacingDirectionOfIndex(directionIndex);
        if (direction != inputDirection) {
            outputDirection = direction;
            this.world.setBlockState(pos, this.world.getBlockState(this.pos)
                    .with(AdvancedObserverBlock.OUTPUT_FACING, this.outputDirection.getDirection()));
        }
    }

    public void setDelay(int delay) {
        if (delay > 0 && delay <= 20) {
            this.delay = delay;
        }
    }

    public void setPulseLength(int pulseLength) {
        if (pulseLength > 0 && pulseLength <= 20) {
            this.pulseLength = pulseLength;
        }
    }

    public void setRepeaterMode(boolean repeaterMode) {
        this.repeaterMode = repeaterMode;
    }

    @Environment(EnvType.CLIENT)
    public FacingDirection getInputDirection() {
        return inputDirection;
    }

    @Environment(EnvType.CLIENT)
    public FacingDirection getOutputDirection() {
        return outputDirection;
    }

    @Environment(EnvType.CLIENT)
    public int getDelay() {
        return delay;
    }

    @Environment(EnvType.CLIENT)
    public int getPulseLength() {
        return pulseLength;
    }

    @Environment(EnvType.CLIENT)
    public boolean getRepeaterMode() {
        return repeaterMode;
    }

    // Serialize
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("inputDirection", inputDirection.getIndex());
        tag.putInt("outputDirection", outputDirection.getIndex());
        tag.putInt("delay", delay);
        tag.putInt("pulseLength", pulseLength);
        tag.putBoolean("repeaterMode", repeaterMode);

        return tag;
    }

    // Deserialize
    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        inputDirection = FacingDirection.getFacingDirectionOfIndex(tag.getInt("inputDirection"));
        outputDirection = FacingDirection.getFacingDirectionOfIndex(tag.getInt("outputDirection"));
        delay = tag.getInt("delay");
        pulseLength = tag.getInt("pulseLength");
        repeaterMode = tag.getBoolean("repeaterMode");
    }
}