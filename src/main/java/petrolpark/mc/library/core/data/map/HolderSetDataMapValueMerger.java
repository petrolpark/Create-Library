package petrolpark.mc.library.core.data.map;

import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;

@ParametersAreNonnullByDefault
public class HolderSetDataMapValueMerger<R, T> implements DataMapValueMerger<R, HolderSet<T>> {

    public static final <R, T> HolderSetDataMapValueMerger<R, T> create() {
        return new HolderSetDataMapValueMerger<>();
    };

    @Override
    public HolderSet<T> merge(Registry<R> registry, Either<TagKey<R>, ResourceKey<R>> first, HolderSet<T> firstValue, Either<TagKey<R>, ResourceKey<R>> second, HolderSet<T> secondValue) {
        return HolderSet.direct(Stream.concat(firstValue.stream(), secondValue.stream()).toList());
    };
    
};
