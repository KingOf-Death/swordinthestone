package com.bonker.swordinthestone.datagen;

import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import com.bonker.swordinthestone.util.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("unused")
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
                .transform(ItemDisplayContext.GROUND)                 .rotation(0, 0, -135).translation(0, 3, 0)           .scale(1F, 1F, 1F)      .end()
                .transform(ItemDisplayContext.FIXED)                  .rotation(0, 180, 180)                               .scale(1.3F, 1.3F, 1.3F).end()
                .transform(ItemDisplayContext.HEAD)                   .rotation(0, 180, 0) .translation(0, 13, 7)          .scale(1.3F, 1.3F, 1.3F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(0, -90, 55).translation(0F, 4.0F, 0.5F)    .scale(1.1F)            .end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND) .rotation(0, 90, -55).translation(0F, 4.0F, 0.5F)    .scale(1.1F)            .end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, -90, 25).translation(1.13F, 3.2F, 1.13F).scale(0.88F)           .end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND) .rotation(0, 90, -25).translation(1.13F, 3.2F, 1.13F).scale(0.88F)           .end();

        uniqueSwordVariant(SSItems.FOREST_SWORD.get(), "forest_sword", "Sword of the Forest");
        uniqueSwordVariant(SSItems.DESERT_SWORD.get(), "desert_sword", "Sword of the Desert");
        uniqueSwordVariant(SSItems.ARCTIC_SWORD.get(), "arctic_sword", "Sword of the Arctic");
        uniqueSwordVariant(SSItems.PLAINS_SWORD.get(), "plains_sword", "Sword of the Plains");
        uniqueSwordVariant(SSItems.NETHER_SWORD.get(), "nether_sword", "Sword of the Nether");
        uniqueSwordVariant(SSItems.END_SWORD.get(), "end_sword", "Sword of the End");

        abilityOverlay("thunder_smite", 2, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 8});
        abilityOverlay("vampiric", 2, null);
        abilityOverlay("toxic_dash", 3, null);
        abilityOverlay("ender_rift", 2, null);
        abilityOverlay("fireball", 2, null);
        abilityOverlay("double_jump", 2, null);
        abilityOverlay("alchemist", 3, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 13});
        abilityOverlay("bat_swarm", 2, null);
        abilityOverlay("vortex_charge", 2, null);

        swordStoneVariant(Blocks.COBBLESTONE);
        swordStoneVariant("sandstone", "sandstone_bottom");
        swordStoneVariant("red_sandstone", "red_sandstone_bottom");
        swordStoneVariant(Blocks.PACKED_ICE);
        swordStoneVariant(Blocks.NETHERRACK);
        swordStoneVariant(Blocks.END_STONE);
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

    public void abilityOverlay(String id, int frametime, @Nullable int[] frames) {
        animatedTextureProvider.create("item/ability/" + id).frametime(frametime).frames(frames);
        ResourceLocation loc = Util.makeResource("item/ability/" + id);
        getBuilder(loc.toString())
                .parent(new ModelFile.UncheckedModelFile("swordinthestone:item/unique_sword"))
                .texture("layer0", new ResourceLocation(loc.getNamespace(), loc.getPath()));
    }

    public void uniqueSwordVariant(UniqueSwordItem item, String id, String name) {
        languageProvider.add(item, name);
        ResourceLocation loc = Util.makeResource("item/sword/" + id);
        getBuilder(loc.toString())
                .parent(new ModelFile.UncheckedModelFile("swordinthestone:item/unique_sword"))
                .texture("layer0", new ResourceLocation(loc.getNamespace(), "item/" + id));
        childOf(item, "builtin/entity");
    }

    public void swordStoneVariant(Block block) {
        ResourceLocation key = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
        swordStoneVariant(key.getPath(), key.getPath());
    }

    public void swordStoneVariant(String name, String blockTexture) {
        ResourceLocation loc = Util.makeResource(name);
        ItemModelBuilder builder = getBuilder("swordinthestone:block/sword_stone_" + loc.getPath())
                .parent(new ModelFile.ExistingModelFile(new ResourceLocation("swordinthestone", "block/sword_stone"), existingFileHelper));
        ResourceLocation texture = new ResourceLocation("block/" + blockTexture);
        builder.texture("0", texture).texture("particle", texture);
        blockStateProvider.swordStoneVariants.put(loc.getPath(), new ModelFile.ExistingModelFile(builder.getLocation(), existingFileHelper));
    }
}
