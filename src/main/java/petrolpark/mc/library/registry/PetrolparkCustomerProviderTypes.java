package petrolpark.mc.library.registry;

import com.mojang.serialization.MapCodec;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.codec.StreamCodec;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;
import petrolpark.mc.library.core.world.restaurant.customer.MobCustomer;

public class PetrolparkCustomerProviderTypes {

    public static final RegistryEntry<ICustomer.ProviderType, ICustomer.ProviderType>

    MOB = Petrolpark.REGISTRATE.customerProviderType("mob", MobCustomer.Provider.CODEC, MobCustomer.Provider.STREAM_CODEC),
    NONE = Petrolpark.REGISTRATE.customerProviderType("none", MapCodec.unit(ICustomer.none()), StreamCodec.unit(ICustomer.none()));
    
    public static final void register() {};
};
