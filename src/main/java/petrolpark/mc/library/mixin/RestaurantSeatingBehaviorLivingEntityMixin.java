package petrolpark.mc.library.mixin;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.core.world.restaurant.serving.EatRestaurantServingBehavior;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedDataMapTypes;
import petrolpark.mc.library.shared.registry.SharedMemoryModuleTypes;

@Mixin({
    Allay.class,
    // Armadillo.class,
    Axolotl.class,
    // Camel.class,
    Frog.class,
    Tadpole.class,
    Goat.class,
    Sniffer.class,
    Breeze.class,
    Hoglin.class,
    Piglin.class,
    PiglinBrute.class,
    Zoglin.class,
    Villager.class
})
public abstract class RestaurantSeatingBehaviorLivingEntityMixin extends PathfinderMob {

    protected RestaurantSeatingBehaviorLivingEntityMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        throw new AssertionError();
    };

    @ModifyReturnValue(
        method = "makeBrain",
        at = @At("RETURN")
    )
    public <E extends PathfinderMob> Brain<E> petrolpark$addRestaurantSeatingBehaviors(Brain<E> original) {
        EatRestaurantServingBehavior.addCompoundBehavior(this, original);
        return original;
    };

    @WrapOperation(
        method = "brainProvider",
        at = @At(
            value = "INVOKE",
            target = "provider"
        )
    )
    @SuppressWarnings("deprecation")
    public <E extends LivingEntity> Brain.Provider<E> petrolpark$addRestaurantSeatingMemories(Collection<? extends MemoryModuleType<?>> memoryTypes, Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes, Operation<Brain.Provider<E>> original) {
        if (SharedFeatureFlag.RESTAURANT_SEATING.enabled())
            if (getType().builtInRegistryHolder().getData(SharedDataMapTypes.RESTAURANT_EAT_BEHAVIOR_PRIORITY) != null) {
                final ImmutableList.Builder<MemoryModuleType<?>> memoryTypesBuilder = ImmutableList.<MemoryModuleType<?>>builder()
                    .addAll(memoryTypes)
                    .add(SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get());
                if (Mods.CREATE.isLoaded()) memoryTypesBuilder.add(SharedMemoryModuleTypes.SEAT_POS.get());
                return original.call(memoryTypesBuilder.build(), sensorTypes);
            };
        return original.call(memoryTypes, sensorTypes);
    };
};
