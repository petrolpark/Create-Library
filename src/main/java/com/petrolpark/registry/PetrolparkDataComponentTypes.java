package com.petrolpark.registry;

import java.util.List;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.petrolpark.Petrolpark;
import com.petrolpark.core.badge.BadgeItem;
import com.petrolpark.core.badge.BadgeItem.BadgeAward;
import com.petrolpark.core.data.recipe.bogglePattern.BogglePatternHelper;
import com.petrolpark.core.flags.AbstractFlagPole;
import com.petrolpark.core.flags.Flag;
import com.petrolpark.core.world.entity.player.team.ITeam;
import com.petrolpark.core.world.item.crafting.recipeBook.RecipeReferenceDataComponent;
import com.petrolpark.core.world.item.decay.DecayTime;
import com.petrolpark.core.world.item.decay.product.IDecayProduct;
import com.petrolpark.core.world.item.restaurant.Restaurant;
import com.petrolpark.core.world.item.restaurant.RestaurantsData;
import com.petrolpark.util.WoodHelper;
import com.petrolpark.util.codec.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PetrolparkDataComponentTypes {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Petrolpark.MOD_ID);

    public static final DataComponentType<BadgeItem.BadgeAward> BADGE_AWARD = register("badge_award", builder -> builder
        .persistent(BadgeAward.CODEC)
        .networkSynchronized(BadgeAward.STREAM_CODEC)
    );

    public static final DataComponentType<ITeam.Provider> TEAM_PROVIDER = register("team", builder -> builder
        .persistent(ITeam.Provider.CODEC)
        .networkSynchronized(ITeam.Provider.STREAM_CODEC)
    );

    public static final DataComponentType<List<Holder<Flag>>> ORPHAN_FLAGS = register("flags", builder -> builder
        .persistent(AbstractFlagPole.ORPHAN_HOLDER_LIST_CODEC)
        .networkSynchronized(AbstractFlagPole.ORPHAN_HOLDER_LIST_STREAM_CODEC)
    );

    public static final DataComponentType<RecipeReferenceDataComponent> RECIPE_REFERENCE = register("recipe_reference", builder -> builder
        .persistent(RecipeReferenceDataComponent.CODEC)
        .networkSynchronized(RecipeReferenceDataComponent.STREAM_CODEC)
    );

    public static final DataComponentType<IDecayProduct> DECAY_PRODUCT = register("decay_product", builder -> builder
        .persistent(IDecayProduct.CODEC)
        .networkSynchronized(IDecayProduct.STREAM_CODEC)
    );
    public static final DataComponentType<DecayTime> DECAY_TIME = register("decay_time", builder -> builder
        .persistent(DecayTime.CODEC)
        .networkSynchronized(DecayTime.STREAM_CODEC)
    );
    public static final DataComponentType<Long> DECAY_START_TIME = register("decay_start_time", builder -> builder
        .persistent(Codec.LONG)
        .networkSynchronized(ByteBufCodecs.VAR_LONG)
    );

    public static final DataComponentType<Integer> BOGGLE_PATTERN = register("boggle_pattern", builder -> builder
        .persistent(BogglePatternHelper.SHORT_CODEC)
        .networkSynchronized(BogglePatternHelper.SHORT_STREAM_CODEC)
    );

    public static final DataComponentType<Holder<LootItemFunction>> FERTILIZER_EFFECT = register("fertilizer_effect", builder -> builder
        .persistent(LootItemFunctions.CODEC)
    );

    public static final DataComponentType<Holder<Restaurant>> RESTAURANT = register("restaurant", builder -> builder
        .persistent(Restaurant.CODEC)
        .networkSynchronized(Restaurant.STREAM_CODEC)
    );

    public static final DataComponentType<WoodHelper.Wood> WOOD = register("wood", builder -> builder
        .persistent(WoodHelper.Wood.CODEC)
        .networkSynchronized(WoodHelper.Wood.STREAM_CODEC)
    );

    // TEAMS

    public static final DataComponentType<RestaurantsData> TEAM_RESTAURANTS = register("team_restaurants", builder -> builder
        .persistent(RestaurantsData.CODEC)
    );

    // Create //TODO move

    public static final DataComponentType<Rotation> ROTATION_WHILE_FLYING = register("rotation_while_flying", builder -> builder
        .persistent(Rotation.CODEC)
        .networkSynchronized(CodecHelper.enumStream(Rotation.class))
    );
    
    private static <T> DataComponentType<T> register(String name, UnaryOperator<Builder<T>> builder) {
		DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
		DATA_COMPONENTS.register(name, () -> type);
		return type;
	};

	@ApiStatus.Internal
	public static final void register(IEventBus modEventBus) {
		DATA_COMPONENTS.register(modEventBus);
	};
};
