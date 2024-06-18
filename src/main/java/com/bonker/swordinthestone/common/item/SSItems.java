package com.bonker.swordinthestone.common.item;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.ability.SwordAbility;
import com.bonker.swordinthestone.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class SSItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SwordInTheStone.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB.location(), SwordInTheStone.MODID);

    public static final RegistryObject<UniqueSwordItem> FOREST_SWORD = swordVariant("forest_sword", 0x33641c);
    public static final RegistryObject<UniqueSwordItem> DESERT_SWORD = swordVariant("desert_sword", 0xdad2a3);
    public static final RegistryObject<UniqueSwordItem> ARCTIC_SWORD = swordVariant("arctic_sword", 0x85adf8);
    public static final RegistryObject<UniqueSwordItem> PLAINS_SWORD = swordVariant("plains_sword", 0x587336);
    public static final RegistryObject<UniqueSwordItem> NETHER_SWORD = swordVariant("nether_sword", 0x723232);
    public static final RegistryObject<UniqueSwordItem> END_SWORD = swordVariant("end_sword", 0xecfbaf);


    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("unique_swords", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.swordinthestone.swords"))
            .icon(() -> new ItemStack(FOREST_SWORD.get()))
            .displayItems(((params, items) -> {
                for (RegistryObject<Item> item : SSItems.ITEMS.getEntries()) {
                    if (item.get() instanceof UniqueSwordItem sword) {
                        for (RegistryObject<SwordAbility> ability : SwordAbilities.SWORD_ABILITIES.getEntries()) {
                            ItemStack stack = new ItemStack(sword);
                            stack.getOrCreateTag().putString("ability", ability.getId().toString());
                            items.accept(stack);
                        }
                    }
                }
            }))
            .build());

    private static RegistryObject<UniqueSwordItem> swordVariant(String name, int color) {
        SwordInTheStone.SWORD_MODEL_MAP.put(Util.makeResource(name), Util.makeResource("item/sword/" + name));
        return ITEMS.register(name, () -> new UniqueSwordItem(color, new Item.Properties()));
    }
}
