package petrolpark.mc.library.util;

import java.util.List;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {

    public static final <T> T cycle(List<T> list) {
        return cycle(list, 20);
    };
    
    public static final <T> T cycle(List<T> list, int cycleLength) {
        if (list.isEmpty()) return null;
        if (list.size() == 1) return list.get(0);
        return list.get((AnimationTickHolder.getTicks(true) / cycleLength) % list.size());
    };
};
