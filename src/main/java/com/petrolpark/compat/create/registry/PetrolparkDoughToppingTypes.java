package com.petrolpark.compat.create.registry;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.create.core.world.dough.topping.CookableTopping;
import com.petrolpark.compat.create.core.world.dough.topping.DoughToppingType;
import com.petrolpark.compat.create.core.world.dough.topping.IDoughTopping;
import com.petrolpark.compat.create.core.world.dough.topping.SimpleTopping;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class PetrolparkDoughToppingTypes {
    
    public static final RegistryEntry<IDoughTopping.Type<?>, DoughToppingType<SimpleTopping>> SIMPLE = register(REGISTRATE, "simple", SimpleTopping.CODEC, SimpleTopping.STREAM_CODEC);
    public static final RegistryEntry<IDoughTopping.Type<?>, DoughToppingType<CookableTopping>> COOKABLE = register(REGISTRATE, "cookable", CookableTopping.CODEC, CookableTopping.STREAM_CODEC);

    public static final <T extends IDoughTopping> RegistryEntry<IDoughTopping.Type<?>, DoughToppingType<T>> register(AbstractRegistrate<?> registrate, String name, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return registrate.simple(name, PetrolparkCreateRegistries.Keys.DOUGH_TOPPING_TYPE, () -> new DoughToppingType<>(codec, streamCodec));
    };

    public static final void register() {};
};
