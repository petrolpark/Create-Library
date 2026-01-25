package com.petrolpark.compat.create;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionRecipe;
import com.petrolpark.compat.create.core.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.petrolpark.compat.create.event.CreateEvents;
import com.petrolpark.compat.create.event.CreateModEvents;
import com.petrolpark.config.PetrolparkStressConfig;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;

@RequiresCreate
public class Create {

    public static final ExtrusionRecipe.MovementBehaviourProvider EXTRUSION_MOVEMENT_BEHAVIOUR_PROVIDER = new ExtrusionRecipe.MovementBehaviourProvider();

    static {
        CreateRecipeTypes.init();
    };
  
    public static void ctor(IEventBus modEventBus, IEventBus mainEventBus) {

        CreateRegistries.init();

        // Registrations
        CreateAdvancedIngredientTypes.register();
        CreateAttachmentTypes.register(modEventBus);
        CreateBlockEntityTypes.register();
        CreateBlocks.register();
        CreateCompatRecipeDeserializers.register();
        CreateCriterionTriggers.register();
        CreateDataComponentTypes.register(modEventBus);
        CreateFluids.register();
        CreateGlobalLootModifierSerializers.register();
        CreateItems.register();
        CreateMenuTypes.register();
        CreatePackets.register();
    
        PetrolparkArmInteractionPointTypes.register();
        PetrolparkMandrelAnimationTypes.register();
        PetrolparkMovementChecks.register();

        // Event Bus Subscribers
        modEventBus.addListener(Create::onRegister);
        NeoForge.EVENT_BUS.register(CreateEvents.class);
        modEventBus.register(CreateModEvents.class);
        mainEventBus.register(PetrolparkStressConfig.class);
        mainEventBus.register(AbstractRememberPlacerBehaviour.class);
        mainEventBus.register(EXTRUSION_MOVEMENT_BEHAVIOUR_PROVIDER);
    };

    private static final void onRegister(final RegisterEvent event) {
        PetrolparkItemAttributeTypes.init();
    };
};
