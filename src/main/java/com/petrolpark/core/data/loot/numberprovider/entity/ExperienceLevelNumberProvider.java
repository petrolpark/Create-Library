package com.petrolpark.core.data.loot.numberprovider.entity;

import com.petrolpark.PetrolparkNumberProviderTypes;
import com.petrolpark.core.data.loot.numberprovider.NumberEstimate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;

public class ExperienceLevelNumberProvider implements EntityNumberProvider {

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        if (entity instanceof Player player) return player.experienceLevel;
        return 0f;
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.ranged(0f, 100f, true);
    };

    @Override
    public LootEntityNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.EXPERIENCE_LEVEL.get();
    };
    
};
