package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import com.petrolpark.core.item.decay.product.ChangeItemDecayProduct;
import com.petrolpark.core.item.decay.product.DecayProductType;
import com.petrolpark.core.item.decay.product.FlagDecayProduct;
import com.petrolpark.core.item.decay.product.NoDecayProduct;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.codec.StreamCodec;

public class PetrolparkDecayProductTypes {
    
    public static final RegistryEntry<DecayProductType, DecayProductType>

    NONE = REGISTRATE.decayProductType("none", MapCodec.unit(NoDecayProduct.INSTANCE), StreamCodec.unit(NoDecayProduct.INSTANCE)),
    FLAG = REGISTRATE.decayProductType("flag", FlagDecayProduct.CODEC, FlagDecayProduct.STREAM_CODEC),
    CHANGE_ITEM = REGISTRATE.decayProductType("change_item", ChangeItemDecayProduct.CODEC, ChangeItemDecayProduct.STREAM_CODEC);

    public static final void register() {};
};
