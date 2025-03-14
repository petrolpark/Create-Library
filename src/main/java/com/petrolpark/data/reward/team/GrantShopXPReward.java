package com.petrolpark.data.reward.team;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkLootContextParams;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.data.reward.IReward;
import com.petrolpark.data.reward.RewardType;
import com.petrolpark.shop.Shop;
import com.petrolpark.shop.TeamShopsData;
import com.petrolpark.team.ITeam;
import com.petrolpark.team.data.TeamDataTypes;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class GrantShopXPReward implements IReward {

    public final ResourceLocation shopRL;
    public final NumberProvider amount;

    public GrantShopXPReward(ResourceLocation shopRL, NumberProvider amount) {
        this.shopRL = shopRL;
        this.amount = amount;
    };

    @Override
    public void reward(LootContext context, float multiplier) {
        ITeam<?> team = context.getParam(PetrolparkLootContextParams.TEAM);
        if (team != null) {
            Shop shop = context.getLevel().registryAccess().registryOrThrow(PetrolparkRegistries.Keys.SHOP).get(shopRL);
            if (shop == null) Petrolpark.LOGGER.warn("Unknown Shop: "+shopRL);
            ((TeamShopsData)team.getTeamData(TeamDataTypes.SHOPS.get())).grantXP(shop, amount.getInt(context));
        };
    };

    @Override
    public void render(GuiGraphics graphics) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    };

    @Override
    public Component getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    };

    @Override
    public RewardType getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union(Collections.singleton(PetrolparkLootContextParams.TEAM), amount.getReferencedContextParams());
    };

    // public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<GrantShopXPReward> {

    //     @Override
    //     public void serialize(JsonObject json, GrantShopXPReward value, JsonSerializationContext serializationContext) {
    //         json.addProperty("shop", value.shopRL.toString());
    //         json.add("amount", serializationContext.serialize(value.amount, NumberProvider.class));
    //     };

    //     @Override
    //     public GrantShopXPReward deserialize(JsonObject json, JsonDeserializationContext deserializationContext) {
    //         return new GrantShopXPReward(ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json, "shop")), GsonHelper.getAsObject(json, "amount", deserializationContext, NumberProvider.class));
    //     };

    // };
    
};
