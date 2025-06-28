package com.petrolpark.core.data.reward;

import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ITypedReward<TYPE extends INamedRewardType> extends LootContextUser {

    public TYPE getType();
    
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics);

    @OnlyIn(Dist.CLIENT)
    public void addToDescription(IndentedTooltipBuilder builder);

    default Component translate(String postfix, Object... args) {
        return Component.translatable(getType().translationKey() + "." + postfix, args);
    };

    default Component translateSimple(Object... args) {
        return Component.translatable(getType().translationKey(), args);
    };
};
