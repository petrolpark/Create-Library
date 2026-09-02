package petrolpark.mc.library.core.world.restaurant.customer;

import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.order.IRestaurantOrder;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkRegistries;

@ParametersAreNonnullByDefault
public interface ICustomer {

    public static NoCustomer none() {
        return NoCustomer.INSTANCE;
    };

    public boolean isNone();

    public Component getDescription();

    public boolean canInteractWith(Player player);

    public @Nonnull ICustomer.Provider getProvider();
    
    public @Nonnull Holder<Restaurant> getRestaurant();

    public @Nonnull ITeam.Provider getTeamProvider();
    
    public @Nonnull IRestaurantOrder getOrder();

    public long getOrderTime();

    public default void tickWhileOrderItemHeld(ItemStack stack, Level level, Player player, int slotId) {};

    // public void clearOpenOrder();

    public default void supplyLootParams(LootParams.Builder builder) {
        builder.withParameter(PetrolparkLootContextParams.CUSTOMER, this);
        final Holder<Restaurant> restaurant = getRestaurant();
        if (restaurant != null) builder.withParameter(PetrolparkLootContextParams.RESTAURANT, restaurant);
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
};
