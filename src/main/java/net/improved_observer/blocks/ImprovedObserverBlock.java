package net.improved_observer.blocks;

import net.improved_observer.block_entities.ImprovedObserverBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ImprovedObserverBlock extends ObserverBlock implements BlockEntityProvider {
    // POWERED and FACING properties in ObserverBlock class
    public static final DirectionProperty OUTPUT_FACING;

    public ImprovedObserverBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(OUTPUT_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OUTPUT_FACING, POWERED);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return super.rotate(state, rotation).with(OUTPUT_FACING, rotation.rotate(state.get(OUTPUT_FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState placementState = super.getPlacementState(ctx);
        if (placementState == null) {
            placementState = stateManager.getDefaultState();
        }

        // Output faces opposite of input
        Direction outputDir = placementState.get(FACING).getOpposite();
        return placementState.with(OUTPUT_FACING, outputDir);
    }

    @Override
    protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Direction outputDirection = state.get(OUTPUT_FACING);
        BlockPos outputBlockPos = pos.offset(outputDirection);
        world.updateNeighbor(outputBlockPos, this, pos);
        world.updateNeighborsExcept(outputBlockPos, this, outputDirection.getOpposite());
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) && state.get(OUTPUT_FACING).getOpposite() == direction ? 15 : 0;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Direction sideHit = hit.getSide();
        // Set output side when not sneaking with empty hand
        if (!player.isSneaking() && player.getMainHandStack().isEmpty() && sideHit != state.get(FACING)) {
            world.setBlockState(pos, state.with(OUTPUT_FACING, sideHit));
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new ImprovedObserverBlockEntity();
    }

    static {
        OUTPUT_FACING = DirectionProperty.of("output_facing",
                Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);
    }
}
