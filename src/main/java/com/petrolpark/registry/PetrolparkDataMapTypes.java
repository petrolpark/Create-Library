package com.petrolpark.registry;

import java.util.List;

import com.mojang.serialization.Codec;
import com.petrolpark.Petrolpark;
import com.petrolpark.core.flags.Flag;

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

    public static final AdvancedDataMapType<Item, List<Holder<Flag>>, DataMapValueRemover.Default<List<Holder<Flag>>, Item>>
    
    ITEM_INTRINSIC_FLAGS = flagListDataMapType(Petrolpark.asResource("intrinsic_flags"), Registries.ITEM),
    ITEM_SHOWN_IF_ABSENT_FLAGS = flagListDataMapType(Petrolpark.asResource("shown_if_absent_flags"), Registries.ITEM);

    public static final AdvancedDataMapType<Fluid, List<Holder<Flag>>, DataMapValueRemover.Default<List<Holder<Flag>>, Fluid>>
    
    FLUID_INTRINSIC_FLAGS = flagListDataMapType(Petrolpark.asResource("intrinsic_flags"), Registries.FLUID),
    FLUID_SHOWN_IF_ABSENT_FLAGS = flagListDataMapType(Petrolpark.asResource("shown_if_absent_flags"), Registries.FLUID);

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
        event.register(ITEM_INTRINSIC_FLAGS);
        event.register(ITEM_SHOWN_IF_ABSENT_FLAGS);
        event.register(FLUID_INTRINSIC_FLAGS);
        event.register(FLUID_SHOWN_IF_ABSENT_FLAGS);
        event.register(BLOCK_ENTITY_ADVANCEMENTS);
    };

    public static final <T> AdvancedDataMapType<T, List<Holder<Flag>>, DataMapValueRemover.Default<List<Holder<Flag>>, T>> flagListDataMapType(ResourceLocation name, ResourceKey<Registry<T>> registry) {
        return AdvancedDataMapType.builder(
            name,
            registry,
            Codec.list(Flag.CODEC)
        ).remover(DataMapValueRemover.Default.codec())
        .merger(DataMapValueMerger.listMerger())
        .build();
    };
};
