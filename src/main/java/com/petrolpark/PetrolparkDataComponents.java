package com.petrolpark;

import java.util.List;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.serialization.Codec;
import com.petrolpark.badge.BadgeItem;
import com.petrolpark.badge.BadgeItem.BadgeAward;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.util.NetworkHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PetrolparkDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Petrolpark.MOD_ID);

    public static final DataComponentType<BadgeItem.BadgeAward> BADGE_AWARD = register(
        "badge_award",
        builder -> builder.persistent(BadgeAward.CODEC).networkSynchronized(BadgeAward.STREAM_CODEC)
    );

    public static final DataComponentType<List<Holder<Contaminant>>> ORPHAN_CONTAMINANTS = register(
        "contamination", 
        builder -> builder.persistent(Codec.list(Contaminant.CODEC)).networkSynchronized(NetworkHelper.listStreamCodec(Contaminant.STREAM_CODEC))
    );

    public static final DataComponentType<Rotation> ROTATION_WHILE_FLYING = register(
        "rotation_while_flying",
        builder -> builder.persistent(Rotation.CODEC).networkSynchronized(NetworkHelper.enumStreamCodec(Rotation.class))
    );
    
    private static <T> DataComponentType<T> register(String name, UnaryOperator<Builder<T>> builder) {
		DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
		DATA_COMPONENTS.register(name, () -> type);
		return type;
	};

	@Internal
	public static void register(IEventBus modEventBus) {
		DATA_COMPONENTS.register(modEventBus);
	};
};
