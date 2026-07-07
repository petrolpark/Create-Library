package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import petrolpark.mc.library.core.data.stringProvider.DirectStringProvider;
import petrolpark.mc.library.core.data.stringProvider.NumberStringProvider;
import petrolpark.mc.library.core.data.stringProvider.StringProviderType;

public class PetrolparkStringProviderTypes {

    public static final RegistryEntry<StringProviderType, StringProviderType>

    DIRECT = REGISTRATE.stringProviderType("direct", DirectStringProvider.CODEC),
    NUMBER = REGISTRATE.stringProviderType("number", NumberStringProvider.CODEC);
    
    public static final void register() {};
};
