package petrolpark.mc.library.core.flags;

import java.util.stream.Stream;

import net.minecraft.core.Holder;

public final class UnflaggableFlagPole implements IFlagPole<Object, Object> {

    public static final UnflaggableFlagPole INSTANCE = new UnflaggableFlagPole();

    private static final Object OBJECT = new Object();

    private UnflaggableFlagPole() {};

    @Override
    public Flaggable<Object, Object> getFlaggable() {
        return Flaggables.NOT;
    };

    @Override
    public Object getType() {
        return OBJECT;
    };

    @Override
    public double getAmount() {
        return 0d;
    };

    @Override
    public void save() {};

    @Override
    public boolean has(Holder<Flag> flag) {
        return false;
    };

    @Override
    public boolean hasAnyFlag() {
        return false;
    };

    @Override
    public boolean hasAnyExtrinsicFlag() {
        return false;
    };

    @Override
    public Stream<Holder<Flag>> streamAllFlags() {
        return Stream.empty();
    };

    @Override
    public Stream<Holder<Flag>> streamOrphanExtrinsicFlags() {
        return Stream.empty();
    };

    @Override
    public boolean flag(Holder<Flag> flag) {
        return false;
    };

    @Override
    public boolean flagAll(Stream<Holder<Flag>> flagsStream) {
        return false;
    };

    @Override
    public boolean unflag(Holder<Flag> flag) {
        return false;
    };

    @Override
    public boolean unflagOnly(Holder<Flag> flag) {
        return false;
    };

    @Override
    public boolean clearFlags() {
        return false;
    };

    @Override
    public Stream<Holder<Flag>> streamIntrinsicFlags() {
        return Stream.empty();
    };

    @Override
    public Stream<Holder<Flag>> streamShownIfAbsentFlags() {
        return Stream.empty();
    };

    @Override
    public boolean isIntrinsic(Holder<Flag> flagHolder) {
        return false;
    };
    
};
