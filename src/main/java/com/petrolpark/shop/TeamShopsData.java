package com.petrolpark.shop;

import java.util.Map;

import javax.annotation.Nonnull;

import java.util.HashMap;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.team.data.ITeamDataType;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class TeamShopsData extends HashMap<Shop, TeamShopsData.TeamShop> {

    public TeamShop getOrCreate(Shop shop) {
        return computeIfAbsent(shop, s -> defaultEntry());
    };

    public void grantXP(Shop shop, int amount) {
        getOrCreate(shop).xp += amount;
    };

    public Component getName(Shop shop) {
        String customName = getOrCreate(shop).customName;
        if (customName.isEmpty()) return shop.getName();
        return Component.literal(customName);
    };

    protected TeamShop defaultEntry() {
        return new TeamShop("", 0);
    };

    protected class TeamShop {
        public int xp;
        public String customName;

        public TeamShop(String customName, int xp) {
            this.customName = customName;
            this.xp = xp;
        };
    };
    
    public static class Type implements ITeamDataType<TeamShopsData> {

        @Override
        public @Nonnull TeamShopsData getBlankInstance() {
            return new TeamShopsData();
        };

        @Override
        public boolean isBlank(TeamShopsData data) {
            return data.isEmpty();
        };

        @Override
        public TeamShopsData load(Level level, CompoundTag tag) {
            Registry<Shop> registry = level.registryAccess().registryOrThrow(PetrolparkRegistries.Keys.SHOP);
            TeamShopsData data = getBlankInstance();
            for (String key : tag.getAllKeys()) {
                Shop shop = registry.get(ResourceLocation.parse(key));
                if (shop != null && tag.contains(key, Tag.TAG_COMPOUND)) {
                    CompoundTag shopTag = tag.getCompound(key);
                    String customName = shopTag.getString("Name");
                    int xp = shopTag.getInt("XP");
                    if (xp != 0 && !customName.isEmpty()) data.put(shop, data.new TeamShop(customName, xp));
                };
            };
            return data;
        };

        @Override
        public CompoundTag save(Level level, TeamShopsData data) {
            Registry<Shop> registry = level.registryAccess().registryOrThrow(PetrolparkRegistries.Keys.SHOP);
            CompoundTag tag = new CompoundTag();
            for (Map.Entry<Shop, TeamShop> entry : data.entrySet()) {
                CompoundTag shopTag = new CompoundTag();
                if (!entry.getValue().customName.isEmpty()) shopTag.putString("Name", entry.getValue().customName);
                if (entry.getValue().xp != 0) shopTag.putInt("XP", entry.getValue().xp);
                tag.put(registry.getKey(entry.getKey()).toString(), shopTag);
            };
            return tag;
        };

    };
};
