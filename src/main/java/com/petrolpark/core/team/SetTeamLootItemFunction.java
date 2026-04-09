package com.petrolpark.core.team;

import java.util.Collections;
import java.util.Set;

import com.petrolpark.PetrolparkDataComponentTypes;
import com.petrolpark.PetrolparkLootContextParams;
import com.petrolpark.PetrolparkLootItemFunctions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

/**
 * Set the {@link ITeam} {@link PetrolparkDataComponentTypes#TEAM_PROVIDER Component} of ItemStacks to the {@link PetrolparkLootContextParams#TEAM context Team}.
 * No arguments.
 * @author petrolpark
 */
public final class SetTeamLootItemFunction implements LootItemFunction {

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        stack.set(PetrolparkDataComponentTypes.TEAM_PROVIDER, context.getParam(PetrolparkLootContextParams.TEAM).getProvider());
        return stack;
    };

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Collections.singleton(PetrolparkLootContextParams.TEAM);
    };

    @Override
    public LootItemFunctionType<SetTeamLootItemFunction> getType() {
        return PetrolparkLootItemFunctions.SET_TEAM.get();
    };
    
};