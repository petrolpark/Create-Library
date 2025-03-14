package com.petrolpark.data.reward.entity;

import com.mojang.serialization.MapCodec;
import com.petrolpark.data.reward.PetrolparkRewardTypes;
import com.petrolpark.util.NetworkHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record GrantExperienceEntityReward(NumberProvider amount) implements IEntityReward {

    public static final MapCodec<GrantExperienceEntityReward> CODEC = NetworkHelper.singleFieldMapCodec(NumberProviders.CODEC, "amount", GrantExperienceEntityReward::amount, GrantExperienceEntityReward::new);

    @Override
    public void reward(Entity entity, LootContext context, float multiplier) {
        if (entity instanceof Player player) player.giveExperiencePoints(amount.getInt(context));
    };

    @Override
    public void render(GuiGraphics graphics) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    };

    @Override
    public Component getName() {
        return Component.translatable("reward.petrolpark.xp");
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.GRANT_EXPERIENCE.get();
    };
    
};
