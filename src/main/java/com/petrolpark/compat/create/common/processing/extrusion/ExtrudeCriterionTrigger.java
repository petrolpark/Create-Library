package com.petrolpark.compat.create.common.processing.extrusion;

import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.util.AdvancementHelper;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ExtrudeCriterionTrigger extends SimpleCriterionTrigger<ExtrudeCriterionTrigger.Instance> {
    
    public void trigger(ServerPlayer player, RecipeHolder<ExtrusionRecipe> recipeHolder, BlockState inputState, BlockState outputState) {
        trigger(player, instance -> instance.matches(recipeHolder, inputState, outputState));
    };

    public Consumer<ServerPlayer> trigger(RecipeHolder<ExtrusionRecipe> recipeHolder, BlockState inputState, BlockState outputState) {
        return player -> trigger(recipeHolder, inputState, outputState);
    };

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    };

    public static record Instance(
        Optional<ContextAwarePredicate> player,
        Optional<ResourceLocation> recipeId,
        Optional<HolderSet<Block>> inputBlock, Optional<StatePropertiesPredicate> inputState,
        Optional<HolderSet<Block>> outputBlock, Optional<StatePropertiesPredicate> outputState
    ) implements SimpleCriterionTrigger.SimpleInstance {
    
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ContextAwarePredicate.CODEC.optionalFieldOf("owner").forGetter(Instance::player),
            ResourceLocation.CODEC.optionalFieldOf("recipe").forGetter(Instance::recipeId),
            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("input_block").forGetter(Instance::inputBlock),
            StatePropertiesPredicate.CODEC.optionalFieldOf("input_state").forGetter(Instance::inputState),
            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("outut_block").forGetter(Instance::inputBlock),
            StatePropertiesPredicate.CODEC.optionalFieldOf("output_state").forGetter(Instance::inputState)
        ).apply(instance, Instance::new));

        boolean matches(RecipeHolder<ExtrusionRecipe> recipeHolder, BlockState inputState, BlockState outputState) {
            return AdvancementHelper.test(recipeId(), recipeHolder.id())
                && AdvancementHelper.testBlocks(inputBlock(), inputState) && AdvancementHelper.testState(inputState(), inputState)
                && AdvancementHelper.testBlocks(outputBlock(), outputState) && AdvancementHelper.testState(outputState(), outputState);
        };
    };
};
