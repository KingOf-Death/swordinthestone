package com.bonker.swordinthestone.datagen;

import com.bonker.swordinthestone.common.block.SSBlocks;
import com.bonker.swordinthestone.common.block.SwordStoneBlock;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashMap;
import java.util.Map;

public class SSBlockStateProvider extends BlockStateProvider {
    Map<String, ModelFile> swordStoneVariants = new HashMap<>();

    public SSBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(SSBlocks.SWORD_STONE.get())
                .forAllStatesExcept(state ->
                            ConfiguredModel.builder()
                                    .modelFile(swordStoneVariants.get(state.getValue(SwordStoneBlock.VARIANT).getSerializedName()))
                                    .rotationY((int) state.getValue(SwordStoneBlock.FACING).getOpposite().toYRot())
                                    .build()
                    , SwordStoneBlock.HAS_SWORD);
    }
}
