package com.petrolpark.data.reward;

import java.util.Collections;
import java.util.Set;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkLootContextParams;
import com.petrolpark.data.reward.team.ITeamReward;
import com.petrolpark.team.ITeam;
import com.petrolpark.util.NetworkHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ContextTeamReward(ITeamReward reward) implements IReward {

    public static final MapCodec<ContextTeamReward> CODEC = NetworkHelper.singleFieldMapCodec(ITeamReward.CODEC, "reward", ContextTeamReward::reward, ContextTeamReward::new);

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(GuiGraphics graphics) {
        reward.render(graphics);
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public Component getName() {
        return reward.getName();
    };

    @Override
    public void reward(LootContext context, float multiplier) {
        ITeam<?> team = context.getParam(PetrolparkLootContextParams.TEAM);
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
