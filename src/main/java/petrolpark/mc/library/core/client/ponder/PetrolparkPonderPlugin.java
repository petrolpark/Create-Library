package petrolpark.mc.library.core.client.ponder;

import javax.annotation.Nonnull;

import petrolpark.mc.library.Petrolpark;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class PetrolparkPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return Petrolpark.MOD_ID;
    };

    @Override
    public void registerScenes(@Nonnull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PetrolparkPonderScenes.register(helper);
    };

    @Override
    public void registerTags(@Nonnull PonderTagRegistrationHelper<ResourceLocation> helper) {
        
    };
    
};
