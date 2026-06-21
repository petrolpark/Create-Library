package petrolpark.mc.library.compat.create.shared.content.kinetics.horseMill;

import java.util.Optional;
import java.util.function.DoubleSupplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateDataMapTypes;
import petrolpark.mc.library.core.data.loot.numberprovider.entity.EntityNumberProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record HorseMillProperties(Vec3 positionOffset, EntityNumberProvider maxSpeed, EntityNumberProvider stressCapacity, Optional<ResourceLocation> harnessModelLocation) {
    
    public static final float generateHorseMillStressCapacityAttribute(DoubleSupplier supplier) {
        return 384f + (float)supplier.getAsDouble() * 128f + (float)supplier.getAsDouble() * 128f;
    };

    @SuppressWarnings("deprecation")
    public static final Optional<HorseMillProperties> get(Entity entity) {
        return Optional.ofNullable(entity.getType().builtInRegistryHolder().getData(SharedCreateDataMapTypes.HORSE_MILL_PROPERTIES));
    };

    public static final Codec<HorseMillProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3.CODEC.fieldOf("position_offset").forGetter(HorseMillProperties::positionOffset),
        EntityNumberProvider.CODEC.fieldOf("max_speed").forGetter(HorseMillProperties::maxSpeed),
        EntityNumberProvider.CODEC.fieldOf("stress_capacity").forGetter(HorseMillProperties::stressCapacity),
        ResourceLocation.CODEC.optionalFieldOf("harness_model").forGetter(HorseMillProperties::harnessModelLocation)
    ).apply(instance, HorseMillProperties::new));
};
