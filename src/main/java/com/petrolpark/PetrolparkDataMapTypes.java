package com.petrolpark;

import java.util.List;

import com.mojang.serialization.Codec;
import com.petrolpark.core.contamination.Contaminant;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber
public class PetrolparkDataMapTypes {

    public static final AdvancedDataMapType<Item, List<Holder<Contaminant>>, DataMapValueRemover.Default<List<Holder<Contaminant>>, Item>>
    
    ITEM_INTRINSIC_CONTAMINANTS = contaminantListDataMapType(Petrolpark.asResource("intrinsic_contaminants"), Registries.ITEM),
    ITEM_SHOWN_IF_ABSENT_CONTAMINANTS = contaminantListDataMapType(Petrolpark.asResource("shown_if_absent_contaminants"), Registries.ITEM);

    public static final AdvancedDataMapType<Fluid, List<Holder<Contaminant>>, DataMapValueRemover.Default<List<Holder<Contaminant>>, Fluid>>
    
    FLUID_INTRINSIC_CONTAMINANTS = contaminantListDataMapType(Petrolpark.asResource("intrinsic_contaminants"), Registries.FLUID),
    FLUID_SHOWN_IF_ABSENT_CONTAMINANTS = contaminantListDataMapType(Petrolpark.asResource("shown_if_absent_contaminants"), Registries.FLUID);

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

    public static final <T> AdvancedDataMapType<T, List<Holder<Contaminant>>, DataMapValueRemover.Default<List<Holder<Contaminant>>, T>> contaminantListDataMapType(ResourceLocation name, ResourceKey<Registry<T>> registry) {
        return AdvancedDataMapType.builder(
            name,
            registry,
            Codec.list(Contaminant.CODEC)
        ).remover(DataMapValueRemover.Default.codec())
        .merger(DataMapValueMerger.listMerger())
        .build();
    };
};
