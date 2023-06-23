package com.bonker.swordinthestone.common.block.entity;

import com.bonker.swordinthestone.util.AbilityUtil;
import com.bonker.swordinthestone.common.block.SSBlocks;
import com.bonker.swordinthestone.common.block.SwordStoneBlock;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.bonker.swordinthestone.common.networking.ClientboundSyncSwordStoneItemPacket;
import com.bonker.swordinthestone.common.networking.SSNetworking;
import com.bonker.swordinthestone.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

public class SwordStoneBlockEntity extends BlockEntity {
    public static final String ITEM_TAG = "Item";
    public static final String AGE_TAG = "Age";
    public static final String VARIANT_TAG = "variant";
    public static final String FILLED_SWORD = "filled";

    public static final int BEACON_ANIMATION_TIME = 140;
    public static final int BEACON_ANIMATION_CYCLE = 200;
    public static final int SHAKE_ANIMATION_TIME = 10;
    public static final int REQUIRED_SHAKES = 20;
    public static final int IDLE_TIME = 100;

    private ItemStack stack = ItemStack.EMPTY;
    public int progress = 0;
    public int ticksSinceLastInteraction = 0;
    public int idleTicks;
    private boolean sendSyncPacket = false;
    private String variant = "";

    private final AABB renderBox;
    private final BlockPos[] blocks;

    public SwordStoneBlockEntity(BlockPos pPos, BlockState pState) {
        super(SSBlockEntities.SWORD_STONE.get(), pPos, pState);

        BlockPos pos = getBlockPos();
        renderBox = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 2, pos.getY() + 256, pos.getZ() + 2);

        blocks = new BlockPos[] {pPos, pPos.south(), pPos.east(), pPos.south().east()};
    }

    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        assert level != null;

        if ((idleTicks > 0 && idleTicks < BEACON_ANIMATION_TIME) // can't be spinning
            || !getBlockState().getValue(SwordStoneBlock.HAS_SWORD) // has a sword to shake
            || ticksSinceLastInteraction <= SHAKE_ANIMATION_TIME) // can't be shaking
            return InteractionResult.PASS;

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

        Vec3 position = new Vec3(getBlockPos().getX() + 1, getBlockPos().getY() + 1.3, getBlockPos().getZ() + 1);
        ItemEntity item = new ItemEntity(level, position.x(), position.y(), position.z(), getItem());
        item.setDeltaMovement(0, 0.3, 0);
        item.setPickUpDelay(20);
        level.addFreshEntity(item);

        setItem(ItemStack.EMPTY);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, SwordStoneBlockEntity entity) {
        entity.ticksSinceLastInteraction++;
        if (entity.progress > 0 && entity.ticksSinceLastInteraction >= IDLE_TIME) {
            entity.progress = 0;
        }

        if (entity.isIdle()) {
            entity.idleTicks++;
            if (entity.idleTicks >= BEACON_ANIMATION_CYCLE) {
                entity.idleTicks = 0;
            }
        } else {
            entity.idleTicks = 0;
        }

        if (!level.isClientSide && !entity.variant.equals(FILLED_SWORD)) {
            entity.fillSword();
        }

        if (entity.sendSyncPacket && !level.isClientSide) {
            sendSyncPacket(entity);
            entity.sendSyncPacket = false;
        }
    }

    private void fillSword() {
        System.out.println("filling: " + variant);
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

    public float[] getBeamColor() {
        if (stack.getItem() instanceof UniqueSwordItem uniqueSwordItem) {
            Color color = UniqueSwordItem.STYLE_TABLE.get(uniqueSwordItem, AbilityUtil.getSwordAbility(stack));
            if (color != null) return color.getDiffusedColor();
        }
        return AbilityUtil.getSwordAbility(stack).getDiffusedColor();
    }

    public boolean isIdle() {
        return progress == 0;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        stack = ItemStack.of(pTag.getCompound(ITEM_TAG));
        variant = pTag.getString(VARIANT_TAG);
        System.out.println("loaded: " + variant); //TODO someimes says "Tried to load a block entity before it was loaded
        // TODO sometimes variant is an empty string instead of the correct value
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(ITEM_TAG, stack.serializeNBT());
        pTag.putString(VARIANT_TAG, variant);
    }

    @Override
    public CompoundTag getUpdateTag() {
       return saveWithoutMetadata();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return renderBox;
    }
}
