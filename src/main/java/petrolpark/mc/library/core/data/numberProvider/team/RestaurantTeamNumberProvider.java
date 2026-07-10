package petrolpark.mc.library.core.data.numberProvider.team;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.RestaurantsData;
import petrolpark.mc.library.core.world.restaurant.RestaurantsData.RestaurantData;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;

/**
 * <p>{@code petrolpark:restaurant}</p>
 * 
 * Gets a numerical property of the given {@link Restaurant} as it pertains to the given {@link ITeam}.
 * 
 * Arguments:
 * <ul>
 * <li> {@code restaurant} - The Restaurant to query. If left blank, defaults to the {@link PetrolparkLootContextParams#RESTAURANT Restaurant provided in the LootContext}
 * <li> {@code value} - Any of {@code xp}, {@code orders_taken} or {@code orders_fulfilled}
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record RestaurantTeamNumberProvider(Optional<Holder<Restaurant>> restaurant, RestaurantTeamNumberProvider.Value value) implements TeamNumberProvider {

    public static final MapCodec<RestaurantTeamNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Restaurant.CODEC.optionalFieldOf("restaurant").forGetter(RestaurantTeamNumberProvider::restaurant),
            StringRepresentable.fromEnum(RestaurantTeamNumberProvider.Value::values).fieldOf("value").forGetter(RestaurantTeamNumberProvider::value)
        ).apply(instance, RestaurantTeamNumberProvider::new)
    );

    @Override
    public float getFloat(ITeam team, LootContext context) {
        return getInt(team, context);
    };

    @Override
    public int getInt(ITeam team, LootContext lootContext) {
        final Holder<Restaurant> restaurant = restaurant().orElseGet(() -> lootContext.getParam(PetrolparkLootContextParams.RESTAURANT));
        final RestaurantsData restaurants = team.get(PetrolparkDataComponentTypes.RESTAURANTS_DATA);
        if (restaurants == null) return 0;
        final RestaurantData data = restaurants.data().get(restaurant);
        if (data == null) return 0;
        return switch (value()) {
            case XP -> data.xp();
            case ORDERS_TAKEN -> data.ordersTaken();
            case ORDERS_FULFILLED -> data.ordersFulfilled();
        };
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.POSITIVE;
    };

    public enum Value implements StringRepresentable {
        XP,
        //TODO overall level
        ORDERS_TAKEN,
        ORDERS_FULFILLED,;

        private final String name;

        Value() {
            name = Lang.asId(name());
        };

        @Override
        public String getSerializedName() {
            return name;
        };
        
    };

    @Override
    public LootTeamNumberProviderType getTeamNumberProviderType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTeamNumberProviderType'");
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return restaurant().isEmpty() ? Collections.singleton(PetrolparkLootContextParams.RESTAURANT) : Collections.emptySet();
    };
    
};
