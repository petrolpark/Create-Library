package petrolpark.mc.library.core.flags;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import petrolpark.mc.library.PetrolparkTags;

/**
 * A specific instance of a flaggable object, storing the specific Flags that object posseses.
 * @see Flaggable The class of flaggable objects this FlagPole is for
 * @see Flag
 */
public interface IFlagPole<OBJECT, OBJECT_STACK> {

    public static Optional<IFlagPole<?, ?>> get(Object object) {
        return Flaggables.streamFlaggables().map(c -> c.getFlagPole(object)).filter(Objects::nonNull).findFirst().map(c -> (IFlagPole<?, ?>)c);
    };

    /**
     * @param inputs
     * @param outputs
     * @see IFlagPole#perpetuate(Stream, Stream, Function) If you have a faster way of getting the FlagPole
     */
    public static <OBJECT> void perpetuate(Stream<OBJECT> inputs, Stream<OBJECT> outputs) {
        perpetuate(inputs, outputs, object -> get(object).orElse(null));
    };

    /**
     * @param <OBJECT> Type of the flaggable object
     * @param inputs
     * @param outputs
     * @param flagpoleGetter
     */
    public static <OBJECT> void perpetuate(Stream<OBJECT> inputs, Stream<OBJECT> outputs, Function<OBJECT, IFlagPole<?, ?>> flagpoleGetter) {
        Object2DoubleMap<Holder<Flag>> amounts = new Object2DoubleArrayMap<>();
        double totalAmount = inputs.map(flagpoleGetter)
            .dropWhile(Objects::isNull)
            .mapToDouble(flagpole -> {
                double amount = flagpole.getAmount();
                flagpole.streamAllFlags().forEach(flag -> amounts.merge(flag, amount, Double::sum));
                return amount;
            }).sum();
        outputs.map(flagpoleGetter)
            .dropWhile(Objects::isNull)
            .forEach(flagpole -> 
            flagpole.flagAll(
                amounts.object2DoubleEntrySet().stream()
                    .filter(entry -> entry.getKey().value().isPreserved(entry.getDoubleValue() / totalAmount))
                    .map(Object2DoubleMap.Entry::getKey)
            )
        );
    };

    public static void perpetuate(Stream<ItemStack> itemInputs, Stream<FluidStack> fluidInputs, double fluidWeight, Stream<ItemStack> itemOutputs, Stream<FluidStack> fluidOutputs) {
        Object2DoubleMap<Holder<Flag>> amounts = new Object2DoubleArrayMap<>();
        double totalAmount = itemInputs.map(ItemFlagPole::get)
            .mapToDouble(flagpole -> {
                double amount = flagpole.getAmount();
                flagpole.streamAllFlags().forEach(flag -> amounts.merge(flag, amount, Double::sum));
                return amount;
            }).sum();
        if (fluidWeight > 0d) totalAmount += fluidInputs.map(FluidFlagPole::get)
            .mapToDouble(flagpole -> {
                double amount = flagpole.getAmount() / fluidWeight;
                flagpole.streamAllFlags().forEach(flag -> amounts.merge(flag, amount, Double::sum));
                return amount;
            }).sum();
        double finalTotalAmount = totalAmount;
        Stream.concat(itemOutputs.map(ItemFlagPole::get), fluidOutputs.map(FluidFlagPole::get))
            .forEach(flagpole -> 
                flagpole.flagAll(
                    amounts.object2DoubleEntrySet().stream()
                        .filter(entry -> entry.getKey().value().isPreserved(entry.getDoubleValue() / finalTotalAmount))
                        .map(Object2DoubleMap.Entry::getKey)
                )
            );
    };

    public Flaggable<OBJECT, OBJECT_STACK> getFlaggable();
    
    public OBJECT getType();

    public double getAmount();

    /**
     * Called whenever the {@link IFlagPole} is changed, to save the changes to the underlying object.
     */
    public void save();

    public boolean has(Holder<Flag> flagHolder);

    public boolean hasAnyFlag();

    public boolean hasAnyExtrinsicFlag();

    public Stream<Holder<Flag>> streamAllFlags();

    /**
     * Stream all Flags in this FlagPole that:<ul>
     * <li>Are not {@link IFlagPole#isIntrinsic(Holder) intrinsic}
     * <li>Have no parents in this FlagPole</ul>
     * This is the minimum set of Flags needed to uniquely define a FlagPole instance.
     * @return Distinct Stream of Flags 
     */
    public Stream<Holder<Flag>> streamOrphanExtrinsicFlags();

    public default Stream<Holder<Flag>> streamShownFlags() {
        //TODO cache and make not shit
        final Set<Holder<Flag>> shownIfAbsent = streamShownAbsentFlags().collect(Collectors.toSet());
        return streamAllFlags()
            .filter(Predicate.not(PetrolparkTags.Flags.HIDDEN::matches))
            .filter(Predicate.not(shownIfAbsent::contains))
            .filter(flag -> flag.value().getParents().stream().noneMatch(this::has));
    };

    public default Stream<Holder<Flag>> streamShownAbsentFlags() {
        return streamShownIfAbsentFlags()
            .filter(Predicate.not(this::has))
            .filter(Predicate.not(PetrolparkTags.Flags.HIDDEN::matches));
    };

    public boolean flag(Holder<Flag> flagHolder);

    /**
     * Add several Flags, and 
     * @param flagsStream
     * @return
     */
    public boolean flagAll(Stream<Holder<Flag>> flagsStream);

    /**
     * Remove a Flag and any {@link Flag#getChildren() children} it has that don't belong to another parent.
     * If the Flag has any parents in this FlagPole, it will not be removed.
     * @param flagHolder
     * @return Whether this FlagPole changed
     * @see IFlagPole#unflagOnly(Holder) Don't remove children
     */
    public boolean unflag(Holder<Flag> flagHolder);

    /**
     * Remove a Flag, but not any of its children.
     * If the Flag has any parents in this FlagPole, it will not be removed.
     * @param flagHolder
     * @return Whether this FlagPole changed (the Flag was removed)
     * @see IFlagPole#unflag(Holder) Remove all children
     */
    public boolean unflagOnly(Holder<Flag> flagHolder);

    /**
     * Remove all extrinsic Flags.
     * @return Whether this FlagPole changed (whether it had any extrinsic Flags)
     */
    public boolean clearFlags();

    public boolean isIntrinsic(Holder<Flag> flagHolder);

    public Stream<Holder<Flag>> streamIntrinsicFlags();

    public Stream<Holder<Flag>> streamShownIfAbsentFlags();
};
