package com.petrolpark.compat.create;

import com.petrolpark.compat.create.core.event.CreateEvents;
import com.petrolpark.compat.create.core.event.CreateModEvents;
import com.petrolpark.compat.create.core.world.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.petrolpark.compat.create.registry.PetrolparkArmInteractionPointTypes;
import com.petrolpark.compat.create.registry.PetrolparkCreateAdvancedIngredientTypes;
import com.petrolpark.compat.create.registry.PetrolparkCreateAttachmentTypes;
import com.petrolpark.compat.create.registry.PetrolparkCreateCompatRecipeDeserializers;
import com.petrolpark.compat.create.registry.PetrolparkCreateDataComponentTypes;
import com.petrolpark.compat.create.registry.PetrolparkCreateDoughTypes;
import com.petrolpark.compat.create.registry.PetrolparkCreateGlobalLootModifierSerializers;
import com.petrolpark.compat.create.registry.PetrolparkCreateRegistrateProviderTypes;
import com.petrolpark.compat.create.registry.PetrolparkCreateRegistries;
import com.petrolpark.compat.create.registry.PetrolparkDoughIngredientTypes;
import com.petrolpark.compat.create.registry.PetrolparkDoughToppingTypes;
import com.petrolpark.compat.create.registry.PetrolparkItemAttributeTypes;
import com.petrolpark.compat.create.registry.PetrolparkMovementChecks;
import com.petrolpark.compat.create.registry.PetrolparkOpenPipeEffectHandlers;
import com.petrolpark.compat.create.shared.content.processing.extrusion.ExtrusionRecipe;
import com.petrolpark.compat.create.shared.registry.SharedCreateItems;
import com.petrolpark.compat.create.shared.registry.SharedCreateBlocks;
import com.petrolpark.compat.create.shared.registry.SharedCreateCriterionTriggers;
import com.petrolpark.compat.create.shared.registry.SharedCreateFluids;
import com.petrolpark.compat.create.shared.registry.SharedCreateMenuTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreateRecipeTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreatePackets;
import com.petrolpark.compat.create.shared.registry.PetrolparkMandrelAnimationTypes;
import com.petrolpark.compat.create.shared.registry.SharedContraptionTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreateBlockEntityTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreateEntityTypes;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;

@RequiresCreate
public class PetrolparkCreate {

    public static final PetrolparkCreateRegistrate REGISTRATE = new PetrolparkCreateRegistrate();

    public static final ExtrusionRecipe.MovementBehaviourProvider EXTRUSION_MOVEMENT_BEHAVIOUR_PROVIDER = new ExtrusionRecipe.MovementBehaviourProvider();

    static {
        SharedCreateRecipeTypes.init();
    };
  
    public static void ctor(IEventBus modEventBus, IEventBus mainEventBus) {

        PetrolparkCreateRegistries.init();

        REGISTRATE.registerEventListeners(modEventBus);

        // Registrations
        PetrolparkCreateAdvancedIngredientTypes.register();
        PetrolparkCreateAttachmentTypes.register(modEventBus);
        SharedCreateBlockEntityTypes.register();
        SharedCreateBlocks.register();
        PetrolparkCreateCompatRecipeDeserializers.register();
        SharedContraptionTypes.register();
        SharedCreateCriterionTriggers.register();
        PetrolparkCreateDataComponentTypes.register(modEventBus);
        PetrolparkCreateDoughTypes.register();
        SharedCreateEntityTypes.register();
        SharedCreateFluids.register();
        PetrolparkCreateGlobalLootModifierSerializers.register();
        SharedCreateItems.register();
        SharedCreateMenuTypes.register();
        SharedCreatePackets.register();
        PetrolparkCreateRegistrateProviderTypes.register();
        PetrolparkDoughIngredientTypes.register();
        PetrolparkDoughToppingTypes.register();
    
        PetrolparkArmInteractionPointTypes.register();
        PetrolparkMandrelAnimationTypes.register();
        PetrolparkMovementChecks.register();
        PetrolparkOpenPipeEffectHandlers.register();

        // Event Bus Subscribers
        modEventBus.addListener(PetrolparkCreate::onRegister);
        NeoForge.EVENT_BUS.register(CreateEvents.class);
        modEventBus.register(CreateModEvents.class);
        mainEventBus.register(AbstractRememberPlacerBehaviour.class);
        mainEventBus.register(EXTRUSION_MOVEMENT_BEHAVIOUR_PROVIDER);
    };

    public static final void init(final FMLCommonSetupEvent event) {
        
    };

    private static final void onRegister(final RegisterEvent event) {
        PetrolparkItemAttributeTypes.init();
    };

    public static final void registerTooltip(Item item) {
        final TooltipModifier modifier = new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
			.andThen(TooltipModifier.mapNull(KineticStats.create(item)));
	    TooltipModifier.REGISTRY.register(item, modifier);
    };
};
