package com.petrolpark;

import java.util.List;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.serialization.Codec;
import com.petrolpark.badge.BadgeItem;
import com.petrolpark.badge.BadgeItem.BadgeAward;
import com.petrolpark.compat.create.dough.Dough;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.contamination.Contamination;
import com.petrolpark.shop.Shop;
import com.petrolpark.shop.ShopsData;
import com.petrolpark.team.ITeam;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PetrolparkDataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Petrolpark.MOD_ID);

    public static final DataComponentType<BadgeItem.BadgeAward> BADGE_AWARD = register(
        "badge_award",
        builder -> builder.persistent(BadgeAward.CODEC).networkSynchronized(BadgeAward.STREAM_CODEC)
    );

    public static final DataComponentType<ITeam.Provider> TEAM_PROVIDER = register(
        "team",
        builder -> builder.persistent(ITeam.Provider.CODEC).networkSynchronized(ITeam.Provider.STREAM_CODEC)
    );

    public static final DataComponentType<List<Holder<Contaminant>>> ORPHAN_CONTAMINANTS = register(
        "contamination", 
        builder -> builder.persistent(Contamination.ORPHAN_HOLDER_LIST_CODEC).networkSynchronized(Contamination.ORPHAN_HOLDER_LIST_STREAM_CODEC)
    );

    public static final DataComponentType<Long> DECAYING_ITEM_CREATION_TIME = register(
        "creation_time",
        builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
    );

    public static final DataComponentType<Holder<Shop>> SHOP = register(
        "shop",
        builder -> builder.persistent(Shop.CODEC).networkSynchronized(Shop.STREAM_CODEC)
    );

    public static final DataComponentType<ShopsData> SHOPS_DATA = register(
        "shops_data", builder -> builder.persistent(ShopsData.CODEC)
    );

    // Create

    public static final DataComponentType<Rotation> ROTATION_WHILE_FLYING = register(
        "rotation_while_flying",
        builder -> builder.persistent(Rotation.CODEC).networkSynchronized(CodecHelper.enumStream(Rotation.class))
    );

    public static final DataComponentType<Dough> DOUGH = register(
        "dough",
        builder -> builder.persistent(Dough.CODEC).networkSynchronized(Dough.STREAM_CODEC)
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
