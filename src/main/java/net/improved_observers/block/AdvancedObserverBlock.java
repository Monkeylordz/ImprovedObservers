package net.improved_observers.block;

import net.improved_observers.block.entity.AdvancedObserverBlockEntity;
import net.improved_observers.entity.AdvancedObserverUser;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AdvancedObserverBlock extends DirectionalObserverBlock implements BlockEntityProvider {

    public AdvancedObserverBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new AdvancedObserverBlockEntity();
    }

    // Open AdvancedObserverScreen if player is empty-handed
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).isEmpty() && player.canModifyBlocks()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AdvancedObserverBlockEntity) {
                ((AdvancedObserverUser) player).openAdvancedObserverScreen((AdvancedObserverBlockEntity)blockEntity);
                return ActionResult.success(world.isClient);
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (isRepeaterMode(world, pos)) {
            scheduledRepeaterTick(state, world, pos);
        } else {
            scheduledObserverTick(state, world, pos);
        }

        this.updateNeighbors(world, pos, state);
    }

    // Same as scheduledTick in DirectionalObserverBlock
    private void scheduledObserverTick(BlockState state, ServerWorld world, BlockPos pos) {
        if (state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, false), 2);
        } else {
            world.setBlockState(pos, state.with(POWERED, true), 2);
            int pulseLength = getPulseLength(world, pos);
            world.getBlockTickScheduler().schedule(pos, this, pulseLength);
        }
    }

    // Similar to scheduledTick in AbstractRedstoneGateBlock
    private void scheduledRepeaterTick(BlockState state, ServerWorld world, BlockPos pos) {
        boolean isPowered = state.get(POWERED);
        boolean hasPower = this.hasPower(world, pos, state);
        if (isPowered && !hasPower) {
            if (!world.getBlockTickScheduler().isScheduled(pos, this)) {
                world.setBlockState(pos, state.with(POWERED, false), 2);
            }
        } else if (!isPowered) {
            world.setBlockState(pos, state.with(POWERED, true), 2);
            if (!hasPower) {
                world.getBlockTickScheduler().schedule(pos, this, this.getPulseLength(world, pos));
            }
        }
    }

    // Same as getStateForNeighborUpdate in ObserverBlock, but checks for repeater mode
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (!isRepeaterMode(world, pos)) {
            if (state.get(FACING) == direction && !(Boolean)state.get(POWERED)) {
                this.scheduleObserverTick(world, pos);
            }
        }

        return state;
    }

    // Same as scheduleTick in ObserverBlock, but with custom delay
    private void scheduleObserverTick(WorldAccess world, BlockPos pos) {
        if (!world.isClient() && !world.getBlockTickScheduler().isScheduled(pos, this)) {
            int delay = getDelay(world, pos);
            world.getBlockTickScheduler().schedule(pos, this, delay);
        }
    }

    // Called when adjacent block has redstone update
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (isRepeaterMode(world, pos) && state.canPlaceAt(world, pos)) {
            this.updateRepeaterPowered(world, pos, state);
        }
    }

    protected void updateRepeaterPowered(World world, BlockPos pos, BlockState state) {
        if (state.get(POWERED) != this.hasPower(world, pos, state) && !world.getBlockTickScheduler().isScheduled(pos, this)) {
            world.getBlockTickScheduler().schedule(pos, this, this.getDelay(world, pos));
        }
    }

    protected int getDelay(WorldAccess world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AdvancedObserverBlockEntity) {
            return ((AdvancedObserverBlockEntity) blockEntity).getDelay() * 2;
        }
        return 2;
    }

    protected int getPulseLength(WorldAccess world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AdvancedObserverBlockEntity) {
            return ((AdvancedObserverBlockEntity) blockEntity).getPulseLength() * 2;
        }
        return 2;
    }

    protected boolean isRepeaterMode(WorldAccess world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AdvancedObserverBlockEntity) {
            return ((AdvancedObserverBlockEntity) blockEntity).getRepeaterMode();
        }
        return false;
    }

    protected boolean hasPower(World world, BlockPos pos, BlockState state) {
        return this.getPower(world, pos, state) > 0;
    }

    protected int getPower(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction);
        return world.getEmittedRedstonePower(blockPos, direction);
    }

    // Send block entity input/output directions
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AdvancedObserverBlockEntity) {
            ((AdvancedObserverBlockEntity) blockEntity).setInputDirection(state.get(FACING));
            ((AdvancedObserverBlockEntity) blockEntity).setOutputDirection(state.get(OUTPUT_FACING));
        }
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }
}
