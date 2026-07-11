package petrolpark.mc.library.core.data.reward.generator;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import petrolpark.mc.library.core.data.IEntityTarget;

public interface IContextEntityRewardGenerator extends IRewardGenerator {

    public IEntityTarget target();

    @Override
    public default Set<LootContextParam<?>> getReferencedContextParams() {
        return new HashSet<>(target().getReferencedParam());
    };
};
