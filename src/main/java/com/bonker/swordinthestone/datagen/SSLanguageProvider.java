package com.bonker.swordinthestone.datagen;

import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;

public class SSLanguageProvider extends LanguageProvider {
    public SSLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("item_group.swordinthestone.swords", "Unique Swords");

        add(SwordAbilities.THUNDER_SMITE.get(), "Thunder Smite", "Thundering %1$s", "Sword charges when attacking mobs\nStrikes mobs with lightning when fully charged");
        add(SwordAbilities.VAMPIRIC.get(), "Life Steal", "Vampiric %1$s", "Gain 0-2 hearts upon killing a mob");
        add(SwordAbilities.TOXIC_DASH.get(), "Toxic Dash", "Toxic %1$s", "Slash forward on right click leaving behind a poisonous cloud");
    }

    private void add(SwordAbility key, String name, String title, String description) {
        ResourceLocation loc = SwordAbilities.SWORD_ABILITY_REGISTRY.get().getKey(key);
        if (loc != null) {
            add("ability." + loc.getNamespace() + "." + loc.getPath(), name);
            add("ability." + loc.getNamespace() + "." + loc.getPath() + ".title", title);
            add("ability." + loc.getNamespace() + "." + loc.getPath() + ".description", description);
        }
    }
}