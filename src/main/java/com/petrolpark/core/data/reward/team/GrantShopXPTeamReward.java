package com.petrolpark.core.data.reward.team;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkDataComponentTypes;
import com.petrolpark.PetrolparkLootContextParams;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.core.shop.Shop;
import com.petrolpark.core.shop.ShopsData;
import com.petrolpark.core.team.ITeam;
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

public record GrantShopXPTeamReward(Holder<Shop> shop, NumberProvider amount) implements ITeamReward {

    public static final ResourceLocation SHOP_EXPERIENCE_ORBS_TEXTURE = Petrolpark.asResource("item/gui/shop_experience_orbs");

    public static final MapCodec<GrantShopXPTeamReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Shop.CODEC.fieldOf("shop").forGetter(GrantShopXPTeamReward::shop),
        NumberProviders.CODEC.fieldOf("amount").forGetter(GrantShopXPTeamReward::amount)
    ).apply(instance, GrantShopXPTeamReward::new));

    @Override
    public void reward(ITeam team, LootContext context, float multiplier) {
        team.getOrDefault(PetrolparkDataComponentTypes.TEAM_SHOPS, new ShopsData()).grantXP(shop, amount.getInt(context));
    };

    @Override
    public void render(GuiGraphics graphics) {
        graphics.blit(0, 0, 0, 16, 16, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SHOP_EXPERIENCE_ORBS_TEXTURE));
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        NumberEstimate amount = NumberEstimate.get(amount());
        if (amount.unknown()) builder.add(translate("unknown_amount", shop.value().getName()));
        else builder.add(translateSimple(amount.getIntComponent(), shop.value().getName()));
    };

    @Override
    public TeamRewardType getType() {
        return PetrolparkRewardTypes.GRANT_SHOP_XP.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Sets.union(Collections.singleton(PetrolparkLootContextParams.TEAM), amount.getReferencedContextParams());
    };
    
};
