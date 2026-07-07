package petrolpark.mc.library.core.data.reward.team;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.item.restaurant.Restaurant;
import petrolpark.mc.library.core.world.item.restaurant.RestaurantsData;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

public record GrantRestaurantXPTeamReward(Holder<Restaurant> restaurant, NumberProvider amount) implements ITeamReward {

    public static final ResourceLocation RESTAURANT_EXPERIENCE_ORBS_TEXTURE = Petrolpark.asResource("item/gui/restaurant_experience_orbs");

    public static final MapCodec<GrantRestaurantXPTeamReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Restaurant.ID_CODEC.fieldOf("restaurant").forGetter(GrantRestaurantXPTeamReward::restaurant),
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
