package petrolpark.mc.library.compat.create;

import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.core.registrate.AbstractPetrolparkCreateRegistrate;
import petrolpark.mc.library.compat.create.core.world.item.attribute.SimplePetrolparkItemAttribute;

@ParametersAreNonnullByDefault
public final class PetrolparkCreateRegistrate extends AbstractPetrolparkCreateRegistrate<PetrolparkCreateRegistrate> {

    protected PetrolparkCreateRegistrate() {
        super(Petrolpark.MOD_ID);
    };

    public RegistryEntry<ItemAttributeType, SimplePetrolparkItemAttribute> simpleItemAttributeType(String name, BiFunction<ItemStack, Level, Boolean> predicate) {
        return super.itemAttributeType(name, new SimplePetrolparkItemAttribute(name, predicate));
    };

    @Override
    protected void onData(GatherDataEvent event) {
        //NOOP, we use the non-Create registrate
    };

    @Override
    public <T extends RegistrateProvider> PetrolparkCreateRegistrate addDataGenerator(ProviderType<? extends T> type, NonNullConsumer<? extends T> cons) {
        Petrolpark.REGISTRATE.addDataGenerator(type, cons);
        return this;
    };
    
    @Override
    public <P extends RegistrateProvider> Optional<P> getDataProvider(ProviderType<P> type) {
        return Petrolpark.REGISTRATE.getDataProvider(type);
    };
    
};
