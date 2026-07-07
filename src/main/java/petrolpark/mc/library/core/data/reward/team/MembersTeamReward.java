package petrolpark.mc.library.core.data.reward.team;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

/**
 * Rewards a proportion of members of a {@link ITeam} with an {@link IEntityReward}.
 */
@ParametersAreNonnullByDefault
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
    public void addToDescription(IndentedTooltipBuilder builder) {
        builder.add(who.map(
            count -> translate("count", NumberEstimate.get(count).getIntComponent()),
            proportion -> NumberEstimate.get(proportion).min() == 1f ? translate("all") : translate("percentage", NumberEstimate.get(proportion).multiply(100f).getIntComponent())
        ));
        builder.indent();
        reward().addToDescription(builder);
        builder.unindent();
    };

    @Override
    public TeamRewardType getType() {
        return PetrolparkRewardTypes.MEMBERS.get();
    };

    @Override
    public void validate(ValidationContext context) {
        ITeamReward.super.validate(context);
        reward().validate(context.forChild(".member_reward"));
    };
    
};
