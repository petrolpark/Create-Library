package petrolpark.mc.library.shared.world.effect;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.core.world.effect.SimpleMobEffect;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedAttachmentTypes;
import petrolpark.mc.library.shared.registry.SharedDamageSources;
import petrolpark.mc.library.shared.registry.SharedEffectCures;
import petrolpark.mc.library.shared.registry.SharedMobEffects;

@EventBusSubscriber
@ParametersAreNonnullByDefault
public class HangoverMobEffect extends SimpleMobEffect implements ISharedFeature {

    public HangoverMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    };

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        final DynamicGameEventListener<HangoverMobEffect.VibrationListener> listener = livingEntity.getData(SharedAttachmentTypes.HANGOVER_VIBRATION_LISTENER);
        if (livingEntity.level() instanceof ServerLevel level) listener.move(level);    
        VibrationSystem.Ticker.tick(livingEntity.level(), listener.getListener().vibrationSystem.getVibrationData(), listener.getListener().vibrationSystem);
        
        return super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    };

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        cures.add(SharedEffectCures.HANGOVER);
    };

    @Override
    public void onMobRemoved(LivingEntity livingEntity, int amplifier, Entity.RemovalReason reason) {
        livingEntity.getExistingData(SharedAttachmentTypes.HANGOVER_VIBRATION_LISTENER).ifPresent(HangoverMobEffect::removeVibrationListener);
    };

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect() == SharedMobEffects.HANGOVER) {
            event.getEntity().removeData(SharedAttachmentTypes.HANGOVER_VIBRATION_DATA);
            Optional.ofNullable(event.getEntity().removeData(SharedAttachmentTypes.HANGOVER_VIBRATION_LISTENER)).ifPresent(HangoverMobEffect::removeVibrationListener);
        };
    };

    public static final void removeVibrationListener(DynamicGameEventListener<HangoverMobEffect.VibrationListener> listener) {
        if (listener.getListener().vibrationSystem.entity.level() instanceof ServerLevel level)
            listener.remove(level);
    };

    public static class VibrationUser implements VibrationSystem, VibrationSystem.User {

        protected final LivingEntity entity;
        protected final PositionSource positionSource;

        public VibrationUser(LivingEntity entity, int effectAmplifier) {
            this.entity = entity;
            this.positionSource = new EntityPositionSource(entity, entity.getEyeHeight());
        };

        @Override
        public HangoverMobEffect.VibrationUser getVibrationUser() {
            return this;
        };

        @Override
        public VibrationSystem.Data getVibrationData() {
            return entity.getData(SharedAttachmentTypes.HANGOVER_VIBRATION_DATA);
        };

        @Override
        public int getListenerRadius() {
            return 10;
        };

        @Override
        public PositionSource getPositionSource() {
            return positionSource;
        };

        @Override
        public boolean canReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> gameEvent, GameEvent.Context context) {
            return !(gameEvent.is(PetrolparkTags.GameEvents.DOESNT_TRIGGER_HANGOVER))
                && entity.hasEffect(SharedMobEffects.HANGOVER);
        };

        @Override
        public void onReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> gameEvent, @Nullable Entity entity, @Nullable Entity playerEntity, float distance) {
            final MobEffectInstance instance = this.entity.getEffect(SharedMobEffects.HANGOVER);
            if (instance == null) return;
            this.entity.hurt(SharedDamageSources.headache(level), 1f * (1 + instance.getAmplifier()));
        };

    };

    public static class VibrationListener extends VibrationSystem.Listener {

        public static final DynamicGameEventListener<HangoverMobEffect.VibrationListener> create(IAttachmentHolder holder) {
            if (!(holder instanceof LivingEntity entity)) throw new IllegalArgumentException("Not a LivingEntity!");
                final MobEffectInstance instance = entity.getEffect(SharedMobEffects.HANGOVER);
                if (instance == null) throw new IllegalArgumentException("LivingEntity does not have Hangover effect");
                return new DynamicGameEventListener<>(new HangoverMobEffect.VibrationListener(new VibrationUser(entity, instance.getAmplifier())));
        };

        protected final HangoverMobEffect.VibrationUser vibrationSystem;

        public VibrationListener(HangoverMobEffect.VibrationUser vibrationSystem) {
            super(vibrationSystem);
            this.vibrationSystem = vibrationSystem;
        };

        @Override
        public int hashCode() {
            return vibrationSystem.entity.hashCode();
        };

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof VibrationListener listener && vibrationSystem.entity == listener.vibrationSystem.entity;
        };

        public static class AttachmentSerializer implements IAttachmentSerializer<Tag, DynamicGameEventListener<HangoverMobEffect.VibrationListener>> {

            @Override
            public DynamicGameEventListener<HangoverMobEffect.VibrationListener> read(IAttachmentHolder holder, Tag tag, HolderLookup.Provider provider) {
                return HangoverMobEffect.VibrationListener.create(holder);
            };

            @Override
            public @Nullable Tag write(DynamicGameEventListener<HangoverMobEffect.VibrationListener> attachment, HolderLookup.Provider provider) {
                return null;
            };

        };

    };
    
    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.INEBRIATION;
    };
};
