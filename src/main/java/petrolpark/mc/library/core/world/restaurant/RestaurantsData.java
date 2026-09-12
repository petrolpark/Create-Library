package petrolpark.mc.library.core.world.restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.restaurant.order.IRestaurantOrder;
import petrolpark.mc.library.registry.PetrolparkCriteriaTriggers;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;

public record RestaurantsData(
    Map<Holder<Restaurant>, RestaurantData> data
) {

    public static final RestaurantsData EMPTY = new RestaurantsData(Collections.emptyMap());

    public static final Codec<RestaurantsData> CODEC = Codec.unboundedMap(Restaurant.CODEC, RestaurantData.CODEC).xmap(RestaurantsData::new, RestaurantsData::data);

    @OnlyIn(Dist.CLIENT)
    public Component getName(Holder<Restaurant> restaurant) {
        return Optional.ofNullable(data().get(restaurant)).flatMap(RestaurantData::customName).map(Component::literal).orElse(restaurant.value().getName().copy());
    };

    public static final void modify(MutableDataComponentHolder componentHolder, Consumer<RestaurantsData.Mutable> modification) {
        final RestaurantsData originalData = componentHolder.getOrDefault(PetrolparkDataComponentTypes.TEAM_RESTAURANTS, RestaurantsData.EMPTY);
        final RestaurantsData.Mutable mutable = originalData.mutable();
        modification.accept(mutable);
        final RestaurantsData modifiedData = mutable.toImmutable();
        componentHolder.set(PetrolparkDataComponentTypes.TEAM_RESTAURANTS, modifiedData);
        if (componentHolder instanceof ITeam team) {
            // Iterate to find changed Restaurants (ignoring any which have now been removed)
            for (Map.Entry<Holder<Restaurant>, RestaurantData> entry : modifiedData.data().entrySet()) {
                if (!entry.getValue().equals(originalData.data().get(entry.getKey()))) team.streamOnlineMembers().forEach(player -> PetrolparkCriteriaTriggers.RESTAURANT_CHANGED.get().trigger(player, entry.getKey(), team));
            };
        };
    };

    protected RestaurantsData.Mutable mutable() {
        return data().entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().mutable(),
            (data1, data2) -> {throw new IllegalStateException("Duplicate data for restaurants");},
            RestaurantsData.Mutable::new
        ));
    };

    public class Mutable extends HashMap<Holder<Restaurant>, RestaurantData.Mutable> {

        protected Mutable() {
            super();
        };

        public RestaurantData.Mutable getOrCreate(Holder<Restaurant> restaurant) {
            return computeIfAbsent(restaurant, $ -> new RestaurantData().mutable());
        };

        public RestaurantsData toImmutable() {
            return new RestaurantsData(entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toImmutable())));
        };
    };

    public record RestaurantData(
        int ordersTaken,
        int ordersFulfilled,
        int xp,
        Optional<String> customName,
        List<IRestaurantOrder> menu
    ) {

        public static final Codec<RestaurantData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("orders_taken").forGetter(RestaurantData::ordersTaken),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("orders_fulfilled").forGetter(RestaurantData::ordersFulfilled),
            Codec.INT.fieldOf("xp").forGetter(RestaurantData::xp),
            Codec.STRING.optionalFieldOf("custom_name").forGetter(RestaurantData::customName),
            IRestaurantOrder.SERVER_CODEC.listOf().fieldOf("menu").forGetter(RestaurantData::menu)
        ).apply(instance, RestaurantData::new));

        public RestaurantData() {
            this(0, 0, 0, Optional.empty(), Collections.emptyList());
        };

        protected RestaurantData.Mutable mutable() {
            return new RestaurantData.Mutable();
        };
        
        public class Mutable {

            public int totalOrdersTaken;
            public int totalOrdersFulfilled;

            public int xp;
            public @Nonnull Optional<String> customName;
            public @Nonnull List<IRestaurantOrder> menu;

            protected Mutable() {
                this.totalOrdersTaken = ordersTaken();
                this.totalOrdersFulfilled = ordersFulfilled();
                this.xp = xp();
                this.customName = customName();
                this.menu = new ArrayList<>(menu());
            };

            public RestaurantData toImmutable() {
                return new RestaurantData(totalOrdersTaken, totalOrdersFulfilled, xp, customName, menu);
            };
        };
    };
};
