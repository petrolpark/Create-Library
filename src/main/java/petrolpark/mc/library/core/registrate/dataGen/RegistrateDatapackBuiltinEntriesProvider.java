package petrolpark.mc.library.core.registrate.dataGen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.RegistrySetBuilder.RegistryBootstrap;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

public abstract class RegistrateDatapackBuiltinEntriesProvider<T> extends DatapackBuiltinEntriesProvider implements RegistrateProvider {

    protected final AbstractRegistrate<?> registrate;
    protected final ResourceKey<Registry<T>> registryKey;
    protected final Bootstrap<T> bootstrap;

    protected RegistrateDatapackBuiltinEntriesProvider(AbstractRegistrate<?> parent, PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ResourceKey<Registry<T>> registryKey, Bootstrap<T> bootstrap) {
        super(output, registries, new RegistrySetBuilder().add(registryKey, bootstrap), Collections.singleton(parent.getModid()));
        this.registrate = parent;
        this.registryKey = registryKey;
        this.bootstrap = bootstrap;
    };

    public abstract ProviderType<? extends RegistrateDatapackBuiltinEntriesProvider<T>> getProviderType();

    @Override
    @SuppressWarnings("deprecation")
    public CompletableFuture<?> run(@Nonnull CachedOutput output) {
        registrate.genData(getProviderType(), this);
        return super.run(output);
    };

    public RegistrateDatapackBuiltinEntriesProvider<T> addCallback(RegistryBootstrap<T> bootstrap) {
        this.bootstrap.children.add(bootstrap);
        return this;
    };

    public RegistrateDatapackBuiltinEntriesProvider<T> register(ResourceLocation name, T entry) {
        return addCallback(ctx -> ctx.register(createKey(name), entry));
    };

    public ResourceKey<T> createKey(ResourceLocation location) {
        return ResourceKey.create(registryKey, location);
    };

    @Override
    public LogicalSide getSide() {
        return LogicalSide.SERVER;
    };

    public static class Bootstrap<T> implements RegistryBootstrap<T> {

        protected final List<RegistryBootstrap<T>> children = new ArrayList<>();

        @Override
        public void run(@Nonnull BootstrapContext<T> context) {
            children.forEach(b -> b.run(context));
        };

    };
    
};
