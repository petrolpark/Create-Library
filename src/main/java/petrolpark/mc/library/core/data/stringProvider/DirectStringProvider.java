package petrolpark.mc.library.core.data.stringProvider;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.level.storage.loot.LootContext;
import petrolpark.mc.library.registry.PetrolparkStringProviderTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

public record DirectStringProvider(Component component) implements StringProvider {

    public static final MapCodec<DirectStringProvider> CODEC = CodecHelper.singleFieldMap(ComponentSerialization.CODEC, "text", DirectStringProvider::component, DirectStringProvider::new);

    @Override
    public Component getString(LootContext context) {
        return component();
    };

    @Override
    public StringProviderType getStringProviderType() {
        return PetrolparkStringProviderTypes.DIRECT.get();
    };
    
};
