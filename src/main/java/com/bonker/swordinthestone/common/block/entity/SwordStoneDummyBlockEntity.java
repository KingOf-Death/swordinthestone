package com.bonker.swordinthestone.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SwordStoneDummyBlockEntity extends BlockEntity implements ISwordStoneBlockEntity{
    private SwordStoneMasterBlockEntity master;

    public SwordStoneDummyBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SSBlockEntities.SWORD_STONE_DUMMY.get(), pPos, pBlockState);
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        assert level != null;

        if (master == null) ISwordStoneBlockEntity.getMaster(level, getBlockPos()).ifPresent(master -> this.master = master);

        if (master != null) {
            return master.interact(pPlayer, pHand);
        } else return InteractionResult.PASS;
    }
}
