package com.petrolpark.core.data.reward.generator;

import java.util.Set;

import com.petrolpark.core.data.IEntityTarget;

import java.util.Collections;

import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public interface IContextEntityRewardGenerator extends IRewardGenerator {

    public IEntityTarget target();

    @Override
    public default Set<LootContextParam<?>> getReferencedContextParams() {
        return Collections.singleton(target().getReferencedParam());
    };
};
