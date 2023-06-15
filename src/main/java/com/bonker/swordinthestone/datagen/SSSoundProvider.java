package com.bonker.swordinthestone.datagen;

import com.bonker.swordinthestone.SwordInTheStone;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class SSSoundProvider extends SoundDefinitionsProvider {
    private final LanguageProvider languageProvider;

    protected SSSoundProvider(PackOutput output, String modId, ExistingFileHelper helper, SSLanguageProvider languageProvider) {
        super(output, modId, helper);
        this.languageProvider = languageProvider;
    }

    @Override
    public void registerSounds() {
        createMultiSound("zap", "Electric zap", "zap1", "zap2", "zap3");
        createMultiSound("heal", "Vampiric healing", "heal1", "heal2", "heal3", "heal4", "heal5");
        createSingleSound("toxic", "Toxins released", "toxic");
        createMultiSound("dash", "Dash attack", "dash1", "dash2", "dash3", "dash4");
    }

    private void createMultiSound(String name, String subtitle, String... sounds) {
        SoundDefinition definition = SoundDefinition.definition();
        for (String sound : sounds) {
            definition.with(SoundDefinition.Sound.sound(new ResourceLocation(SwordInTheStone.MODID, sound), SoundDefinition.SoundType.SOUND));
        }
        String langKey = "subtitles.swordinthestone." + name;
        add(name, definition.subtitle(langKey));
        languageProvider.add(langKey, subtitle);
    }

    private void createSingleSound(String name, String subtitle, String sound) {
        SoundDefinition definition = SoundDefinition.definition();
        definition.with(SoundDefinition.Sound.sound(new ResourceLocation(SwordInTheStone.MODID, sound), SoundDefinition.SoundType.SOUND));
        String langKey = "subtitles.swordinthestone." + name;
        add(name, definition.subtitle(langKey));
        languageProvider.add(langKey, subtitle);
    }
}
