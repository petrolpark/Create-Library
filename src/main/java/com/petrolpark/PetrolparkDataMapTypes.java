package com.petrolpark;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber
public class PetrolparkDataMapTypes {
    
    public static final AdvancedDataMapType<BlockEntityType<?>, List<ResourceLocation>, DataMapValueRemover.Default<List<ResourceLocation>, BlockEntityType<?>>> BLOCK_ENTITY_ADVANCEMENTS = AdvancedDataMapType
        .builder(
            Petrolpark.asResource("block_entity_advancements"),
            Registries.BLOCK_ENTITY_TYPE,
            Codec.list(ResourceLocation.CODEC)
        ).remover(DataMapValueRemover.Default.codec())
        .merger(DataMapValueMerger.listMerger())
        .build();

    @SubscribeEvent
    public static final void onRegisterDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(BLOCK_ENTITY_ADVANCEMENTS);
    };
};
