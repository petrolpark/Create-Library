package com.petrolpark.core.data.reward;

import java.util.Collections;
import java.util.Set;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkLootContextParams;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.core.data.reward.team.ITeamReward;
import com.petrolpark.core.team.ITeam;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ContextTeamReward(ITeamReward reward) implements IReward {

    public static final MapCodec<ContextTeamReward> CODEC = CodecHelper.singleFieldMap(ITeamReward.CODEC, "reward", ContextTeamReward::reward, ContextTeamReward::new);

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(GuiGraphics graphics) {
        reward.render(graphics);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        builder.add(translateSimple());
        builder.indent();
        reward().addToDescription(builder);
        builder.unindent();
    };

    @Override
    public void reward(LootContext context, float multiplier) {
        ITeam team = context.getParam(PetrolparkLootContextParams.TEAM);
        if (team != null) reward.reward(team, context, multiplier);
    };

    @Override
    public RewardType getType() {
        return PetrolparkRewardTypes.CONTEXT_TEAM.get();
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Collections.singleton(PetrolparkLootContextParams.TEAM);
    };
    
};
