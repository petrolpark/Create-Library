package petrolpark.mc.library.shared.world.item.crafting.drying.rack;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.item.decay.ItemDecay;

import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class DryingRackJadeBlockComponentProvider implements IBlockComponentProvider {

    public static final ResourceLocation UID = Petrolpark.asResource("drying_rack");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof DryingRackBlockEntity rack) ItemDecay.getTooltip(rack.inv.getStackInSlot(0)).ifPresent(tooltip::add);
    };

    @Override
    public ResourceLocation getUid() {
        return UID;
    };
    
};
