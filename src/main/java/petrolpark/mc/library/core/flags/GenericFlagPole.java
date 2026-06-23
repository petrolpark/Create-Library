package petrolpark.mc.library.core.flags;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;

/**
 * A {@link AbstractFlagPole} not tied to any specific object. When these are used, the developer will have to manage loading and saving them themselves.
 */
public class GenericFlagPole extends AbstractFlagPole<Object, Object> {

    private final Runnable onSave;

    public GenericFlagPole() {
        this(() -> {});
    };

    public GenericFlagPole(Runnable onSave) {
        super(new Object());
        this.onSave = onSave;
    };

    public GenericFlagPole readNBT(Tag tag, HolderLookup.Provider registries) {
        orphanFlags.clear();
        ORPHAN_HOLDER_LIST_CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, registries), tag).ifSuccess(ls -> ls.stream().map(orphanFlags::add));
        return this;
    };

    public Tag writeNBT(HolderLookup.Provider registries) {
        return ORPHAN_HOLDER_LIST_CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registries), getOrphanHolderList()).getOrThrow();
    };

    @Override
    @Deprecated
    public Flaggable<Object, Object> getFlaggable() {
        return Flaggables.GENERIC;
    };

    @Override
    @Deprecated
    public Object getType() {
        return stack;
    };

    @Override
    @Deprecated
    public double getAmount() {
        return 1d;
    };

    /**
     * @deprecated Generic Flagss must be saved externally.
     */
    @Override
    @Deprecated
    public final void save() {
        onSave.run();
    };
    
};
