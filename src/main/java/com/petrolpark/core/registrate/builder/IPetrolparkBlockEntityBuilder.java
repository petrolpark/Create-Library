package com.petrolpark.core.registrate.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.OneTimeEventReceiver;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public interface IPetrolparkBlockEntityBuilder<T extends BlockEntity, P, S> {
  
    S self();

    AbstractRegistrate<?> getOwner();

    BlockEntityType<T> getEntry();

    public default S registerItemCapability(ICapabilityProvider<T, Direction, IItemHandler> provider) {
        return registerCapability(Capabilities.ItemHandler.BLOCK, provider);
    };

    public default S registerFluidCapability(ICapabilityProvider<T, Direction, IFluidHandler> provider) {
        return registerCapability(Capabilities.FluidHandler.BLOCK, provider);
    };

    public default S registerEnergyCapability(ICapabilityProvider<T, Direction, IEnergyStorage> provider) {
        return registerCapability(Capabilities.EnergyStorage.BLOCK, provider);
    };

    public default <CAP, CTX> S registerCapability(BlockCapability<CAP, CTX> capability, ICapabilityProvider<T, CTX, CAP> provider) {
        OneTimeEventReceiver.addModListener(getOwner(), RegisterCapabilitiesEvent.class, event -> event.registerBlockEntity(capability, getEntry(), provider));
        return self();
    };
    
};
