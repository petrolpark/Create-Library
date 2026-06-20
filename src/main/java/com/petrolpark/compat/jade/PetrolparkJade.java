package com.petrolpark.compat.jade;

import com.petrolpark.shared.world.item.crafting.drying.rack.DryingRackBlock;
import com.petrolpark.shared.world.item.crafting.drying.rack.DryingRackJadeBlockComponentProvider;

import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class PetrolparkJade implements IWailaPlugin {
    
    @Override
    public void registerClient(IWailaClientRegistration registration) {

        // Blocks
        registration.registerBlockComponent(new FlagsBlockComponentProvider(), Block.class);
        registration.registerBlockComponent(new DryingRackJadeBlockComponentProvider(), DryingRackBlock.class);

        // Items
        registration.addItemModNameCallback(new SharedFeatureItemModNameCallback());
    };
};
