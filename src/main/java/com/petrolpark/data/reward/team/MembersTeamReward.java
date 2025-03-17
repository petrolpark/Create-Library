package com.petrolpark.data.reward.team;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.data.reward.entity.IEntityReward;
import com.petrolpark.team.ITeam;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * Rewards a proportion of members of a {@link ITeam} with an {@link IEntityReward}.
 */
public record MembersTeamReward(IEntityReward reward, Either<NumberProvider, NumberProvider> who, boolean random) implements ITeamReward {

    public static final MapCodec<MembersTeamReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        IEntityReward.CODEC.fieldOf("reward").forGetter(MembersTeamReward::reward),
        Codec.mapEither(
            NumberProviders.CODEC.fieldOf("count"),
            NumberProviders.CODEC.optionalFieldOf("proportion", ConstantValue.exactly(1f))
        ).forGetter(MembersTeamReward::who),
        Codec.BOOL.optionalFieldOf("random", false).forGetter(MembersTeamReward::random)
    ).apply(instance, MembersTeamReward::new));

    public static final Codec<MembersTeamReward> INLINE_CODEC = IEntityReward.CODEC.xmap(entityReward -> new MembersTeamReward(entityReward, Either.right(ConstantValue.exactly(1f)), false), MembersTeamReward::reward);

    @Override
    public void reward(ITeam team, LootContext context, float multiplier) {
        int count = who.map(absoluteCount -> 
                Mth.clamp(absoluteCount.getInt(context), 0, team.memberCount()),
            proportion -> 
                (int)((Mth.clamp(proportion.getFloat(context), 0f, 1f) * team.memberCount()))
        );
        if (count == 0) return;
        List<Player> members = team.streamMembers().collect(Collectors.toList());
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
    public TeamRewardType getType() {
        return PetrolparkRewardTypes.MEMBERS.get();
    };
    
};
