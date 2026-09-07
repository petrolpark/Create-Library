package petrolpark.mc.library.compat.create;

import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.createmod.catnip.lang.FontHelper;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import petrolpark.mc.library.compat.create.core.event.CreateEvents;
import petrolpark.mc.library.compat.create.core.event.CreateModEvents;
import petrolpark.mc.library.compat.create.core.world.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import petrolpark.mc.library.compat.create.registry.PetrolparkArmInteractionPointTypes;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateAdvancedIngredientTypes;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateAttachmentTypes;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateBlockEntityTypes;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateBlocks;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateCompatRecipeDeserializers;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateDataComponentTypes;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateGlobalLootModifierSerializers;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreatePackets;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRecipeTypes;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRegistrateProviderTypes;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRegistries;
import petrolpark.mc.library.compat.create.registry.PetrolparkItemAttributeTypes;
import petrolpark.mc.library.compat.create.registry.PetrolparkMovementChecks;
import petrolpark.mc.library.compat.create.registry.PetrolparkOpenPipeEffectHandlers;
import petrolpark.mc.library.compat.create.shared.SharedCreate;
import petrolpark.mc.library.compat.create.shared.content.processing.extrusion.ExtrusionRecipe;
import petrolpark.mc.library.compat.create.shared.registry.PetrolparkMandrelAnimationTypes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateRecipeTypes;

@RequiresCreate
public class PetrolparkCreate {

    public static final PetrolparkCreateRegistrate REGISTRATE = new PetrolparkCreateRegistrate();

    public static final ExtrusionRecipe.MovementBehaviourProvider EXTRUSION_MOVEMENT_BEHAVIOUR_PROVIDER = new ExtrusionRecipe.MovementBehaviourProvider();

    static {
        PetrolparkCreateRecipeTypes.init();
        SharedCreateRecipeTypes.init();
    };
  
    public static void ctor(IEventBus modEventBus, IEventBus mainEventBus) {

        PetrolparkCreateRegistries.init();

        REGISTRATE.registerEventListeners(modEventBus);

        SharedCreate.ctor(modEventBus, mainEventBus);

        // Registrations
        PetrolparkCreateAdvancedIngredientTypes.register();
        PetrolparkCreateAttachmentTypes.register(modEventBus);
        PetrolparkCreateBlockEntityTypes.register();
        PetrolparkCreateBlocks.register();
        PetrolparkCreateCompatRecipeDeserializers.register();
        PetrolparkCreateDataComponentTypes.register(modEventBus);
        PetrolparkCreateGlobalLootModifierSerializers.register();
        PetrolparkCreatePackets.register();
        PetrolparkCreateRegistrateProviderTypes.register();
    
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
