package petrolpark.mc.library.compat.create.registry;

import petrolpark.mc.library.core.registrate.dataGen.RegistratePotatoCannonProjectileTypeProvider;
import com.tterrag.registrate.providers.ProviderType;

public class PetrolparkCreateRegistrateProviderTypes {
    
    public static final ProviderType<RegistratePotatoCannonProjectileTypeProvider> POTATO_CANNON_PROJECTILE = ProviderType.registerServerData("potato_cannon_projectile_type", RegistratePotatoCannonProjectileTypeProvider::new);

    public static final void register() {};
};
