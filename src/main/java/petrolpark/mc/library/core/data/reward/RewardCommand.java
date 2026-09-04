package petrolpark.mc.library.core.data.reward;

import java.util.Collection;
import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.registry.PetrolparkLootContextParamSets;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.ItemHelper;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;

public class RewardCommand {

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_REWARDS = (context, builder) -> {
        final ReloadableServerRegistries.Holder registries = context.getSource().getServer().reloadableRegistries();
        return SharedSuggestionProvider.suggestResource(registries.getKeys(PetrolparkRegistries.Keys.REWARD), builder);
    };

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_ENTITY_REWARDS = (context, builder) -> {
        final ReloadableServerRegistries.Holder registries = context.getSource().getServer().reloadableRegistries();
        return SharedSuggestionProvider.suggestResource(registries.getKeys(PetrolparkRegistries.Keys.ENTITY_REWARD), builder);
    };
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
            Commands.literal(Petrolpark.MOD_ID).then(
                Commands.literal("reward").requires(source -> source.hasPermission(2)).then(
                    Commands.argument("reward", new RewardArgument(context)).suggests(SUGGEST_REWARDS).then(
                        Commands.argument("multiplier", FloatArgumentType.floatArg()).executes(ctx ->
                            reward(ctx.getSource(), RewardArgument.getReward(ctx, "reward"), FloatArgumentType.getFloat(ctx, "multiplier"))
                        )
                    ).executes(ctx ->
                        reward(ctx.getSource(), RewardArgument.getReward(ctx, "reward"), 1f)
                    )
                )
                .then(
                    Commands.literal("entity").then(
                        Commands.argument("targets", EntityArgument.entities()).then(
                            Commands.argument("reward", new EntityRewardArgument(context)).suggests(SUGGEST_ENTITY_REWARDS).then(
                                Commands.argument("multiplier", FloatArgumentType.floatArg()).executes(ctx -> 
                                    rewardEntities(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), EntityRewardArgument.getReward(ctx, "reward"), FloatArgumentType.getFloat(ctx, "multiplier"))
                                )
                            ).executes(ctx -> 
                                rewardEntities(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), EntityRewardArgument.getReward(ctx, "reward"), 1f)
                            )
                        )
                    )
                )
            )
        );
    };

    private static int reward(CommandSourceStack source, Holder<IReward> rewardHolder, float multiplier) {
        final LootParams.Builder lootParamsBuilder = new LootParams.Builder(source.getLevel())
            .withParameter(LootContextParams.ORIGIN, source.getPosition())
            .withOptionalParameter(LootContextParams.THIS_ENTITY, source.getEntity())
            .withOptionalParameter(PetrolparkLootContextParams.ITEM_HANDLER, ItemHelper.wrap(source.getEntity()));
        if (rewardHolder.value().reward(new LootContext.Builder(lootParamsBuilder.create(PetrolparkLootContextParamSets.REWARD_COMMAND)).create(Optional.empty()), multiplier, false)) {
            final IndentedTooltipBuilder.OneLine tooltipBuilder = new IndentedTooltipBuilder.OneLine();
            rewardHolder.value().info().addToDescription(tooltipBuilder);
            source.sendSuccess(() -> Component.translatable("commands.petrolpark.reward.success", tooltipBuilder.buildSingle()), true);
            return 1;
        } else {
            source.sendFailure(Component.translatable("commands.petrolpark.reward.failed"));
            return 0;
        }
    };

    private static int rewardEntities(CommandSourceStack source, Collection<? extends Entity> targets, Holder<IEntityReward> rewardHolder, float multiplier) {
        int succesfull = 0;
        for (Entity target : targets) {
            final LootParams.Builder lootParamsBuilder = new LootParams.Builder(source.getLevel())
                .withParameter(LootContextParams.ORIGIN, source.getPosition())
                .withOptionalParameter(LootContextParams.THIS_ENTITY, target)
                .withOptionalParameter(PetrolparkLootContextParams.ITEM_HANDLER, ItemHelper.wrap(target));
            if (rewardHolder.value().reward(target, new LootContext.Builder(lootParamsBuilder.create(PetrolparkLootContextParamSets.REWARD_COMMAND)).create(Optional.empty()), multiplier, false))
                succesfull++;
        };
        if (succesfull > 0) {
            final IndentedTooltipBuilder.OneLine tooltipBuilder = new IndentedTooltipBuilder.OneLine();
            rewardHolder.value().info().addToDescription(tooltipBuilder);
            final int succesfullFinal = succesfull;
            source.sendSuccess(() -> Component.translatable("commands.petrolpark.reward.entity.success", succesfullFinal, tooltipBuilder.buildSingle()), true);
            return 1;
        } else {
            source.sendFailure(Component.translatable("commands.petrolpark.reward.failed"));
            return 0;
        }
    };

    public static class RewardArgument extends ResourceOrIdArgument<IReward> {

        public RewardArgument(CommandBuildContext registryLookup) {
            super(registryLookup, PetrolparkRegistries.Keys.REWARD, IReward.CODEC);
        };

        @SuppressWarnings("unchecked")
        public static Holder<IReward> getReward(CommandContext<CommandSourceStack> context, String name) {
            return context.getArgument(name, Holder.class);
        };

    };

    public static class EntityRewardArgument extends ResourceOrIdArgument<IEntityReward> {

        public EntityRewardArgument(CommandBuildContext registryLookup) {
            super(registryLookup, PetrolparkRegistries.Keys.ENTITY_REWARD, IEntityReward.CODEC);
        };
        
        @SuppressWarnings("unchecked")
        public static Holder<IEntityReward> getReward(CommandContext<CommandSourceStack> context, String name) {
            return context.getArgument(name, Holder.class);
        };
    };
};
