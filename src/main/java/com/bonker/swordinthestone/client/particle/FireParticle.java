package com.bonker.swordinthestone.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class FireParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float rollSpeed;

    public FireParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSpriteSet) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        this.lifetime = 30 + pLevel.random.nextInt(15);

        this.sprites = pSpriteSet;
        setSpriteFromAge(sprites);

        this.xd = (pLevel.random.nextFloat() - 0.5F) * 0.05F;
        this.yd = (pLevel.random.nextFloat() - 0.5F) * 0.05F;
        this.zd = (pLevel.random.nextFloat() - 0.5F) * 0.05F;

        this.rollSpeed = (pLevel.random.nextFloat() - 0.5F) * 0.1F;
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(sprites);

        oRoll = roll;
        roll += rollSpeed;

        scale(0.96F);
    }



    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType options, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new FireParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
        }
    }
}
