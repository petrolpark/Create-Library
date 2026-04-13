package com.petrolpark.core.contamination;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.util.GraphHelper;
import com.petrolpark.util.GraphHelper.CircularReferenceException;

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
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ExtraCodecs;

public class Contaminant {

    public static final Codec<Contaminant> DIRECT_CODEC = ExtraCodecs.catchDecoderException(RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.doubleRange(0d, 1d).fieldOf("preservationProportion").forGetter(Contaminant::getPreservationProportion),
            Codec.intRange(0, 16777215).fieldOf("color").forGetter(Contaminant::getColor),
            Codec.intRange(0, 16777215).fieldOf("absentColor").forGetter(Contaminant::getAbsentColor),
            RegistryCodecs.homogeneousList(PetrolparkRegistries.Keys.CONTAMINANT).optionalFieldOf("children", HolderSet.direct()).forGetter(Contaminant::getDirectChildrenHolders)
        ).apply(instance, Contaminant::new)
    ));
    public static final Codec<Holder<Contaminant>> CODEC = RegistryFixedCodec.create(PetrolparkRegistries.Keys.CONTAMINANT);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Contaminant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PetrolparkRegistries.Keys.CONTAMINANT);

    public static int compareHolders(Holder<Contaminant> holder1, Holder<Contaminant> holder2) {
        return holder1.unwrapKey().map(ResourceKey::location).flatMap(rl1 -> holder2.unwrapKey().map(ResourceKey::location).map(rl2 -> rl1.compareTo(rl2))).orElse(0);
    };

    // Initial fields
    public final double preservationProportion;
    public final int color;
    public final int absentColor;
    protected final HolderSet<Contaminant> directChildrenHolders;

    // Internal fields
    protected final Set<Holder<Contaminant>> childrenHolders;
    protected final Set<Holder<Contaminant>> parentHolders = new HashSet<>();

    // Publicly accessible fields
    protected String descriptionId;
    protected String absentDescriptionId;
    protected Set<Holder<Contaminant>> childrenView = null;
    protected Set<Holder<Contaminant>> parentsView = null;

    public Contaminant(double preservationProportion, int color, int absentColor, HolderSet<Contaminant> directChildrenHolders) {
        this.preservationProportion = preservationProportion;
        this.color = color;
        this.absentColor = absentColor;
        this.directChildrenHolders = directChildrenHolders;

        childrenHolders = new HashSet<>(directChildrenHolders.size());
        directChildrenHolders.forEach(childrenHolders::add);
    };

    public double getPreservationProportion() {
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

    protected HolderSet<Contaminant> getDirectChildrenHolders() {
        return directChildrenHolders;
    };

    /**
     * All Contaminants (not just direct children) which any Contamination automatically has if they have this Contaminant.
     */
    public Set<Holder<Contaminant>> getChildren() {
        if (childrenView == null) childrenView = childrenHolders.stream().collect(Collectors.toUnmodifiableSet());
        return childrenView;
    };

    /**
     * Any Contaminants (not just direct parents) which, if a Contamination has, will also belong to that Contamination.
     */
    public Set<Holder<Contaminant>> getParents() {
        if (parentsView == null) parentsView = parentHolders.stream().collect(Collectors.toUnmodifiableSet());
        return parentsView;
    };

    public static Component getName(Holder<Contaminant> contaminantHolder) {
        Contaminant contaminant = contaminantHolder.value();
        if (contaminant.descriptionId == null) contaminant.descriptionId = Util.makeDescriptionId("contaminant", contaminantHolder.getKey().location());
        return Component.translatable(contaminant.descriptionId);
    };

    public static Component getNameColored(Holder<Contaminant> contaminantHolder) {
        return getName(contaminantHolder).copy().withStyle(Style.EMPTY.withColor(contaminantHolder.value().color));
    };

    public static Component getAbsentName(Holder<Contaminant> contaminantHolder) {
        Contaminant contaminant = contaminantHolder.value();
        if (contaminant.absentDescriptionId == null) contaminant.absentDescriptionId = Util.makeDescriptionId("contaminant", contaminantHolder.getKey().location()) + ".absent";
        return Component.translatable(contaminant.absentDescriptionId);
    };

    public static Component getAbsentNameColored(Holder<Contaminant> contaminantHolder) {
        return getAbsentName(contaminantHolder).copy().withStyle(Style.EMPTY.withColor(contaminantHolder.value().absentColor));
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return (obj instanceof Contaminant contaminant &&
            contaminant.preservationProportion == preservationProportion &&
            contaminant.color == color &&
            contaminant.absentColor == absentColor &&
            contaminant.directChildrenHolders.equals(directChildrenHolders)
        );
    };

    public static class ReloadListener implements ResourceManagerReloadListener {

        public final RegistryAccess registryAccess;

        public ReloadListener(RegistryAccess registryAccess) {
            this.registryAccess = registryAccess;
        };

        @Override
        public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
            Registry<Contaminant> registry = registryAccess.registryOrThrow(PetrolparkRegistries.Keys.CONTAMINANT);
            registry.asLookup().listElements().forEach(parentHolder -> {
                parentHolder.value().directChildrenHolders.forEach(childHolder -> 
                    childHolder.value().parentHolders.add(parentHolder)
                );
            });
            registry.asLookup().listElements().forEach(parentHolder -> {
                try {
                    for (Holder<Contaminant> descendantHolder : GraphHelper.getAllDescendants((Holder<Contaminant>)parentHolder, h -> h.value().directChildrenHolders)) {
                        parentHolder.value().childrenHolders.add(descendantHolder);
                        descendantHolder.value().parentHolders.add(parentHolder);
                    };
                } catch (CircularReferenceException e) {
                    throw new JsonSyntaxException(String.format("Contaminant %s is its own descendant. Replace the circular reference with a single Contaminant", parentHolder.getKey().location().toString()));
                };
            });
        };

    };
};
