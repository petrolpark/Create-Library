package petrolpark.mc.library.core.data.stringProvider;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import petrolpark.mc.library.registry.PetrolparkRegistries;

public interface StringProvider extends LootContextUser {
    
    /**
     * Use {@link StringProvider#CODEC} instead.
     */
    @ApiStatus.Internal
    static final Codec<StringProvider> TYPED_CODEC = PetrolparkRegistries.STRING_PROVIDER_TYPES
        .byNameCodec()
        .dispatch(StringProvider::getStringProviderType, StringProviderType::codec);

    public static final Codec<StringProvider> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, ComponentSerialization.CODEC.xmap(DirectStringProvider::new, DirectStringProvider::component)));

    public Component getString(LootContext context);

    public StringProviderType getStringProviderType();
};
