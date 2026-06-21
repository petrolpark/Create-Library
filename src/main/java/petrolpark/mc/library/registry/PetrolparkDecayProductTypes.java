package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.core.world.item.decay.product.ChangeItemDecayProduct;
import petrolpark.mc.library.core.world.item.decay.product.DecayProductType;
import petrolpark.mc.library.core.world.item.decay.product.FlagDecayProduct;
import petrolpark.mc.library.core.world.item.decay.product.NoDecayProduct;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.codec.StreamCodec;

public class PetrolparkDecayProductTypes {
    
    public static final RegistryEntry<DecayProductType, DecayProductType>

    NONE = REGISTRATE.decayProductType("none", MapCodec.unit(NoDecayProduct.INSTANCE), StreamCodec.unit(NoDecayProduct.INSTANCE)),
    FLAG = REGISTRATE.decayProductType("flag", FlagDecayProduct.CODEC, FlagDecayProduct.STREAM_CODEC),
    CHANGE_ITEM = REGISTRATE.decayProductType("change_item", ChangeItemDecayProduct.CODEC, ChangeItemDecayProduct.STREAM_CODEC);

    public static final void register() {};
};
