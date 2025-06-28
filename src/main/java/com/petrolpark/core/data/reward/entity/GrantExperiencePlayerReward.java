package com.petrolpark.core.data.reward.entity;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * Give a Player some XP.
 */
public record GrantExperiencePlayerReward(NumberProvider amount) implements IPlayerReward {

    public static final MapCodec<GrantExperiencePlayerReward> CODEC = CodecHelper.singleFieldMap(NumberProviders.CODEC, "amount", GrantExperiencePlayerReward::amount, GrantExperiencePlayerReward::new);

    @Override
    public void rewardPlayer(Player player, LootContext context, float multiplier) {
        player.giveExperiencePoints(amount.getInt(context));
    };

    @Override
    public void render(GuiGraphics graphics) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        NumberEstimate amount = NumberEstimate.get(amount());
        if (amount.unknown()) builder.add(translate("unknown_amount"));
        else builder.add(translateSimple(amount.getIntComponent()));
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.GRANT_EXPERIENCE.get();
    };
    
};
