package com.petrolpark.compat.create;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.common.processing.extrusion.ExtrusionRecipe;
import com.petrolpark.compat.create.core.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.petrolpark.compat.create.event.CreateEvents;
import com.petrolpark.compat.create.event.CreateModEvents;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;

@RequiresCreate
public class PetrolparkCreate {

    public static final ExtrusionRecipe.MovementBehaviourProvider EXTRUSION_MOVEMENT_BEHAVIOUR_PROVIDER = new ExtrusionRecipe.MovementBehaviourProvider();

    static {
        PetrolparkCreateRecipeTypes.init();
    };
  
    public static void ctor(IEventBus modEventBus, IEventBus mainEventBus) {

        PetrolparkCreateRegistries.init();

        // Registrations
        PetrolparkCreateAdvancedIngredientTypes.register();
        PetrolparkCreateAttachmentTypes.register(modEventBus);
        PetrolparkCreateBlockEntityTypes.register();
        PetrolparkCreateBlocks.register();
        PetrolparkCreateCompatRecipeDeserializers.register();
        PetrolparkCreateCriterionTriggers.register();
        PetrolparkCreateDataComponentTypes.register(modEventBus);
        PetrolparkCreateDoughTypes.register();
        PetrolparkCreateFluids.register();
        PetrolparkCreateGlobalLootModifierSerializers.register();
        PetrolparkCreateItems.register();
        PetrolparkCreateMenuTypes.register();
        PetrolparkCreatePackets.register();
    
        PetrolparkArmInteractionPointTypes.register();
        PetrolparkMandrelAnimationTypes.register();
        PetrolparkMovementChecks.register();

        // Event Bus Subscribers
        modEventBus.addListener(PetrolparkCreate::onRegister);
        NeoForge.EVENT_BUS.register(CreateEvents.class);
        modEventBus.register(CreateModEvents.class);
        mainEventBus.register(AbstractRememberPlacerBehaviour.class);
        mainEventBus.register(EXTRUSION_MOVEMENT_BEHAVIOUR_PROVIDER);
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
