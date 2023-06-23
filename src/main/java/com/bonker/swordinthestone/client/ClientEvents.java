package com.bonker.swordinthestone.client;

import com.bonker.swordinthestone.SwordInTheStone;
import com.bonker.swordinthestone.client.particle.HealParticle;
import com.bonker.swordinthestone.client.particle.SSParticles;
import com.bonker.swordinthestone.client.renderer.SSBEWLR;
import com.bonker.swordinthestone.client.renderer.SwordStoneBlockEntityRenderer;
import com.bonker.swordinthestone.common.ability.SwordAbilities;
import com.bonker.swordinthestone.common.block.entity.SSBlockEntities;
import com.bonker.swordinthestone.common.entity.SSEntityTypes;
import com.bonker.swordinthestone.common.item.SSItems;
import com.bonker.swordinthestone.common.item.UniqueSwordItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = SwordInTheStone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            Minecraft minecraft = Minecraft.getInstance();
            SSBEWLR.INSTANCE = new SSBEWLR(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        }

        @SubscribeEvent
        public static void onRegisterEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SSEntityTypes.HEIGHT_AREA_EFFECT_CLOUD.get(), NoopRenderer::new);

            event.registerBlockEntityRenderer(SSBlockEntities.SWORD_STONE.get(), SwordStoneBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterAdditionalModels(final ModelEvent.RegisterAdditional event) {
            for (ResourceLocation ability : SwordAbilities.SWORD_ABILITY_REGISTRY.get().getKeys()) {
                event.register(new ResourceLocation(ability.getNamespace(), "item/ability/" + ability.getPath()));
            }
            for (RegistryObject<Item> regObj : SSItems.ITEMS.getEntries()) {
                if (!(regObj.get() instanceof UniqueSwordItem)) continue;
                ResourceLocation loc = ForgeRegistries.ITEMS.getKey(regObj.get());
                if (loc == null) continue;
                event.register(new ResourceLocation(loc.getNamespace(), "item/sword/" + loc.getPath()));
            }
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(final RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(SSParticles.HEAL.get(), HealParticle.Provider::new);
        }
    }
}
