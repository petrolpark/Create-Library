package petrolpark.mc.library.core.flags;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.GraphHelper;
import petrolpark.mc.library.util.GraphHelper.CircularReferenceException;

/**
 * <a href="https://github.com/petrolpark/Create-Library/wiki/Flags"> Wiki article
 */
@EventBusSubscriber
public class Flag {

    public static final Codec<Flag> DIRECT_CODEC = ExtraCodecs.catchDecoderException(RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.floatRange(0f, 1f).fieldOf("preservation_proportion").forGetter(Flag::getPreservationProportion),
            Codec.intRange(0, 16777215).fieldOf("color").forGetter(Flag::getColor),
            Codec.intRange(0, 16777215).fieldOf("absent_color").forGetter(Flag::getAbsentColor),
            RegistryCodecs.homogeneousList(PetrolparkRegistries.Keys.FLAG).optionalFieldOf("children", HolderSet.direct()).forGetter(Flag::getDirectChildrenHolders)
        ).apply(instance, Flag::new)
    ));
    public static final Codec<Holder<Flag>> CODEC = RegistryFixedCodec.create(PetrolparkRegistries.Keys.FLAG);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Flag>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PetrolparkRegistries.Keys.FLAG);

    public static int compareHolders(Holder<Flag> holder1, Holder<Flag> holder2) {
        return holder1.unwrapKey().map(ResourceKey::location).flatMap(rl1 -> holder2.unwrapKey().map(ResourceKey::location).map(rl2 -> rl1.compareTo(rl2))).orElse(0);
    };

    // Initial fields
    public final float preservationProportion;
    public final int color;
    public final int absentColor;
    protected final HolderSet<Flag> directChildrenHolders;

    // Internal fields
    protected final Set<Holder<Flag>> childrenHolders;
    protected final Set<Holder<Flag>> parentHolders = new HashSet<>();
    protected boolean familyInitialized = false;

    // Publicly accessible fields
    protected String descriptionId;
    protected String absentDescriptionId;
    protected Set<Holder<Flag>> childrenView = null;
    protected Set<Holder<Flag>> parentsView = null;

    public Flag(float preservationProportion, int color, int absentColor, HolderSet<Flag> directChildrenHolders) {
        this.preservationProportion = preservationProportion;
        this.color = color;
        this.absentColor = absentColor;
        this.directChildrenHolders = directChildrenHolders;

        childrenHolders = new HashSet<>(directChildrenHolders.size());
        directChildrenHolders.forEach(childrenHolders::add);
    };

    public float getPreservationProportion() {
        return preservationProportion;
    };

    public boolean isPreserved(double proportion) {
        if (preservationProportion == 0d) return proportion > 0d;
        return proportion >= preservationProportion;
    };

    public int getColor() {
        return color;
    };

    public int getAbsentColor() {
        return absentColor;
    };

    protected HolderSet<Flag> getDirectChildrenHolders() {
        return directChildrenHolders;
    };

    /**
     * All Flags (not just direct children) which any Flags automatically has if they have this Flag.
     */
    public Set<Holder<Flag>> getChildren() {
        if (!familyInitialized) return Collections.emptySet(); // Don't access too early
        if (childrenView == null) childrenView = childrenHolders.stream().collect(Collectors.toUnmodifiableSet());
        return childrenView;
    };

    /**
     * Any Flags (not just direct parents) which, if a Flags has, will also belong to that Flags.
     */
    public Set<Holder<Flag>> getParents() {
        if (!familyInitialized) return Collections.emptySet(); // Don't access too early
        if (parentsView == null) parentsView = parentHolders.stream().collect(Collectors.toUnmodifiableSet());
        return parentsView;
    };

    public static Component getName(Holder<Flag> flagHolder) {
        Flag flag = flagHolder.value();
        if (flag.descriptionId == null) flag.descriptionId = Util.makeDescriptionId("flag", flagHolder.getKey().location());
        return Component.translatable(flag.descriptionId);
    };

    public static Component getNameColored(Holder<Flag> flagHolder) {
        return getName(flagHolder).copy().withStyle(Style.EMPTY.withColor(flagHolder.value().color));
    };

    public static Component getAbsentName(Holder<Flag> flagHolder) {
        Flag flag = flagHolder.value();
        if (flag.absentDescriptionId == null) flag.absentDescriptionId = Util.makeDescriptionId("flag", flagHolder.getKey().location()) + ".absent";
        return Component.translatable(flag.absentDescriptionId);
    };

    public static Component getAbsentNameColored(Holder<Flag> flagHolder) {
        return getAbsentName(flagHolder).copy().withStyle(Style.EMPTY.withColor(flagHolder.value().absentColor));
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return (obj instanceof Flag flag &&
            flag.preservationProportion == preservationProportion &&
            flag.color == color &&
            flag.absentColor == absentColor &&
            flag.directChildrenHolders.equals(directChildrenHolders)
        );
    };

    public static final void loadChildren(RegistryAccess registries) {
        final Registry<Flag> registry = registries.registryOrThrow(PetrolparkRegistries.Keys.FLAG);
        registry.asLookup().listElements().forEach(parentHolder -> {
            parentHolder.value().directChildrenHolders.forEach(childHolder -> 
                childHolder.value().parentHolders.add(parentHolder)
            );
        });
        registry.asLookup().listElements().forEach(parentHolder -> {
            try {
                for (Holder<Flag> descendantHolder : GraphHelper.getAllDescendants((Holder<Flag>)parentHolder, h -> h.value().directChildrenHolders)) {
                    parentHolder.value().childrenHolders.add(descendantHolder);
                    descendantHolder.value().parentHolders.add(parentHolder);
                };
                parentHolder.value().familyInitialized = true;
            } catch (CircularReferenceException e) {
                throw new JsonSyntaxException(String.format("Flag %s is its own descendant. Replace the circular reference with a single Flag", parentHolder.getKey().location().toString()));
            };
        });
    };

    @SubscribeEvent
    public static final void onTagsUpdated(TagsUpdatedEvent event) {
        loadChildren(event.getRegistryAccess());
    };
};
