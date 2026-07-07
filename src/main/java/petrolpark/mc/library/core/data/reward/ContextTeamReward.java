package petrolpark.mc.library.core.data.reward;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.core.data.reward.team.ITeamReward;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

@ParametersAreNonnullByDefault
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
    public void validate(ValidationContext context) {
        IReward.super.validate(context);
        reward().validate(context.forChild(".team_reward"));
    };
    
};
