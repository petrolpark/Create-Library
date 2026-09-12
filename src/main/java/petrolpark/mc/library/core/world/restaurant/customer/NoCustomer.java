package petrolpark.mc.library.core.world.restaurant.customer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.entity.player.team.NoTeam;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.order.ClientRestaurantOrder;
import petrolpark.mc.library.core.world.restaurant.order.IRestaurantOrder;
import petrolpark.mc.library.registry.PetrolparkCustomerProviderTypes;

@ParametersAreNonnullByDefault
public class NoCustomer implements ICustomer, ICustomer.Provider {

    static final NoCustomer INSTANCE = new NoCustomer();

    protected NoCustomer() {};

    // @Override
    // public int getElapsedOrderTime() {
    //     return 0;
    // };

    // @Override
    // public void clearOpenOrder() {};

    @Override
    public boolean isNone() {
        return true;
    };

    @Override
    public Component getName() {
        return Component.translatable("customer." + Petrolpark.MOD_ID + ".none");
    };

    @Override
    public BlockPos getPosition() {
        return BlockPos.ZERO;
    };

    @Override
    public @Nonnull NoCustomer getProvider() {
        return ICustomer.none();
    };
    
    @Override
    public @Nonnull Holder<Restaurant> getRestaurant() {
        throw new IllegalStateException("None customer has no restaurant");
    };

    @Override
    public @Nonnull NoTeam getTeamProvider() {
        return NoTeam.INSTANCE;
    };

    @Override
    public @Nonnull IRestaurantOrder getOrder() {
        throw new IllegalStateException("None customer has no order");
    };

    @Override
    public long getOrderTime() {
        return 0l;
    };

    @Override
    public void cancelOrder(ServerLevel level, Player player) {};

    @Override
    public void supplyLootParams(ServerLevel level, LootParams.Builder builder) {};

    @Override
    public ClientRestaurantOrder.Description getDescription(Level level, Font font, int maxWidth, int lineHeight) {
        return ClientRestaurantOrder.Description.EMPTY;
    };

    @Override
    public ICustomer provideCustomer(Level level) {
        return INSTANCE;
    };

    @Override
    public ProviderType getProviderType() {
        return PetrolparkCustomerProviderTypes.NONE.get();
    };
    
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj == this;
    };

    @Override
    public int hashCode() {
        return super.hashCode();
    };
    
};
