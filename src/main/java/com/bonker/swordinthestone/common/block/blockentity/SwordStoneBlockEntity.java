package com.bonker.swordinthestone.common.block.blockentity;

import com.bonker.swordinthestone.common.block.SSBlocks;
import com.bonker.swordinthestone.common.block.SwordStoneBlock;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.bonker.swordinthestone.common.networking.ClientboundSyncSwordStoneItemPacket;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class SwordStoneBlockEntity extends BlockEntity {
    public static final String ITEM_TAG = "Item";
    public static final String VARIANT_TAG = "variant";
    public static final String FILLED_SWORD = "filled";

    public static final int ANIMATION_TIME = 10;
    public static final int REQUIRED_SHAKES = 20;
    public static final int IDLE_TIME = 15;

    private ItemStack stack = ItemStack.EMPTY;
    public int progress = 0;
    public int ticksSinceLastInteraction = 0;
    private boolean sendSyncPacket = false;
    private String variant = "";
    private final AABB renderBox;
    private final BlockPos[] blocks;

    public SwordStoneBlockEntity(BlockPos pPos, BlockState pState) {
        super(SSBlockEntities.SWORD_STONE.get(), pPos, pState);

        BlockPos pos = getBlockPos();
        Vec3 center = new Vec3(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        renderBox = AABB.ofSize(center, 1, 2, 1);

        blocks = new BlockPos[] {pPos, pPos.south(), pPos.east(), pPos.south().east()};
    }

    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        assert level != null;

        if (!getBlockState().getValue(SwordStoneBlock.HAS_SWORD)) return InteractionResult.PASS;
        if (ticksSinceLastInteraction <= ANIMATION_TIME) return InteractionResult.PASS;

        ticksSinceLastInteraction = 0;
        if (++progress >= REQUIRED_SHAKES) {
            progress = 0;
            finish();
        }

        return InteractionResult.SUCCESS;
    }

    private void finish() {
        assert level != null;

        for (BlockPos pos : blocks) {
            BlockState state = level.getBlockState(pos);
            level.addDestroyBlockEffect(pos, state);
            if (state.is(SSBlocks.SWORD_STONE.get())) {
                level.setBlock(pos, state.setValue(SwordStoneBlock.HAS_SWORD, false), Block.UPDATE_ALL);
            }
        }

        Vec3 position = new Vec3(getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1);
        ItemEntity item = new ItemEntity(level, position.x(), position.y(), position.z(), getItem());
        item.setDeltaMovement(0, 0.3, 0);
        item.setPickUpDelay(20);
        level.addFreshEntity(item);

        setItem(ItemStack.EMPTY);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, SwordStoneBlockEntity entity) {
        entity.ticksSinceLastInteraction++;
        if (entity.progress > 0 && entity.ticksSinceLastInteraction >= IDLE_TIME) {
            entity.progress--;
        }

        if (!entity.variant.equals(FILLED_SWORD)) {
            entity.fillSword();
        }

        if (entity.sendSyncPacket && !level.isClientSide) {
            sendSyncPacket(entity);
            entity.sendSyncPacket = false;
        }
    }

    private void fillSword() {
        stack = UniqueSwordItem.getRandom(variant, level == null ? RandomSource.create() : level.random);
        variant = FILLED_SWORD;
        sendSyncPacket = true;
    }

    private static void sendSyncPacket(SwordStoneBlockEntity entity) {
        SSNetworking.sendToClients(new ClientboundSyncSwordStoneItemPacket(entity.getBlockPos(), entity.stack));
    }

    public void setItem(ItemStack stack) {
        assert level != null;

        this.stack = stack.copy();
        setChanged();
        if (!level.isClientSide) {
            sendSyncPacket(this);
        }
    }

    /** @return a copy of the held stack */
    public ItemStack getItem() {
        return stack.copy();
    }

    public void modifyStack(Consumer<ItemStack> stackConsumer) {
        ItemStack oldStack = stack.copy();
        stackConsumer.accept(stack);
        if (!oldStack.equals(stack)) {
            setChanged();
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        stack = ItemStack.of(pTag.getCompound(ITEM_TAG));
        this.variant = pTag.getString(VARIANT_TAG);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(ITEM_TAG, stack.serializeNBT());
        pTag.putString(VARIANT_TAG, variant);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put(ITEM_TAG, stack.serializeNBT());
        tag.putString(VARIANT_TAG, variant);
        return tag;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return renderBox;
    }
}
