package petrolpark.mc.library.core.world.restaurant.customer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.order.ClientRestaurantOrder;
import petrolpark.mc.library.core.world.restaurant.order.IRestaurantOrder;

@ParametersAreNonnullByDefault
public abstract class AbstractCustomer implements ICustomer {

    protected final Holder<Restaurant> restaurant;
    protected final ITeam.Provider teamProvider;
    protected final IRestaurantOrder order;
    protected final long orderTime;

    public AbstractCustomer(Holder<Restaurant> restaurant, ITeam.Provider teamProvider, IRestaurantOrder order, long orderTime) {
        this.restaurant = restaurant;
        this.teamProvider = teamProvider;
        this.order = order;
        this.orderTime = orderTime;
    };

    @Override
    public boolean isNone() {
        return false;
    };

    @Override
    public @Nonnull Holder<Restaurant> getRestaurant() {
        return restaurant;
    };

    @Override
    public @Nonnull ITeam.Provider getTeamProvider() {
        return teamProvider;
    };

    @Override
    public @Nonnull IRestaurantOrder getOrder() {
        return order;
    };

    @Override
    public long getOrderTime() {
        return orderTime;
    };

    public CompoundTag serializeNBT(HolderLookup.Provider registries) throws IllegalStateException {
        final CompoundTag tag = new CompoundTag();
        final RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        tag.put("restaurant", Restaurant.CODEC.encodeStart(ops, restaurant).getOrThrow());
        tag.put("team", ITeam.Provider.CODEC.encodeStart(ops, teamProvider).getOrThrow());
        tag.put("order", IRestaurantOrder.SERVER_CODEC.encodeStart(ops, order).getOrThrow());
        tag.putLong("order_time", orderTime);
        return tag;
    };

    public static <C extends AbstractCustomer> C deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt, AbstractCustomer.Factory<C> factory) throws IllegalStateException {
        final RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        return factory.create(
            Restaurant.CODEC.parse(ops, nbt.get("restaurant")).getOrThrow(),
            ITeam.Provider.CODEC.parse(ops, nbt.get("team")).getOrThrow(),
            IRestaurantOrder.SERVER_CODEC.parse(ops, nbt.get("order")).getOrThrow(),
            nbt.getLong("order_time")
        );
    };

    public void writeBuffer(RegistryFriendlyByteBuf buffer) {
        Restaurant.STREAM_CODEC.encode(buffer, restaurant);
        ITeam.Provider.STREAM_CODEC.encode(buffer, teamProvider);
        ClientRestaurantOrder.STREAM_CODEC.encode(buffer, order);
        buffer.writeVarLong(orderTime);
    };

    public static <C extends AbstractCustomer> C readBuffer(RegistryFriendlyByteBuf buffer, AbstractCustomer.Factory<C> factory) {
        return factory.create(
            Restaurant.STREAM_CODEC.decode(buffer),
            ITeam.Provider.STREAM_CODEC.decode(buffer),
            ClientRestaurantOrder.STREAM_CODEC.decode(buffer),
            buffer.readVarLong()
        );
    };

    public interface Factory<C extends AbstractCustomer> {

        public C create(Holder<Restaurant> restaurant, ITeam.Provider teamProvider, IRestaurantOrder order, long orderTime);
    };
    
};
