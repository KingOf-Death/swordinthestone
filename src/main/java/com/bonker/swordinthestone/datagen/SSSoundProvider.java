package com.bonker.swordinthestone.datagen;

import com.bonker.swordinthestone.util.Util;
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
        createMultiSound("rock", "Rock crumbles", "rock1", "rock2", "rock3", "rock4", "rock5", "rock6", "rock7", "rock8");
        createNoSubtitleSingleSound("sword_pull");
        createSingleSound("success", "Celebratory fanfare", "success");
        createSingleSound("laser", "Laser fires", "laser");
        createSingleSound("fireball", "Fireball burns", "fireball");
        createSingleSound("rift", "Ender rift created", "rift");
        createSingleSound("jump", "Player double jumps", "jump");
        createSingleSound("land", "Player lands softly", "land");
        createSingleSound("suction", "Vortex suction", "suction");
        createSingleSound("vortex", "Powerful vortex blast", "vortex");
        createSingleSound("whoosh", "Vortex energy released", "whoosh");
    }

    private void createMultiSound(String name, String subtitle, String... sounds) {
        SoundDefinition definition = SoundDefinition.definition();
        for (String sound : sounds) {
            definition.with(SoundDefinition.Sound.sound(Util.makeResource(sound), SoundDefinition.SoundType.SOUND));
        }
        String langKey = "subtitles.swordinthestone." + name;
        add(name, definition.subtitle(langKey));
        languageProvider.add(langKey, subtitle);
    }

    private void createSingleSound(String name, String subtitle, String sound) {
        SoundDefinition definition = SoundDefinition.definition();
        definition.with(SoundDefinition.Sound.sound(Util.makeResource(sound), SoundDefinition.SoundType.SOUND));
        String langKey = "subtitles.swordinthestone." + name;
        add(name, definition.subtitle(langKey));
        languageProvider.add(langKey, subtitle);
    }

    private void createNoSubtitleSingleSound(String name) {
        ResourceLocation loc = Util.makeResource(name);
        add(loc, SoundDefinition.definition().with(sound(loc)));
    }
}
