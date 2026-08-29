package petrolpark.mc.library.shared.world.effect;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import petrolpark.mc.library.core.world.effect.SyncedMobEffect;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedMobEffects;
import petrolpark.mc.library.shared.registry.SharedParticleTypes;

public class CryingMobEffect extends SyncedMobEffect implements ISharedFeature {

    public CryingMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    };

    public static MobEffectInstance getDefaultInstance() {
        return SharedMobEffects.CRYING.asInstance()
            .duration(900)
            .visible(false)
            .showIcon(true)
            .build();
    };

    @Override
    public boolean applyEffectTick(@Nonnull LivingEntity livingEntity, int amplifier) {
        final RandomSource random = livingEntity.getRandom();

        final boolean ghastTear = livingEntity.getType() == EntityType.GHAST && random.nextFloat() < 0.1f;
        final boolean particle = livingEntity.level().isClientSide();

        if (!ghastTear && !particle) return true;

        final Vec3 eyePos = livingEntity.getEyePosition();
        final Vec3 motion = livingEntity.getDeltaMovement().add(
            Mth.cos(Mth.PI * (livingEntity.getYHeadRot() + 90 - 15 + random.nextFloat() * 30) / 180) * 0.15d,
            0d,
            Mth.sin(Mth.PI * (livingEntity.getYHeadRot() + 90 - 15 + random.nextFloat() * 30) / 180) * 0.15d
        );

        if (ghastTear) {
            final ItemEntity itemEntity = new ItemEntity(livingEntity.level(), eyePos.x(), eyePos.y(), eyePos.z(), new ItemStack(Items.GHAST_TEAR), motion.x() * 2, motion.y() * 2, motion.z() * 2);
            livingEntity.level().addFreshEntity(itemEntity);
        };

        if (particle) {
            final Minecraft minecraft = Minecraft.getInstance();
            final Player player = minecraft.player;
            final boolean isFirstPerson = player != null && minecraft.options.getCameraType().isFirstPerson() && player.is(livingEntity);
            livingEntity.level().addParticle(SharedParticleTypes.TEAR.get(), eyePos.x(), isFirstPerson ? eyePos.y() - 0.15d : eyePos.y(), eyePos.z(), motion.x(), motion.y(), motion.z());
        };

        return super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % (Math.max(1, 10 / (amplifier + 1))) == 0;
    };

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.CRYING;
    };
    
};
