package com.bonker.swordinthestone.common.block.entity;

import com.bonker.swordinthestone.common.block.SwordStoneBlock;
import com.bonker.swordinthestone.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface ISwordStoneBlockEntity {
    InteractionResult interact(Player pPlayer, InteractionHand pHand);

    @Nullable
    SwordStoneMasterBlockEntity getMaster();

    static List<BlockPos> getBlocks(BlockPos pos, BlockState state) {
        if (!state.hasProperty(SwordStoneBlock.FACING)) return List.of();
        Direction dir = state.getValue(SwordStoneBlock.FACING).getClockWise();
        return Util.betweenClosed(pos, pos.relative(dir).relative(dir.getClockWise()));
    }

    static Optional<BlockPos> getMasterPos(Level level, BlockPos pos) {
        List<BlockPos> blocks = getBlocks(pos, level.getBlockState(pos));
        for (BlockPos blockPos : blocks) {
            BlockState state = level.getBlockState(blockPos);
            if (state.hasProperty(SwordStoneBlock.IS_DUMMY) && !state.getValue(SwordStoneBlock.IS_DUMMY)) {
                return Optional.of(blockPos);
            }
        }
        return Optional.empty();
    }

    static Optional<SwordStoneMasterBlockEntity> getMaster(Level level, BlockPos pos) {
        return getMasterPos(level, pos).flatMap(blockPos -> level.getBlockEntity(blockPos, SSBlockEntities.SWORD_STONE_MASTER.get()));
    }
}
