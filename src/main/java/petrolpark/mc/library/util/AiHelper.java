package petrolpark.mc.library.util;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import petrolpark.mc.library.registry.PetrolparkDataMapTypes;

@EventBusSubscriber
public class AiHelper {
  
    public static void releasePoi(ServerLevel level, LivingEntity entity, MemoryModuleType<GlobalPos> memoryModuleType) {
        entity.getBrain().getMemory(memoryModuleType).ifPresent(globalPos -> {
            final ServerLevel poiLevel = level.getServer().getLevel(globalPos.dimension());
            if (poiLevel == null) return;
            final PoiManager poiManager = poiLevel.getPoiManager();
            poiManager.getType(globalPos.pos()).ifPresent(poiTypeHolder -> {
                if (BuiltInRegistries.MEMORY_MODULE_TYPE.getResourceKey(memoryModuleType)
                    .map(key -> BuiltInRegistries.MEMORY_MODULE_TYPE.getData(PetrolparkDataMapTypes.MEMORY_POI_RELEASERS, key))
                    .stream()
                    .flatMap(HolderSet::stream)
                    .filter(poiTypeHolder::equals)
                    .findAny()
                    .isPresent()
                ) poiManager.release(globalPos.pos());
            });
        });
    };

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) return;
        BuiltInRegistries.MEMORY_MODULE_TYPE.getDataMap(PetrolparkDataMapTypes.MEMORY_POI_RELEASERS).keySet().forEach(key -> {
            final MemoryModuleType<?> memoryType = BuiltInRegistries.MEMORY_MODULE_TYPE.get(key);
            if (memoryType == null) return;
            if (!event.getEntity().getBrain().checkMemory(memoryType, MemoryStatus.REGISTERED)) return;
            if (!(event.getEntity().getBrain().getMemory(memoryType).orElse(null) instanceof GlobalPos)) return;
            releasePoi(level, event.getEntity(), (MemoryModuleType<GlobalPos>)memoryType);
        });
    };
};
