package petrolpark.mc.library.core.world.restaurant.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.RestaurantsData;
import petrolpark.mc.library.core.world.restaurant.order.ClientRestaurantOrder;
import petrolpark.mc.library.core.world.restaurant.order.IRestaurantOrder;
import petrolpark.mc.library.core.world.restaurant.order.RestaurantOrderModifier;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.registry.PetrolparkLootContextParamSets;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.Lang;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

@ParametersAreNonnullByDefault
public interface ICustomer {

    public static NoCustomer none() {
        return NoCustomer.INSTANCE;
    };

    public boolean isNone();

    public Component getName();

    public BlockPos getPosition();

    public @Nonnull ICustomer.Provider getProvider();
    
    public @Nonnull Holder<Restaurant> getRestaurant();

    public @Nonnull ITeam.Provider getTeamProvider();
    
    public @Nonnull IRestaurantOrder getOrder();

    public long getOrderTime();

    public default void cancelOrder(ServerLevel level, Player player) {
        if (getRestaurant().value().orderCancellationRewards().isEmpty()) return;

        final LootParams.Builder builder = new LootParams.Builder(level).withParameter(PetrolparkLootContextParams.ITEM_HANDLER, new InvWrapper(player.getInventory()));
        supplyLootParams(level, builder);
        final LootContext lootContext = new LootContext.Builder(builder.create(PetrolparkLootContextParamSets.RESTAURANT_ORDER_REWARDS)).create(Optional.empty());
            
        for (Holder<IReward> rewardHolder : getRestaurant().value().orderCancellationRewards()) {
            rewardHolder.value().reward(lootContext, 1f, false);
        };
    };

    public default void tickWhileOrderItemHeld(ItemStack stack, Level level, Player player, int slotId) {};

    // public void clearOpenOrder();

    public default void supplyLootParams(ServerLevel level, LootParams.Builder builder) {
        builder
            .withParameter(PetrolparkLootContextParams.RESTAURANT, getRestaurant())
            .withParameter(PetrolparkLootContextParams.TEAM, getTeamProvider().provideTeam(level))
            .withParameter(PetrolparkLootContextParams.CUSTOMER, this);
    };

    /**
     * {@link ITeam} objects have one instance for each team, and cannot be serialized. {@link ITeam.Provider} are references to Teams, not the Teams themselves,
     * and so can be {@link ITeam.ProviderType#codec() serialized}, and have multiple instances per Team. A {@link ITeam.Provider} should uniquely {@link ITeam.Provider#provideTeam(Level) identify} the same Team every single time.
     */
    public static interface Provider {

        /**
         * Use {@link ICustomer.Provider#CODEC} instead.
         */
        @ApiStatus.Internal
        static Codec<Provider> TYPED_CODEC = PetrolparkRegistries.CUSTOMER_PROVIDER_TYPES
            .byNameCodec()
            .dispatch(ICustomer.Provider::getProviderType, ICustomer.ProviderType::codec);

        public static final Codec<ICustomer.Provider> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, Codec.unit(ICustomer.none())));

        public static final StreamCodec<RegistryFriendlyByteBuf, ICustomer.Provider> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkRegistries.Keys.CUSTOMER_PROVIDER_TYPE)
            .dispatch(ICustomer.Provider::getProviderType, ICustomer.ProviderType::streamCodec);

        public ICustomer provideCustomer(Level level);

        public default ICustomer.Provider getUpdated(Level level) {
            return this;
        };

        public ICustomer.ProviderType getProviderType();

        public static UnaryOperator<ICustomer.Provider> update(Level level) {
            return prov -> prov.getUpdated(level);
        };
    };

    public static record ProviderType(MapCodec<? extends ICustomer.Provider> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ICustomer.Provider> streamCodec) {

    };

    @OnlyIn(Dist.CLIENT)
    public default ClientRestaurantOrder.Description getDescription(Level level, Font font, int maxWidth, int lineHeight) {
        final List<Component> components = new ArrayList<>();

        components.add(getTeamProvider().provideTeam(level).getOrDefault(PetrolparkDataComponentTypes.TEAM_RESTAURANTS, RestaurantsData.EMPTY)
            .getName(getRestaurant()));
        components.add(getName());
        components.add(Component.empty());

        components.add(Lang.translate("gui.restaurant.order"));
        final IndentedTooltipBuilder orderBuilder = new IndentedTooltipBuilder.Wrapping(font, new ArrayList<>(), maxWidth);
        getOrder().ingredient().addToDescription(orderBuilder);
        components.addAll(orderBuilder.build());

        final List<RestaurantOrderModifier.Info> orderModifierInfos = getOrder().modifiersInfo();
        final IntList orderModifierLineIndices;
        if (!orderModifierInfos.isEmpty()) {
            components.add(Component.empty());
            components.add(Lang.translate("gui.restaurant.orderModifiers"));
            orderModifierLineIndices = new IntArrayList(orderModifierInfos.size());
            for (RestaurantOrderModifier.Info info : orderModifierInfos) {
                components.add(Component.empty());
                orderModifierLineIndices.add(components.size());
                final IndentedTooltipBuilder orderModifierBuilder = new IndentedTooltipBuilder.Wrapping(font, new ArrayList<>(), maxWidth);
                info.ingredient().addToDescription(orderModifierBuilder);
                components.addAll(orderModifierBuilder.build());
                components.add(Lang.translate("gui.restaurant.successMultiplier", info.successMultiplier().getComponent(Lang.ONE_DP_DF).withStyle(ChatFormatting.DARK_GREEN)));
                components.add(Lang.translate("gui.restaurant.failureMultiplier", info.failureMultiplier().getComponent(Lang.ONE_DP_DF).withStyle(ChatFormatting.DARK_RED)));
            };
        } else {
            orderModifierLineIndices = IntLists.emptyList();
        };

        final List<IRewardInfo> rewardInfos = getOrder().rewardsInfo();
        final int rewardsLineIndex = components.size() + 2;
        if (!rewardInfos.isEmpty()) {
            components.add(Component.empty());
            components.add(Lang.translate("gui.restaurant.rewards"));
            final int rewardsPerRow = maxWidth / 18;
            for (int i = 0; i <= 18 * (1 + rewardInfos.size() / rewardsPerRow) / lineHeight; i++)
                components.add(Component.empty());
        };

        return new ClientRestaurantOrder.Description(components, orderModifierLineIndices, rewardsLineIndex);
    };
};
