package com.petrolpark.core.data.reward.team;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkDataComponents;
import com.petrolpark.PetrolparkLootContextParams;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.core.shop.Shop;
import com.petrolpark.core.shop.ShopsData;
import com.petrolpark.core.team.ITeam;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record GrantShopXPTeamReward(Holder<Shop> shop, NumberProvider amount) implements ITeamReward {

    public static final MapCodec<GrantShopXPTeamReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Shop.CODEC.fieldOf("shop").forGetter(GrantShopXPTeamReward::shop),
        NumberProviders.CODEC.fieldOf("amount").forGetter(GrantShopXPTeamReward::amount)
    ).apply(instance, GrantShopXPTeamReward::new));

    @Override
    public void reward(ITeam team, LootContext context, float multiplier) {
        team.getOrDefault(PetrolparkDataComponents.SHOPS_DATA, new ShopsData()).grantXP(shop, amount.getInt(context));
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
    public TeamRewardType getType() {
        return PetrolparkRewardTypes.GRANT_SHOP_XP.get();
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
