package petrolpark.mc.library.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public interface ITickableCategory {
    
    public static final List<ITickableCategory> TICKING_CATEGORIES = new ArrayList<>();

    public static class ClientEvents {
        
        @SubscribeEvent
        public static void tickAll(ClientTickEvent.Pre event) {
            TICKING_CATEGORIES.forEach(ITickableCategory::tick);
        };
    };

    void tick();
};
