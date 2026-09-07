package petrolpark.mc.library.registry;

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.badge.BadgeItem;
import petrolpark.mc.library.core.badge.BadgeItem.BadgeAward;
import petrolpark.mc.library.core.data.recipe.bogglePattern.BogglePatternHelper;
import petrolpark.mc.library.core.data.reward.team.OneTimeTeamReward;
import petrolpark.mc.library.core.flags.AbstractFlagPole;
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.item.crafting.recipeBook.RecipeReferenceDataComponent;
import petrolpark.mc.library.core.world.item.decay.DecayTime;
import petrolpark.mc.library.core.world.item.decay.product.IDecayProduct;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.RestaurantsData;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;
import petrolpark.mc.library.util.WoodHelper;
import petrolpark.mc.library.util.codec.CodecHelper;

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

    // DECAY

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

    // RESTAURANTS

    /**
     * Reference to a {@link Restaurant}
     */
    public static final DataComponentType<Holder<Restaurant>> RESTAURANT = register("restaurant", builder -> builder
        .persistent(Restaurant.CODEC)
        .networkSynchronized(Restaurant.STREAM_CODEC)
    );

    // public static final DataComponentType<IRestaurantOrder> RESTAURANT_ORDER = register("restaurant_order", builder -> builder
    //     .persistent(IRestaurantOrder.SERVER_CODEC)
    //     .networkSynchronized(ClientRestaurantOrder.STREAM_CODEC)
    // );

    public static final DataComponentType<List<ItemStack>> RESTAURANT_ORDER_EXAMPLES = register("restaurant_order_example_stacks", builder -> builder
        .persistent(ItemStack.SINGLE_ITEM_CODEC.listOf())
        .networkSynchronized(ItemStack.LIST_STREAM_CODEC)
    );

    public static final DataComponentType<ICustomer.Provider> CUSTOMER_PROVIDER = register("customer_provider", builder -> builder
        .persistent(ICustomer.Provider.CODEC)
        .networkSynchronized(ICustomer.Provider.STREAM_CODEC)
    );

    // MISC

    public static final DataComponentType<Integer> BOGGLE_PATTERN = register("boggle_pattern", builder -> builder
        .persistent(BogglePatternHelper.SHORT_CODEC)
        .networkSynchronized(BogglePatternHelper.SHORT_STREAM_CODEC)
    );

    public static final DataComponentType<Holder<LootItemFunction>> FERTILIZER_EFFECT = register("fertilizer_effect", builder -> builder
        .persistent(LootItemFunctions.CODEC)
    );

    public static final DataComponentType<WoodHelper.Wood> WOOD = register("wood", builder -> builder
        .persistent(WoodHelper.Wood.CODEC)
        .networkSynchronized(WoodHelper.Wood.STREAM_CODEC)
    );

    // TEAMS

    public static final DataComponentType<RestaurantsData> TEAM_RESTAURANTS = register("team_restaurants", builder -> builder
        .persistent(RestaurantsData.CODEC)
    );

    public static final DataComponentType<Set<ResourceLocation>> TEAM_ONE_TIME_REWARDS = register("team_one_time_rewards", builder -> builder
        .persistent(OneTimeTeamReward.IDS_CODEC)
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
