package com.petrolpark.core.registrate.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.petrolpark.AbstractPetrolparkRegistrate;
import com.petrolpark.compat.Mods;
import com.petrolpark.compat.create.PetrolparkCreate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.createmod.catnip.data.Couple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

/**
 * {@link ItemBuilder} without any default datagen
 */
public class PetrolparkItemBuilder<T extends Item, P> extends ItemBuilder<T, P> {

    public static <T extends Item, P> PetrolparkItemBuilder<T, P> create(AbstractPetrolparkRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<Item.Properties, T> factory) {
        return new PetrolparkItemBuilder<>(owner, parent, name, callback, factory);
    };

    public PetrolparkItemBuilder(AbstractPetrolparkRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory) {
        super(owner, parent, name, callback, factory);
    };

    public PetrolparkItemBuilder<T, P> fluidCapability(ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> provider) {
        return capability(Capabilities.FluidHandler.ITEM, provider);
    };

    public PetrolparkItemBuilder<T, P> fluidCapability(NonNullFunction<T, ICapabilityProvider<ItemStack, Void, IFluidHandlerItem>> provider) {
        return capability(Capabilities.FluidHandler.ITEM, provider);
    };

    public <CAP, CTX> PetrolparkItemBuilder<T, P> capability(ItemCapability<CAP, CTX> capability, ICapabilityProvider<ItemStack, CTX, CAP> provider) {
        return capability(capability, $ -> provider);
    };

    public <CAP, CTX> PetrolparkItemBuilder<T, P> capability(ItemCapability<CAP, CTX> capability, NonNullFunction<T, ICapabilityProvider<ItemStack, CTX, CAP>> provider) {
        OneTimeEventReceiver.addModListener(getOwner(), RegisterCapabilitiesEvent.class, event -> event.registerItem(capability, provider.apply(getEntry()), getEntry()));
        return this;
    };

    public PetrolparkItemBuilder<T, P> tooltip(String summary, UnaryOperator<TooltipBuilder> builder) {
        final TooltipBuilder tooltip = builder.apply(new TooltipBuilder());
        getOwner().addDataGenerator(ProviderType.LANG, prov -> {
            final String id = getEntry().getDescriptionId() + ".tooltip.";
            prov.add(id + "summary", summary);
            int i = 1;
            for (Couple<String> behaviour : tooltip.behaviours) {
                prov.add(id + "condition" + i, behaviour.getFirst());
                prov.add(id + "behaviour" + i, behaviour.getSecond());
                i++;
            };
        });
        if (Mods.CREATE.isLoaded()) onRegister(PetrolparkCreate::registerTooltip);
        return this;
    };

    public class TooltipBuilder {

        protected List<Couple<String>> behaviours = new ArrayList<>();

        public TooltipBuilder behaviour(String condition, String behaviour) {
            behaviours.add(Couple.create(condition, behaviour));
            return this;
        };
    };
    
};
