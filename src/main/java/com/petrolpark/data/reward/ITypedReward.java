package com.petrolpark.data.reward;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ITypedReward<TYPE> extends LootContextUser {

    public TYPE getType();
    
    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics);

    @OnlyIn(Dist.CLIENT)
    public Component getName();
};
