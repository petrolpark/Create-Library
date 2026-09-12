package petrolpark.mc.library.registry;

import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.map.HolderSetDataMapValueMerger;
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.world.entity.animal.mood.AnimalMoodModifier;

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

    public static final AdvancedDataMapType<MemoryModuleType<?>, HolderSet<PoiType>, DataMapValueRemover.Default<HolderSet<PoiType>, MemoryModuleType<?>>> MEMORY_POI_RELEASERS = AdvancedDataMapType
        .builder(
            Petrolpark.asResource("erasing_releases_pois"),
            Registries.MEMORY_MODULE_TYPE,
            RegistryCodecs.homogeneousList(Registries.POINT_OF_INTEREST_TYPE)
        ).remover(DataMapValueRemover.Default.codec())
        .merger(HolderSetDataMapValueMerger.create())
        .build();

    public static final AdvancedDataMapType<EntityType<?>, HolderSet<AnimalMoodModifier>, DataMapValueRemover.Default<HolderSet<AnimalMoodModifier>, EntityType<?>>> ANIMAL_MOOD_MODIFIERS = AdvancedDataMapType
        .builder(
            Petrolpark.asResource("mood_modifiers"),
            Registries.ENTITY_TYPE,
            HolderSetCodec.create(PetrolparkRegistries.Keys.ANIMAL_MOOD_MODIFIER, AnimalMoodModifier.CODEC, false)
        ).remover(DataMapValueRemover.Default.codec())
        .merger(HolderSetDataMapValueMerger.create())
        .build();

    @SubscribeEvent
    public static final void onRegisterDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ITEM_INTRINSIC_FLAGS);
        event.register(ITEM_SHOWN_IF_ABSENT_FLAGS);
        event.register(FLUID_INTRINSIC_FLAGS);
        event.register(FLUID_SHOWN_IF_ABSENT_FLAGS);

        event.register(BLOCK_ENTITY_ADVANCEMENTS);
        event.register(MEMORY_POI_RELEASERS);
        event.register(ANIMAL_MOOD_MODIFIERS);
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
