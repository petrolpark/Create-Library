package com.petrolpark.core.world.item.restaurant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class RestaurantsData extends HashMap<Holder<Restaurant>, RestaurantsData.TeamRestaurant> {

    public static final Codec<RestaurantsData> CODEC = Codec.unboundedMap(Restaurant.CODEC, TeamRestaurant.CODEC).xmap(RestaurantsData::fromMap, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, RestaurantsData> STREAM_CODEC = ByteBufCodecs.map(RestaurantsData::new, Restaurant.STREAM_CODEC, TeamRestaurant.STREAM_CODEC);

    public static RestaurantsData fromMap(Map<Holder<Restaurant>, RestaurantsData.TeamRestaurant> map) {
        RestaurantsData teamRestaurants = new RestaurantsData(map.size());
        teamRestaurants.putAll(map);
        return teamRestaurants;
    };

    public RestaurantsData() {
        this(0);
    };

    public RestaurantsData(int size) {
        super(size);
    };

    public TeamRestaurant getOrCreate(Holder<Restaurant> restaurant) {
        return computeIfAbsent(restaurant, s -> defaultEntry());
    };

    public void grantXP(Holder<Restaurant> restaurant, int amount) {
        getOrCreate(restaurant).xp += amount;
    };

    @OnlyIn(Dist.CLIENT)
    public Component getName(Holder<Restaurant> restaurant) {
        return getOrCreate(restaurant).getCustomName().map(Component::literal).orElse(restaurant.value().getName().copy());
    };

    protected TeamRestaurant defaultEntry() {
        return new TeamRestaurant(0, Optional.empty());
    };

    protected static class TeamRestaurant {

        public static final Codec<TeamRestaurant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("xp").forGetter(TeamRestaurant::getXp),
            Codec.STRING.optionalFieldOf("customName").forGetter(TeamRestaurant::getCustomName)
        ).apply(instance, TeamRestaurant::new));

        public static final StreamCodec<FriendlyByteBuf, TeamRestaurant> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TeamRestaurant::getXp,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), TeamRestaurant::getCustomName,
            TeamRestaurant::new
        );

        public int xp;
        public Optional<String> customName;

        public TeamRestaurant(int xp, Optional<String> customName) {
            this.customName = customName;
            this.xp = xp;
        };

        public int getXp() {
            return xp;
        };

        public Optional<String> getCustomName() {
            return customName;
        };
    };
};
