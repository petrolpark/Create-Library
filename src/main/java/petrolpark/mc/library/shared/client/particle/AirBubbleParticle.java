package petrolpark.mc.library.shared.client.particle;

import javax.annotation.Nonnull;

import petrolpark.mc.library.shared.registry.SharedParticleTypes;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class AirBubbleParticle extends TextureSheetParticle {

    public static final int POP_TIME = 4;
    protected final SpriteSet spriteSet;

    protected AirBubbleParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.spriteSet = spriteSet;
        setSize(0.02F, 0.02F);
        quadSize = this.quadSize * (this.random.nextFloat() * 0.6F + 0.2F);
        xd = xSpeed * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
        yd = ySpeed * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
        zd = zSpeed * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
        lifetime = (int)(40.0 / (Math.random() * 0.8 + 0.2));
        setSprite(spriteSet.get(0, lifetime));
    };

    @Override
    public void tick() {
        super.tick();
        if (!removed && age + POP_TIME >= lifetime) setSprite(spriteSet.get(age + POP_TIME - lifetime, 4));
    };

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    };

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet sprites) {
            this.spriteSet = sprites;
        };

        public Particle createParticle(@Nonnull SimpleParticleType type, @Nonnull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            AirBubbleParticle particle = new AirBubbleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
            return particle;
        };
    };

    @SubscribeEvent
    public static final void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SharedParticleTypes.AIR_BUBBLE.get(), AirBubbleParticle.Provider::new);
    };
    
};
