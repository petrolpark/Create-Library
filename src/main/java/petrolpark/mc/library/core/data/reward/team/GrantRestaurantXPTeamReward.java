package petrolpark.mc.library.core.data.reward.team;

import java.util.Collections;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.data.reward.info.INamedRewardInfo;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.RestaurantsData;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

@ParametersAreNonnullByDefault
public record GrantRestaurantXPTeamReward(Holder<Restaurant> restaurant, NumberProvider amount) implements ITeamReward {

    public static final MapCodec<GrantRestaurantXPTeamReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Restaurant.CODEC.fieldOf("restaurant").forGetter(GrantRestaurantXPTeamReward::restaurant),
        NumberProviders.CODEC.fieldOf("amount").forGetter(GrantRestaurantXPTeamReward::amount)
    ).apply(instance, GrantRestaurantXPTeamReward::new));

    @Override
    public boolean reward(ITeam team, LootContext context, float multiplier, boolean simulate) {
        if (!simulate) RestaurantsData.modify(team, data -> data.getOrCreate(restaurant()).xp += amount().getInt(context));
        return true;
    };

    @Override
    public IRewardInfo info() {
        return new GrantRestaurantXPTeamReward.Info(restaurant(), NumberEstimate.get(amount()));
    };

    public record Info(Holder<Restaurant> restaurant, NumberEstimate amount) implements INamedRewardInfo {

        public static final ResourceLocation RESTAURANT_EXPERIENCE_ORBS_TEXTURE = Petrolpark.asResource("item/gui/restaurant_experience_orbs");

        public static final MapCodec<GrantRestaurantXPTeamReward.Info> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Restaurant.CODEC.fieldOf("restaurant").forGetter(GrantRestaurantXPTeamReward.Info::restaurant),
                NumberEstimate.fieldCodec("amount").forGetter(GrantRestaurantXPTeamReward.Info::amount)
            ).apply(instance, GrantRestaurantXPTeamReward.Info::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, GrantRestaurantXPTeamReward.Info> STREAM_CODEC = StreamCodec.composite(
            Restaurant.STREAM_CODEC, GrantRestaurantXPTeamReward.Info::restaurant,
            NumberEstimate.STREAM_CODEC, GrantRestaurantXPTeamReward.Info::amount,
            GrantRestaurantXPTeamReward.Info::new
        );

        @Override
        public void render(GuiGraphics graphics) {
            graphics.blit(0, 0, 0, 16, 16, Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(RESTAURANT_EXPERIENCE_ORBS_TEXTURE));
        };

        @Override
        public void addToDescription(IndentedTooltipBuilder builder) {
            if (amount().unknown() || amount().equals(NumberEstimate.POSITIVE)) builder.add(translate("unknown_amount", restaurant().value().getName()));
            else builder.add(translateSimple(amount().getIntComponent(), restaurant().value().getName()));
        };

        @Override
        public TeamRewardAndInfoType getRewardInfoType() {
            return PetrolparkRewardTypes.TEAM_GRANT_RESTAURANT_XP.get();
        };
    };

    @Override
    public TeamRewardAndInfoType getType() {
        return PetrolparkRewardTypes.TEAM_GRANT_RESTAURANT_XP.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Collections.singleton(PetrolparkLootContextParams.TEAM);
    };

    @Override
    public void validate(ValidationContext context) {
        ITeamReward.super.validate(context);
        amount().validate(context.forChild(".amount"));
    };
    
};
