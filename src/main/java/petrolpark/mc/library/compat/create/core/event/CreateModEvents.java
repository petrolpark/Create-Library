package petrolpark.mc.library.compat.create.core.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import petrolpark.mc.library.compat.create.shared.content.processing.mandrel.MandrelBlockEntity;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateDataMapTypes;

public class CreateModEvents {
    
    @SubscribeEvent
    public static final void registerCapabilities(RegisterCapabilitiesEvent event) {
        MandrelBlockEntity.registerCapabilities(event);
    };

    @SubscribeEvent
    public static final void onRegisterDataMapTypes(RegisterDataMapTypesEvent event) {
        SharedCreateDataMapTypes.onRegisterDataMapTypes(event);
    };
};
