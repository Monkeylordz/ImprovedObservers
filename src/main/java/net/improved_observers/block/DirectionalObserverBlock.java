package net.improved_observers.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DirectionalObserverBlock extends ObserverBlock {
    public static final DirectionProperty OUTPUT_FACING;

    public DirectionalObserverBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(OUTPUT_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(OUTPUT_FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return super.rotate(state, rotation).with(OUTPUT_FACING, rotation.rotate(state.get(OUTPUT_FACING)));
    }

    // Output faces opposite of input by default
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState placementState = super.getPlacementState(ctx);
        if (placementState == null) {
            placementState = stateManager.getDefaultState();
        }
        Direction outputDir = placementState.get(FACING).getOpposite();
        return placementState.with(OUTPUT_FACING, outputDir).with(POWERED, false);
    }

    // Update other Directional/Advanced observers when placed
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        world.updateNeighborsAlways(pos, state.getBlock());
    }

    // Similar to ObserverBlock, but changes output direction
    @Override
    protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Direction outputDirection = state.get(OUTPUT_FACING);
        BlockPos outputBlockPos = pos.offset(outputDirection);
        world.updateNeighbor(outputBlockPos, this, pos);
        world.updateNeighborsExcept(outputBlockPos, this, outputDirection.getOpposite());
    }

    // Similar to ObserverBlock, but changes output direction
    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) && state.get(OUTPUT_FACING).getOpposite() == direction ? 15 : 0;
    }

    // Set output side when hit with empty hand
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Direction sideHit = hit.getSide();
        if (player.getStackInHand(hand).isEmpty() && sideHit != state.get(FACING) && player.canModifyBlocks()) {
            world.setBlockState(pos, state.with(OUTPUT_FACING, sideHit));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    static {
        OUTPUT_FACING = DirectionProperty.of("output_facing",
                Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);
    }
}
