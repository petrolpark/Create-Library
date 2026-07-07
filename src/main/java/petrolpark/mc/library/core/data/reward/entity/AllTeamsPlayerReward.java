package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.reward.team.ITeamReward;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

/**
 * <p>{@code petrolpark:}</p>
 * Issue a {@link ITeamReward} to all {@link ITeam}s of which the Player is a part.
 * 
 * Arguments:
 * <ul>
 * <li> {@code reward} - {@link ITeamReward} to award to all {@link ITeam}s
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record AllTeamsPlayerReward(ITeamReward reward) implements IPlayerReward {

    public static final MapCodec<AllTeamsPlayerReward> CODEC = CodecHelper.singleFieldMap(ITeamReward.CODEC, "reward", AllTeamsPlayerReward::reward, AllTeamsPlayerReward::new);

    @Override
    public void rewardPlayer(Player player, LootContext context, float multiplier) {
        ITeam.streamAll(player).forEach(team -> reward().reward(team, context, multiplier));
    };

    @Override
    public void render(GuiGraphics graphics) {
        reward().render(graphics);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        builder.add(translateSimple())
            .indent();
        reward().addToDescription(builder);
        builder.unindent();
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.ALL_TEAMS.get();
    };

    @Override
    public void validate(ValidationContext context) {
        IPlayerReward.super.validate(context);
        reward().validate(context.forChild(".team_reward"));
    };
    
};
