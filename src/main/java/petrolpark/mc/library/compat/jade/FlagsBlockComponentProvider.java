package petrolpark.mc.library.compat.jade;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.core.world.block.entity.behaviour.FlagPoleBehaviour;
import petrolpark.mc.library.core.flags.Flag;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class FlagsBlockComponentProvider implements IBlockComponentProvider {

    public static final ResourceLocation UID = Petrolpark.asResource("flags");

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
            FlagPoleBehaviour behaviour = sbe.getBehaviour(FlagPoleBehaviour.TYPE);
            if (behaviour == null) return;
            behaviour.getFlagPole().streamShownFlags().map(Flag::getNameColored).forEach(tooltip::add);
            behaviour.getFlagPole().streamShownAbsentFlags().map(Flag::getAbsentNameColored).forEach(tooltip::add);
        };
    };
    
};
