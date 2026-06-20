package com.petrolpark.core.data.reward.team;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.Petrolpark;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.core.world.entity.player.team.ITeam;
import com.petrolpark.core.world.item.restaurant.Restaurant;
import com.petrolpark.core.world.item.restaurant.RestaurantsData;
import com.petrolpark.registry.PetrolparkDataComponentTypes;
import com.petrolpark.registry.PetrolparkLootContextParams;
import com.petrolpark.registry.PetrolparkRewardTypes;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record GrantRestaurantXPTeamReward(Holder<Restaurant> restaurant, NumberProvider amount) implements ITeamReward {

    public static final ResourceLocation RESTAURANT_EXPERIENCE_ORBS_TEXTURE = Petrolpark.asResource("item/gui/restaurant_experience_orbs");

    public static final MapCodec<GrantRestaurantXPTeamReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Restaurant.CODEC.fieldOf("restaurant").forGetter(GrantRestaurantXPTeamReward::restaurant),
        NumberProviders.CODEC.fieldOf("amount").forGetter(GrantRestaurantXPTeamReward::amount)
    ).apply(instance, GrantRestaurantXPTeamReward::new));

    @Override
    public void reward(ITeam team, LootContext context, float multiplier) {
        team.getOrDefault(PetrolparkDataComponentTypes.TEAM_RESTAURANTS, new RestaurantsData()).grantXP(restaurant, amount.getInt(context));
    };

    @Override
    public void render(GuiGraphics graphics) {
        graphics.blit(0, 0, 0, 16, 16, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(RESTAURANT_EXPERIENCE_ORBS_TEXTURE));
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        NumberEstimate amount = NumberEstimate.get(amount());
        if (amount.unknown()) builder.add(translate("unknown_amount", restaurant.value().getName()));
        else builder.add(translateSimple(amount.getIntComponent(), restaurant.value().getName()));
    };

    @Override
    public TeamRewardType getType() {
        return PetrolparkRewardTypes.GRANT_RESTAURANT_XP.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union(Collections.singleton(PetrolparkLootContextParams.TEAM), amount.getReferencedContextParams());
    };
    
};
