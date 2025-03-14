package com.petrolpark.data.reward.generator;

import java.util.Set;
import java.util.Collections;

import com.petrolpark.data.IEntityTarget;

import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public interface IContextEntityRewardGenerator extends IRewardGenerator {

    public IEntityTarget target();

    @Override
    public default Set<LootContextParam<?>> getReferencedContextParams() {
        return Collections.singleton(target().getReferencedParam());
    };
};
