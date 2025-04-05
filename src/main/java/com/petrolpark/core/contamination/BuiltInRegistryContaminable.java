package com.petrolpark.core.contamination;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.petrolpark.util.Pair;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public abstract class BuiltInRegistryContaminable<OBJECT, OBJECT_STACK> extends Contaminable<OBJECT, OBJECT_STACK> {

    public final Registry<OBJECT> builtInRegistry;

    public BuiltInRegistryContaminable(Registry<OBJECT> builtInRegistry) {
        this.builtInRegistry = builtInRegistry;
    };

    @Override
    public Map<OBJECT, Set<Holder<Contaminant>>> getIntrinsicContaminants(RegistryAccess registryAccess) {
        return getContaminantsFromTags(registryAccess, Contaminant::getKeyFromInstrinsicTag);
    };

    @Override
    public Map<OBJECT, Set<Holder<Contaminant>>> getShownIfAbsentContaminants(RegistryAccess registryAccess) {
        return getContaminantsFromTags(registryAccess, Contaminant::getKeyFromShownIfAbsentTag);
    };

    protected Map<OBJECT, Set<Holder<Contaminant>>> getContaminantsFromTags(RegistryAccess registryAccess, Function<TagKey<?>, ResourceKey<Contaminant>> contaminantKeyGetter) {
        return builtInRegistry.asLookup().listElements()
            .map(holder -> Pair.of(
                holder.value(),
                withChildren(holder.tags()
                    .map(contaminantKeyGetter::apply)
                    .dropWhile(Objects::isNull)
                    .map(registryAccess::holder)
                    .filter(Optional::isPresent) // Ignore unknown Contaminants
                    .map(Optional::get)
                    .collect(Collectors.toSet())
                )
            )).dropWhile(pair -> pair.getSecond().isEmpty())
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    };

    public static Set<Holder<Contaminant>> withChildren(Set<Holder<Contaminant>> contaminants) {
        return Stream.concat(contaminants.stream(), contaminants.stream().map(Holder::value).map(Contaminant::getChildren).flatMap(Set::stream)).collect(Collectors.toSet()); 
    };
    
};
