package petrolpark.mc.library.core.world.restaurant.serving;

import java.util.Collections;
import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.ValidateNearbyPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.schedule.Activity;
import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.core.world.entity.ai.behavior.AcquirePoiNearBehavior;
import petrolpark.mc.library.core.world.entity.ai.behavior.SetWalkTargetFromBlockMemoriesBehavior;
import petrolpark.mc.library.core.world.entity.ai.behavior.SitBehavior;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;
import petrolpark.mc.library.registry.PetrolparkAttachmentTypes;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedDataMapTypes;
import petrolpark.mc.library.shared.registry.SharedMemoryModuleTypes;
import petrolpark.mc.library.util.FunctionHelper;

@ParametersAreNonnullByDefault
public class EatRestaurantServingBehavior extends Behavior<LivingEntity> {

    @SuppressWarnings("deprecation")
    public static <E extends PathfinderMob> void addCompoundBehavior(LivingEntity entity, Brain<E> brain) {
        if (SharedFeatureFlag.RESTAURANT_SEATING.enabled()) {
            final Integer priority = entity.getType().builtInRegistryHolder().getData(SharedDataMapTypes.RESTAURANT_EAT_BEHAVIOR_PRIORITY);
            if (priority != null) {
                brain.availableBehaviorsByPriority
                    .computeIfAbsent(priority, $ -> Maps.newHashMap())
                    .computeIfAbsent(Activity.CORE, $ -> Sets.newLinkedHashSet())
                    .add(EatRestaurantServingBehavior.createCompoundBehavior());
            };
        };
    };

    public static final Predicate<Holder<PoiType>> SEAT_POI_PREDICATE = holder -> holder.is(PetrolparkTags.PoiTypes.SEATS);

    public static <E extends PathfinderMob> GateBehavior<E> createCompoundBehavior() {

        final ImmutableList.Builder<Pair<? extends BehaviorControl<? super E>, Integer>> behaviorBuilder = ImmutableList.<Pair<? extends BehaviorControl<? super E>, Integer>>builder()
            .add(Pair.of(new EatRestaurantServingBehavior(100), 7));

        if (Mods.CREATE.isLoaded())
            behaviorBuilder.add(Pair.of(
                AcquirePoiNearBehavior.create(
                    holder -> holder.is(PetrolparkTags.PoiTypes.SEATS),
                    SharedMemoryModuleTypes.SEAT_POS.get(), SharedMemoryModuleTypes.SEAT_POS.get(),
                    SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get(),
                    2, FunctionHelper.not(SeatBlock::isSeatOccupied)
                ),
                10
            )).add(Pair.of(
                SetWalkTargetFromBlockMemoriesBehavior.create(
                    SharedMemoryModuleTypes.SEAT_POS.get(), SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get(),
                    0.5f,
                    1, 100, 1200
                ),
                8
            )).add(Pair.of(new SitBehavior(), 5))
            .add(Pair.of(ValidateNearbyPoi.create(SEAT_POI_PREDICATE, SharedMemoryModuleTypes.SEAT_POS.get()), 10));
        else
            behaviorBuilder.add(Pair.of(
                SetWalkTargetFromBlockMemoriesBehavior.create(
                    SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get(), SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get(),
                    0.5f,
                    2, 100, 1200
                ),
                8
            ));
        
        return new GateBehavior<>(
            ImmutableMap.of(SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get(), MemoryStatus.VALUE_PRESENT),
            Collections.emptySet(),
            GateBehavior.OrderPolicy.ORDERED,
            GateBehavior.RunningPolicy.TRY_ALL,
            behaviorBuilder.build()
        );
    };

    protected final int duration;

    protected IServingBlockEntity.Serving.Satisfied serving = null;
    protected long startTime;

    public EatRestaurantServingBehavior(int duration) {
        super(
            ImmutableMap.of(
                SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get(), MemoryStatus.VALUE_PRESENT
            ), duration
        );
        this.duration = duration;
    };

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity owner) {
        if (!SharedFeatureFlag.RESTAURANT_SEATING.enabled()) return false;
        final GlobalPos globalPos = owner.getBrain().getMemory(SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get()).get();
        final ICustomer customer = owner.getExistingData(PetrolparkAttachmentTypes.ENTITY_CUSTOMER).orElse(ICustomer.none());
        return !customer.isNone()
            && isInRightPlace(level, owner)
            && level.getBlockEntity(globalPos.pos()) instanceof IServingBlockEntity be
            && be.streamServings()
                .filter(serving -> serving instanceof IServingBlockEntity.Serving.Satisfied)
                .map(IServingBlockEntity.Serving::customer)
                .anyMatch(customer::equals);
    };

    @Override
    protected void start(ServerLevel level, LivingEntity entity, long gameTime) {
        startTime = gameTime;
        final BlockPos pos = entity.getBrain().getMemory(SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get()).get().pos();
        final ICustomer customer = entity.getExistingData(PetrolparkAttachmentTypes.ENTITY_CUSTOMER).orElse(ICustomer.none());

        if (entity.getBrain().checkMemory(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED))
            entity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(pos));

        if (level.getBlockEntity(pos) instanceof IServingBlockEntity be) {
            for (IServingBlockEntity.Serving serving : be.streamServings().toList()) {
                if (!(serving instanceof IServingBlockEntity.Serving.Satisfied satisfied) || !satisfied.customer().equals(customer)) continue;
                this.serving = satisfied;
                break;
            };
        };
    };

    @Override
    protected void tick(ServerLevel level, LivingEntity owner, long gameTime) {
        if (serving == null) return;
        final int elapsedTime = (int)(gameTime - startTime);
        if (serving.eat(elapsedTime, elapsedTime == duration || duration == 0 ? 1f : (float)elapsedTime / (float)duration)) {
            //TODO rewards and erase customer info
        };
    };

    @Override
    protected boolean canStillUse(ServerLevel level, LivingEntity entity, long gameTime) {
        return isInRightPlace(level, entity)
            && entity.hasData(PetrolparkAttachmentTypes.ENTITY_CUSTOMER.get())
            && serving != null
            && serving.isStillSatisfied();
    };

    public boolean isInRightPlace(ServerLevel level, LivingEntity entity) {
        if (!entity.getBrain().hasMemoryValue(SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get())) return false;
        final GlobalPos globalPos = entity.getBrain().getMemory(SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get()).get();
        return globalPos.dimension() == level.dimension()
            && globalPos.pos().closerToCenterThan(entity.position(), 2d)
            && (!Mods.CREATE.isLoaded() || !entity.getBrain().checkMemory(SharedMemoryModuleTypes.SEAT_POS.get(), MemoryStatus.REGISTERED) || SitBehavior.isSatIfNeeded(level, entity).orElse(true));
    };

    @Override
    protected void stop(ServerLevel level, LivingEntity entity, long gameTime) {
        startTime = gameTime;
        serving = null;
    };


    
};
