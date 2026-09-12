package petrolpark.mc.library.shared.registry;

import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.shared.SharedFeatureFlag;

public class SharedMemoryModuleTypes {
    
    public static final RegistryEntry<MemoryModuleType<?>, MemoryModuleType<GlobalPos>>
    
    SEAT_POS = Petrolpark.REGISTRATE.sharedMemoryModuleType(SharedFeatureFlag.RESTAURANT_SEATING, "seat", GlobalPos.CODEC),
    RESTAURANT_SERVING_POS = Petrolpark.REGISTRATE.sharedMemoryModuleType(SharedFeatureFlag.RESTAURANT_SEATING, "restaurant_serving", GlobalPos.CODEC);

    public static final void register() {};
};
