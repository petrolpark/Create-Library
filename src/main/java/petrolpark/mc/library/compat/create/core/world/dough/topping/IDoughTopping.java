package petrolpark.mc.library.compat.create.core.world.dough.topping;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRegistries;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

public interface IDoughTopping {

    public static final Codec<IDoughTopping> DIRECT_CODEC = PetrolparkCreateRegistries.DOUGH_TOPPING_TYPES.byNameCodec()
        .dispatch(IDoughTopping::getType, IDoughTopping.Type::codec);

    public static final Codec<Holder<IDoughTopping>> CODEC = RegistryFileCodec.create(PetrolparkCreateRegistries.Keys.DOUGH_TOPPING, DIRECT_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<IDoughTopping>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PetrolparkCreateRegistries.Keys.DOUGH_TOPPING);

    public Component name();
    
    public ResourceLocation textureLocation(DoughData doughData);

    public int getTint(DoughData doughData);

    public IDoughTopping.Type<?> getType();

    public interface Type<T extends IDoughTopping> {

        public MapCodec<T> codec();

        public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
    };
};
