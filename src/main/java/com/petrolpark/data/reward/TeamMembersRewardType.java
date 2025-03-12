package com.petrolpark.data.reward;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.data.loot.PetrolparkLootContextParams;
import com.petrolpark.data.reward.entity.IEntityReward;
import com.petrolpark.team.ITeam;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record TeamMembersRewardType(IEntityReward reward, Either<NumberProvider, NumberProvider> who, boolean random) implements IReward {

    public static final MapCodec<TeamMembersRewardType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IEntityReward.CODEC.fieldOf("reward").forGetter(TeamMembersRewardType::reward),
        Codec.mapEither(
            NumberProviders.CODEC.fieldOf("count"),
            NumberProviders.CODEC.fieldOf("proportion")
        ).forGetter(TeamMembersRewardType::who),
        Codec.BOOL.optionalFieldOf("random", false).forGetter(TeamMembersRewardType::random)
    ).apply(instance, TeamMembersRewardType::new));

    @Override
    public void reward(LootContext context, float multiplier) {
        ITeam<?> team = context.getParamOrNull(PetrolparkLootContextParams.TEAM);
        if (team == null) return;
        int count = who.map(absoluteCount -> 
                Mth.clamp(absoluteCount.getInt(context), 0, team.memberCount()),
            proportion -> 
                (int)((Mth.clamp(proportion.getFloat(context), 0f, 1f) * team.memberCount()))
        );
        if (count == 0) return;
        List<Player> members = team.streamMembers(context.getLevel()).collect(Collectors.toList());
        if (count < team.memberCount() && random) Collections.shuffle(members);
        for (int i = 0; i < count && i < members.size(); i++) reward.reward(members.get(i), context, multiplier);
    };

    @Override
    public void render(GuiGraphics graphics) {
        reward.render(graphics);
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
    
};
