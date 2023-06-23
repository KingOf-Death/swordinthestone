package com.bonker.swordinthestone.datagen;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

public class SSItemModelProvider extends ItemModelProvider {
    private final LanguageProvider languageProvider;
    private final AnimatedTextureProvider animatedTextureProvider;
    private final SSBlockStateProvider blockStateProvider;

    public SSItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper, LanguageProvider languageProvider, AnimatedTextureProvider animatedTextureProvider, SSBlockStateProvider blockStateProvider) {
        super(output, modid, existingFileHelper);
        this.languageProvider = languageProvider;
        this.animatedTextureProvider = animatedTextureProvider;
        this.blockStateProvider = blockStateProvider;
    }

    @Override
    protected void registerModels() {
        getBuilder("swordinthestone:item/unique_sword").parent(new ModelFile.UncheckedModelFile("item/handheld")).transforms()
                .transform(ItemDisplayContext.GROUND)                 .rotation(0, 0, 45)  .translation(0, 2, 0)           .scale(1.3F, 1.3F, 1.3F).end()
                .transform(ItemDisplayContext.FIXED)                  .rotation(0, 180, 0)                                 .scale(1.3F, 1.3F, 1.3F).end()
                .transform(ItemDisplayContext.HEAD)                   .rotation(0, 180, 0) .translation(0, 13, 7)          .scale(1.3F, 1.3F, 1.3F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(0, -90, 55).translation(0F, 4.0F, 0.5F)    .scale(1.1F)            .end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND) .rotation(0, 90, -55).translation(0F, 4.0F, 0.5F)    .scale(1.1F)            .end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, -90, 25).translation(1.13F, 3.2F, 1.13F).scale(0.88F)           .end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND) .rotation(0, 90, -25).translation(1.13F, 3.2F, 1.13F).scale(0.88F)           .end();

        uniqueSwordVariant(SSItems.FOREST_SWORD.get(), "forest_sword", "Sword of the Forest");
        uniqueSwordVariant(SSItems.DESERT_SWORD.get(), "desert_sword", "Sword of the Desert");

        abilityOverlayAnim("thunder_smite", 2, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 8});
        abilityOverlayAnim("vampiric", 2, null);
        abilityOverlayAnim("toxic_dash", 3, null);

        swordStoneVariant(Blocks.COBBLESTONE);
        swordStoneVariant(Blocks.SANDSTONE, "sandstone_bottom");
    }

    public void item(Item item, String name) {
        languageProvider.add(item, name);
        basicItem(item);
    }

    public void handheld(Item item, String name) {
        languageProvider.add(item, name);
        childOf(item, "item/handheld");
    }

    public void childOf(Item item, String parent) {
        ResourceLocation loc = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
        getBuilder(loc.toString())
                .parent(new ModelFile.UncheckedModelFile(parent))
                .texture("layer0", new ResourceLocation(loc.getNamespace(), "item/" + loc.getPath()));
    }

    public void abilityOverlay(String id) {
        ResourceLocation loc = new ResourceLocation(SwordInTheStone.MODID, "item/ability/" + id);
        getBuilder(loc.toString())
                .parent(new ModelFile.UncheckedModelFile("swordinthestone:item/unique_sword"))
                .texture("layer0", new ResourceLocation(loc.getNamespace(), loc.getPath()));
    }

    public void abilityOverlayAnim(String id, int frametime, @Nullable int[] frames) {
        animatedTextureProvider.create("item/ability/" + id).frametime(frametime).frames(frames);
        ResourceLocation loc = new ResourceLocation(SwordInTheStone.MODID, "item/ability/" + id);
        getBuilder(loc.toString())
                .parent(new ModelFile.UncheckedModelFile("swordinthestone:item/unique_sword"))
                .texture("layer0", new ResourceLocation(loc.getNamespace(), loc.getPath()));
    }

    public void uniqueSwordVariant(UniqueSwordItem item, String id, String name) {
        languageProvider.add(item, name);
        ResourceLocation loc = new ResourceLocation(SwordInTheStone.MODID, "item/sword/" + id);
        getBuilder(loc.toString())
                .parent(new ModelFile.UncheckedModelFile("swordinthestone:item/unique_sword"))
                .texture("layer0", new ResourceLocation(loc.getNamespace(), "item/" + id));
        childOf(item, "builtin/entity");
    }

    public void swordStoneVariant(Block block) {
        ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(block);
        if (loc == null) return;
        ItemModelBuilder builder = getBuilder("swordinthestone:block/sword_stone_" + loc.getPath())
                .parent(new ModelFile.ExistingModelFile(new ResourceLocation("swordinthestone", "block/sword_stone"), existingFileHelper))
                .texture("0", new ResourceLocation(loc.getNamespace(), "block/" + loc.getPath()));
        blockStateProvider.swordStoneVariants.put(loc.getPath(), new ModelFile.ExistingModelFile(builder.getLocation(), existingFileHelper));
    }

    public void swordStoneVariant(Block block, String texture) {
        ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(block);
        if (loc == null) return;
        ItemModelBuilder builder = getBuilder("swordinthestone:block/sword_stone_" + loc.getPath())
                .parent(new ModelFile.ExistingModelFile(new ResourceLocation("swordinthestone", "block/sword_stone"), existingFileHelper))
                .texture("0", new ResourceLocation("block/" + texture));
        blockStateProvider.swordStoneVariants.put(loc.getPath(), new ModelFile.ExistingModelFile(builder.getLocation(), existingFileHelper));
    }
}
