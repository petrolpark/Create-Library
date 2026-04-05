package com.petrolpark.compat.create.common.kinetics.horseMill;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.compat.create.PetrolparkCreateDataMapTypes;
import com.petrolpark.core.data.loot.numberprovider.entity.EntityNumberProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record HorseMillProperties(Vec3 positionOffset, EntityNumberProvider maxSpeed, EntityNumberProvider stressCapacity, Optional<ResourceLocation> harnessModelLocation) {
    
    @SuppressWarnings("deprecation")
    public static final Optional<HorseMillProperties> get(Entity entity) {
        return Optional.ofNullable(entity.getType().builtInRegistryHolder().getData(PetrolparkCreateDataMapTypes.HORSE_MILL_PROPERTIES));
    };

    public static final Codec<HorseMillProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3.CODEC.fieldOf("position_offset").forGetter(HorseMillProperties::positionOffset),
        EntityNumberProvider.CODEC.fieldOf("max_speed").forGetter(HorseMillProperties::maxSpeed),
        EntityNumberProvider.CODEC.fieldOf("stress_capacity").forGetter(HorseMillProperties::stressCapacity),
        ResourceLocation.CODEC.optionalFieldOf("harness_model").forGetter(HorseMillProperties::harnessModelLocation)
    ).apply(instance, HorseMillProperties::new));
};
