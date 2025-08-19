package com.petrolpark.compat.create.core.dough;

import com.mojang.serialization.MapCodec;
import com.petrolpark.RequiresCreate;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

@RequiresCreate
public interface IDoughType<DOUGH extends IDough<DOUGH>> {
    
    public MapCodec<DOUGH> codec();

    public StreamCodec<? super RegistryFriendlyByteBuf, DOUGH> streamCodec();
};
