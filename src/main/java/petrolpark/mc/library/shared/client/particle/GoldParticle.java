package petrolpark.mc.library.shared.client.particle;

import javax.annotation.ParametersAreNonnullByDefault;

import petrolpark.mc.library.shared.registry.SharedParticleTypes;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class GoldParticle extends GlowParticle {

    protected GoldParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
    };

    @ParametersAreNonnullByDefault
    public static class Provider implements ParticleProvider<SimpleParticleType> {
    
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        };

        @Override
        public Particle createParticle(
            SimpleParticleType type,
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed
        ) {
            GlowParticle glowparticle = new GoldParticle(level, x, y, z, 0d, 0d, 0d, sprite);
            glowparticle.setColor(1f, 0.843f, 0.125f);
            glowparticle.setParticleSpeed(xSpeed * 0.01d / 2d, ySpeed * 0.01d, zSpeed * 0.01d / 2d);
            glowparticle.setLifetime(level.getRandom().nextInt(30) + 10);
            return glowparticle;
        };
    };

    @SubscribeEvent
    public static final void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SharedParticleTypes.GOLD.get(), GoldParticle.Provider::new);
    };
    
};
