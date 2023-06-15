package com.bonker.swordinthestone.datagen;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

public class SSItemModelProvider extends ItemModelProvider {
    private final LanguageProvider languageProvider;
    private final AnimatedTextureProvider animatedTextureProvider;

    public SSItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper, LanguageProvider languageProvider, AnimatedTextureProvider animatedTextureProvider) {
        super(output, modid, existingFileHelper);
        this.languageProvider = languageProvider;
        this.animatedTextureProvider = animatedTextureProvider;
    }

    @Override
    protected void registerModels() { //TODO: put in all of heldheld.json's default values
        ModelBuilder<ItemModelBuilder>.TransformsBuilder transforms = getBuilder("swordinthestone:item/unique_sword")
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .transforms();
        transforms.transform(ItemDisplayContext.GROUND).scale(1.3F, 1.3F, 1.3F).rotation(0, 0, 45).translation(0, 2, 0);
        transforms.transform(ItemDisplayContext.FIXED).scale(1.3F, 1.3F, 1.3F);
        transforms.transform(ItemDisplayContext.HEAD).scale(1.3F, 1.3F, 1.3F);
        transforms.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).scale(1.3F, 1.3F, 1.3F);
        transforms.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(1.3F, 1.3F, 1.3F);

        uniqueSwordVariant(SSItems.FOREST_SWORD.get(), "forest_sword", "Sword of the Forest");
        uniqueSwordVariant(SSItems.DESERT_SWORD.get(), "desert_sword", "Sword of the Desert");

        abilityOverlayAnim("thunder_smite", 2, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 8});
        abilityOverlayAnim("vampiric", 2, null);
        abilityOverlayAnim("toxic_dash", 3, null);
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
}
