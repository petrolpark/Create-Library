package com.petrolpark.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ShopsData extends HashMap<Shop, ShopsData.TeamShop> {

    public static final Codec<ShopsData> CODEC = Codec.unboundedMap(PetrolparkRegistries.SHOP.byNameCodec(), TeamShop.CODEC).xmap(ShopsData::fromMap, Function.identity());

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopsData> STREAM_CODEC = StreamCodec.composite(
        , null, null
    );

    public static ShopsData fromMap(Map<Shop, ShopsData.TeamShop> map) {
        ShopsData teamShops = new ShopsData();
        teamShops.putAll(map);
        return teamShops;
    };

    public TeamShop getOrCreate(Shop shop) {
        return computeIfAbsent(shop, s -> defaultEntry());
    };

    public void grantXP(Shop shop, int amount) {
        getOrCreate(shop).xp += amount;
    };

    @OnlyIn(Dist.CLIENT)
    public Component getName(Shop shop) {
        return getOrCreate(shop).getCustomName().map(Component::literal).orElse(shop.getName().copy());
    };

    protected TeamShop defaultEntry() {
        return new TeamShop(0, Optional.empty());
    };

    protected static class TeamShop {

        public static final Codec<TeamShop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("xp").forGetter(TeamShop::getXp),
            Codec.STRING.optionalFieldOf("customName").forGetter(TeamShop::getCustomName)
        ).apply(instance, TeamShop::new));

        public int xp;
        public Optional<String> customName;

        public TeamShop(int xp, Optional<String> customName) {
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
