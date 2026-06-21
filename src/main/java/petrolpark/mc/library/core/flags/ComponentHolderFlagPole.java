package petrolpark.mc.library.core.flags;

import java.util.ArrayList;

import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

public abstract class ComponentHolderFlagPole<OBJECT, OBJECT_STACK extends MutableDataComponentHolder> extends AbstractFlagPole<OBJECT, OBJECT_STACK> {

    protected ComponentHolderFlagPole(OBJECT_STACK stack) {
        super(stack);
        orphanFlags.addAll(stack.getOrDefault(PetrolparkDataComponentTypes.ORPHAN_FLAGS, new ArrayList<Holder<Flag>>()).stream()
            .dropWhile(this::isIntrinsic)
            .toList()
        );
        for (Holder<Flag> flag : orphanFlags) {
            flags.add(flag);
            flags.addAll(flag.value().getChildren());
        };
    };

    @Override
    public void save() {
        stack.set(PetrolparkDataComponentTypes.ORPHAN_FLAGS, getOrphanHolderList());
    };
    
};
