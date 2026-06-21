package com.petrolpark.core.flags;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public abstract class BuiltInRegistryFlaggable<OBJECT, OBJECT_STACK> extends Flaggable<OBJECT, OBJECT_STACK> {

    public final Registry<OBJECT> builtInRegistry;
    public final DataMapType<OBJECT, List<Holder<Flag>>> intrinsicFlagsDataMapType;
    public final DataMapType<OBJECT, List<Holder<Flag>>> shownIfAbsentFlagsDataMapType;

    @Override
    public final Collection<Holder<Flag>> getIntrinsicFlags(OBJECT object) {
        return Optional.ofNullable(builtInRegistry.wrapAsHolder(object).getData(intrinsicFlagsDataMapType)).map(BuiltInRegistryFlaggable::withChildren).orElseGet(Collections::emptySet);
    };

    @Override
    public final Collection<Holder<Flag>> getShownIfAbsentFlags(OBJECT object) {
        return Optional.ofNullable(builtInRegistry.wrapAsHolder(object).getData(shownIfAbsentFlagsDataMapType)).orElseGet(Collections::emptyList);
    };

    public BuiltInRegistryFlaggable(Registry<OBJECT> builtInRegistry, DataMapType<OBJECT, List<Holder<Flag>>> intrinsicFlagsDataMapType, DataMapType<OBJECT, List<Holder<Flag>>> shownIfAbsentFlagsDataMapType) {
        this.builtInRegistry = builtInRegistry;
        this.intrinsicFlagsDataMapType = intrinsicFlagsDataMapType;
        this.shownIfAbsentFlagsDataMapType = shownIfAbsentFlagsDataMapType;
    };

    protected Map<OBJECT, Set<Holder<Flag>>> getFlagsFromTags(RegistryAccess registryAccess, Function<TagKey<?>, ResourceKey<Flag>> flagKeyGetter) {
        return builtInRegistry.asLookup().listElements()
            .map(holder -> Pair.of(
                holder.value(),
                withChildren(holder.tags()
                    .map(flagKeyGetter::apply)
                    .dropWhile(Objects::isNull)
                    .map(registryAccess::holder)
                    .filter(Optional::isPresent) // Ignore unknown Flags
                    .map(Optional::get)
                    .collect(Collectors.toSet())
                )
            )).dropWhile(pair -> pair.getSecond().isEmpty())
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    };

    public static Set<Holder<Flag>> withChildren(Collection<Holder<Flag>> flags) {
        return Stream.concat(flags.stream(), flags.stream().map(Holder::value).map(Flag::getChildren).flatMap(Set::stream)).collect(Collectors.toSet()); 
    };
    
};
