package com.petrolpark.shop;

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

public class ShopsData extends HashMap<Holder<Shop>, ShopsData.TeamShop> {

    public static final Codec<ShopsData> CODEC = Codec.unboundedMap(Shop.CODEC, TeamShop.CODEC).xmap(ShopsData::fromMap, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, ShopsData> STREAM_CODEC = ByteBufCodecs.map(ShopsData::new, Shop.STREAM_CODEC, TeamShop.STREAM_CODEC);

    public static ShopsData fromMap(Map<Holder<Shop>, ShopsData.TeamShop> map) {
        ShopsData teamShops = new ShopsData(map.size());
        teamShops.putAll(map);
        return teamShops;
    };

    public ShopsData() {
        this(0);
    };

    public ShopsData(int size) {
        super(size);
    };

    public TeamShop getOrCreate(Holder<Shop> shop) {
        return computeIfAbsent(shop, s -> defaultEntry());
    };

    public void grantXP(Holder<Shop> shop, int amount) {
        getOrCreate(shop).xp += amount;
    };

    @OnlyIn(Dist.CLIENT)
    public Component getName(Holder<Shop> shop) {
        return getOrCreate(shop).getCustomName().map(Component::literal).orElse(shop.value().getName().copy());
    };

    protected TeamShop defaultEntry() {
        return new TeamShop(0, Optional.empty());
    };

    protected static class TeamShop {

        public static final Codec<TeamShop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("xp").forGetter(TeamShop::getXp),
            Codec.STRING.optionalFieldOf("customName").forGetter(TeamShop::getCustomName)
        ).apply(instance, TeamShop::new));

        public static final StreamCodec<FriendlyByteBuf, TeamShop> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TeamShop::getXp,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), TeamShop::getCustomName,
            TeamShop::new
        );

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
