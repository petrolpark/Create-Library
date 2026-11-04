package petrolpark.mc.library.core.registrate;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public abstract class DelegatingItemEntry<I extends Item> extends ItemEntry<I> {

    public DelegatingItemEntry(AbstractRegistrate<?> owner, DeferredHolder<Item, I> delegate) {
        super(owner, delegate);
    };

    public abstract DeferredHolder<Item, ? extends I> delegateEntry();

    @Override
    public I value() {
        return delegateEntry().value();
    };

    @Override
    public ResourceKey<Item> getKey() {
        return delegateEntry().getKey();
    };

    @Override
    public boolean equals(Object obj) {
        return delegateEntry().equals(obj);
    };

    @Override
    public boolean is(@Nonnull ResourceLocation id) {
        return delegateEntry().is(id);
    };

    @Override
    public boolean is(@Nonnull ResourceKey<Item> key) {
        return delegateEntry().is(key);
    };

    @Override
    public boolean is(@Nonnull Predicate<ResourceKey<Item>> filter) {
        return delegateEntry().is(filter);
    };

    @Override
    public boolean is(@Nonnull TagKey<Item> tag) {
        return delegateEntry().is(tag);
    };

    @Override
    @Deprecated
    public boolean is(@Nonnull Holder<Item> holder) {
        return delegateEntry().is(holder);
    };

    @Override
    public <Z> @Nullable Z getData(@Nonnull DataMapType<Item, Z> type) {
        return delegateEntry().getData(type);
    };

    @Override
    public Stream<TagKey<Item>> tags() {
        return delegateEntry().tags();
    };

    @Override
    public Either<ResourceKey<Item>, Item> unwrap() {
        return delegateEntry().unwrap();
    };

    @Override
    public Optional<ResourceKey<Item>> unwrapKey() {
        return delegateEntry().unwrapKey();
    };

    @Override
    public Holder<Item> getDelegate() {
        return delegateEntry().getDelegate();
    };
    
};
