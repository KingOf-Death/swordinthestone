package com.bonker.swordinthestone.common.block;

import com.bonker.swordinthestone.common.block.blockentity.SSBlockEntities;
import com.bonker.swordinthestone.common.block.blockentity.SwordStoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class SwordStoneBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty HAS_SWORD = BooleanProperty.create("has_sword");
    private static final VoxelShape BASE = Block.box(0, 0, 0, 16, 9, 16);
    private static final VoxelShape MIDDLE_0 = Block.box(4, 9, 4, 16, 15, 16);
    private static final VoxelShape TOP_0 = Block.box(12, 15, 12, 16, 16, 16);
    private static final VoxelShape MIDDLE_90 = Block.box(0, 9, 4, 12, 15, 16);
    private static final VoxelShape TOP_90 = Block.box(0, 15, 12, 4, 16, 16);
    private static final VoxelShape MIDDLE_180 = Block.box(0, 9, 0, 12, 15, 12);
    private static final VoxelShape TOP_180 = Block.box(0, 15, 0, 4, 16, 4);
    private static final VoxelShape MIDDLE_270 = Block.box(4, 9, 0, 16, 15, 12);
    private static final VoxelShape TOP_270 = Block.box(12, 15, 0, 16, 16, 4);
    private static final VoxelShape SHAPE_0 = Shapes.or(BASE, MIDDLE_0, TOP_0);
    private static final VoxelShape SHAPE_90 = Shapes.or(BASE, MIDDLE_90, TOP_90);
    private static final VoxelShape SHAPE_180 = Shapes.or(BASE, MIDDLE_180, TOP_180);
    private static final VoxelShape SHAPE_270 = Shapes.or(BASE, MIDDLE_270, TOP_270);

    protected SwordStoneBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> interact(pLevel, pPos, pPlayer, pHand);
            case EAST -> interact(pLevel, pPos.west(), pPlayer, pHand);
            case SOUTH -> interact(pLevel, pPos.north().west(), pPlayer, pHand);
            case WEST -> interact(pLevel, pPos.north(), pPlayer, pHand);
            default -> InteractionResult.PASS;
        };
    }

    private InteractionResult interact(Level level, BlockPos pos, Player player, InteractionHand usedHand) {
        return level.getBlockEntity(pos, SSBlockEntities.SWORD_STONE.get())
                .map(swordStoneBlockEntity -> swordStoneBlockEntity.interact(player, usedHand))
                .orElse(InteractionResult.PASS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, HAS_SWORD);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            default -> SHAPE_0;
            case EAST -> SHAPE_90;
            case SOUTH -> SHAPE_180;
            case WEST -> SHAPE_270;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return pState.getValue(FACING) == Direction.NORTH ? new SwordStoneBlockEntity(pPos, pState) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, SSBlockEntities.SWORD_STONE.get(), SwordStoneBlockEntity::tick);
    }
}
