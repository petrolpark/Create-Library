package com.petrolpark.compat.jade;

import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class PetrolparkJade implements IWailaPlugin {
    
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new ContaminationBlockComponentProvider(), Block.class);
        registration.addItemModNameCallback(new SharedFeatureItemModNameCallback());
    };
};
