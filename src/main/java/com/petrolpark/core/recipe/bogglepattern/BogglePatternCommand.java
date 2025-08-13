package com.petrolpark.core.recipe.bogglepattern;

import com.mojang.brigadier.CommandDispatcher;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;

public class BogglePatternCommand {
  
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal(Petrolpark.MOD_ID).then(Commands.literal("bogglepattern")
            .requires(source -> {
                return source.hasPermission(2);
            }).then(Commands.literal("list")
                .executes(ctx -> list(ctx.getSource()))
            ).then(Commands.literal("show")
                .then(Commands.argument("bogglepattern", ResourceArgument.resource(context, PetrolparkRegistries.Keys.BOGGLE_PATTERN))
                    .executes(ctx -> show(ctx.getSource(), ResourceArgument.getResource(ctx, "bogglepattern", PetrolparkRegistries.Keys.BOGGLE_PATTERN)))
                )
            ).then(Commands.literal("regenerate")
                .then(Commands.argument("bogglepattern", ResourceArgument.resource(context, PetrolparkRegistries.Keys.BOGGLE_PATTERN))
                    .executes(ctx -> regenerate(ctx.getSource(), ResourceArgument.getResource(ctx, "bogglepattern", PetrolparkRegistries.Keys.BOGGLE_PATTERN)))
                )
            )
        ));
    };

    private static int list(CommandSourceStack source) {
        Registry<BogglePattern> registry = source.registryAccess().registry(PetrolparkRegistries.Keys.BOGGLE_PATTERN).get();
        source.sendSuccess(() -> Component.translatable("commands.petrolpark.bogglepattern.list", registry.size(), ComponentUtils.formatList(registry.keySet().stream().map(ResourceLocation::toString).toList())), false);
        return registry.size();
    };

    private static int show(CommandSourceStack source, Holder<BogglePattern> bogglePattern) {
        int pattern = bogglePattern.value().getOrGeneratePattern(source.getLevel());
        source.sendSuccess(() -> Component.translatable("commands.petrolpark.bogglepattern.show", bogglePattern.getKey().location().toString()), false);
        for (String line : BogglePatternHelper.format(pattern)) {
            source.sendSuccess(() -> Component.literal(line), false);
        };
        return pattern;
    };

    private static int regenerate(CommandSourceStack source, Holder<BogglePattern> bogglePattern) {
        bogglePattern.value().forgetPattern();
        return show(source, bogglePattern);
    };
};
