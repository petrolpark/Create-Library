package com.petrolpark.core.data.rtg;

import com.petrolpark.Petrolpark;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = Petrolpark.MOD_ID)
public class RTGPackSource {

    // public static final LevelStorageSource.LevelStorageAccess getLevelStorageAccess() {
    //     return Petrolpark.runForDist(() -> ()-> Minecraft.getInstance().level, null);
    // };
    
    @SubscribeEvent
    public static final void onAddPackFinders(AddPackFindersEvent event) {
        // if (event.getPackType() == PackType.SERVER_DATA) {
        //     final LevelStorageAccess levelStorageAccess = ServerLifecycleHooks.getCurrentServer().storageSource;
        //     event.addRepositorySource(new FolderRepositorySource(levelStorageAccess.getLevelPath(LevelResource.GENERATED_DIR).resolve("datapacks"), PackType.SERVER_DATA, PackSource.WORLD, levelStorageAccess.parent().getWorldDirValidator()));
        // };
            
    };
};
