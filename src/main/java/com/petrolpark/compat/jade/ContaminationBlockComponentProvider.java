package com.petrolpark.compat.jade;

import com.petrolpark.Petrolpark;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.Mods;
import com.petrolpark.compat.create.core.block.entity.behaviour.ContaminationBehaviour;
import com.petrolpark.core.contamination.Contaminant;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class ContaminationBlockComponentProvider implements IBlockComponentProvider {

    public static final ResourceLocation UID = Petrolpark.asResource("contamination");

    @Override
    public ResourceLocation getUid() {
        return UID;
    };

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        if (Mods.CREATE.isLoaded()) appendCreateTooltip(tooltip, blockAccessor);
    };

    @RequiresCreate
    private void appendCreateTooltip(ITooltip tooltip, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof SmartBlockEntity sbe) {
            ContaminationBehaviour behaviour = sbe.getBehaviour(ContaminationBehaviour.TYPE);
            if (behaviour == null) return;
            behaviour.getContamination().streamShownContaminants().map(Contaminant::getNameColored).forEach(tooltip::add);
            behaviour.getContamination().streamShownAbsentContaminants().map(Contaminant::getAbsentNameColored).forEach(tooltip::add);
        };
    };
    
};
