package petrolpark.mc.library.core.registrate;

import javax.annotation.Nonnull;

import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import petrolpark.mc.library.Petrolpark;

public final class DummyRegistrate extends AbstractPetrolparkRegistrate<DummyRegistrate> {

    public static final DummyRegistrate INSTANCE = new DummyRegistrate(Petrolpark.MOD_ID);

    protected DummyRegistrate(String modid) {
        super(modid);
    };

    @Override
    public <R> DummyRegistrate addRegisterCallback(@Nonnull ResourceKey<? extends Registry<R>> registryType, @Nonnull Runnable callback) {
        return this;
    };

    @Override
    public <R, T extends R> DummyRegistrate addRegisterCallback(@Nonnull String name, @Nonnull ResourceKey<? extends Registry<R>> registryType, @Nonnull NonNullConsumer<? super T> callback) {
        return this;
    };
    
};
