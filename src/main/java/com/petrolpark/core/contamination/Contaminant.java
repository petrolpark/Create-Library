package com.petrolpark.core.contamination;

import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

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

    public static Contaminant get(ResourceLocation resourceLocation) {
        return PetrolparkRegistries.getRegistry(PetrolparkRegistries.Keys.CONTAMINANT).get(resourceLocation);
    };

    public static Contaminant getFromIntrinsicTag(TagKey<?> tagKey) {
        return getFromTag(tagKey, "intrinsic");
    };

    public static Contaminant getFromShowIfAbsentTag(TagKey<?> tagKey) {
        return getFromTag(tagKey, "show_if_absent");
    };

    public static Contaminant getFromTag(TagKey<?> tagKey, String pathSuffix) {
        ResourceLocation rl = tagKey.location();
        String[] path = rl.getPath().split("/");
        if (!path[0].equals("contaminant") || !path[2].equals(pathSuffix)) return null;
        return PetrolparkRegistries.getRegistry(PetrolparkRegistries.Keys.CONTAMINANT).get(ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), path[1]));
    };

    // Initial fields
    public final double preservationProportion;
    public final int color;
    public final int absentColor;
    protected final HolderSet<Contaminant> directChildrenHolders;

    // Internal fields
    protected final List<Holder<Contaminant>> childrenHolders;
    protected final List<Contaminant> parents = new ArrayList<>();

    // Publicly accessible fields
    protected ResourceLocation rl;
    protected String descriptionId;
    protected String absentDescriptionId;
    protected Set<Contaminant> childrenView = null;
    protected Set<Contaminant> parentsView = null;

    public Contaminant(double preservationProportion, int color, int absentColor, HolderSet<Contaminant> directChildrenHolders) {
        this.preservationProportion = preservationProportion;
        this.color = color;
        this.absentColor = absentColor;
        this.directChildrenHolders = directChildrenHolders;

        childrenHolders = new ArrayList<>(directChildrenHolders.size());
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
    public Set<Contaminant> getChildren() {
        if (childrenView == null) childrenView = childrenHolders.stream().map(Holder::value).collect(Collectors.toUnmodifiableSet());
        return childrenView;
    };

    /**
     * Any Contaminants (not just direct parents) which, if a Contamination has, will also belong to that Contamination.
     */
    public Set<Contaminant> getParents() {
        if (parentsView == null) parentsView = parents.stream().collect(Collectors.toUnmodifiableSet());
        return parentsView;
    };

    public ResourceLocation getLocation() {
        if (rl == null) rl = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(PetrolparkRegistries.Keys.CONTAMINANT).getKey(this);
        return rl;
    };

    public int compareTo(Contaminant contaminant) {
        return getLocation().compareTo(contaminant.getLocation());
    };

    public Component getName() {
        if (descriptionId == null) descriptionId = Util.makeDescriptionId("contaminant", getLocation());
        return Component.translatable(descriptionId);
    };

    public Component getNameColored() {
        return getName().copy().withStyle(Style.EMPTY.withColor(color));
    };

    public Component getAbsentName() {
        if (absentDescriptionId == null) absentDescriptionId = Util.makeDescriptionId("contaminant", getLocation()) + ".absent";
        return Component.translatable(absentDescriptionId);
    };

    public Component getAbsentNameColored() {
        return getAbsentName().copy().withStyle(Style.EMPTY.withColor(absentColor));
    };

    public static class ReloadListener implements ResourceManagerReloadListener {

        public final RegistryAccess registryAccess;

        public ReloadListener(RegistryAccess registryAccess) {
            this.registryAccess = registryAccess;
        };

        @Override
        public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
            Registry<Contaminant> registry = registryAccess.registryOrThrow(PetrolparkRegistries.Keys.CONTAMINANT);
            registry.forEach(parent -> {
                parent.directChildrenHolders.forEach(childHolder -> 
                    childHolder.value().parents.add(parent)
                );
            });
            registry.asLookup().listElements().forEach(parentHolder -> {
                Contaminant parent = parentHolder.value();
                try {
                    for (Holder<Contaminant> descendantHolder : GraphHelper.getAllDescendants((Holder<Contaminant>)parentHolder, h -> h.value().directChildrenHolders)) {
                        parent.childrenHolders.add(descendantHolder);
                        descendantHolder.value().parents.add(parent);
                    };
                } catch (CircularReferenceException e) {
                    throw new JsonSyntaxException(String.format("Contaminant %s is its own descendant. Replace the circular reference with a single Contaminant", parentHolder.getKey().location().toString()));
                };
            });
            IntrinsicContaminants.clear();
        };

    };
};
