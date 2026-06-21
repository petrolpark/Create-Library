package petrolpark.mc.library.compat.create.core.world.dough;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRegistries;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@ApiStatus.Experimental
public interface IDough {

    /**
     * Use {@link IDough#CODEC} instead.
     */
    static final Codec<IDough> TYPED_CODEC = PetrolparkCreateRegistries.DOUGH_TYPES.byNameCodec()
        .dispatch(IDough::getType, IDoughType::codec);

    public static final Codec<IDough> CODEC = Codec.lazyInitialized(() -> TYPED_CODEC);

    static final StreamCodec<RegistryFriendlyByteBuf, IDough> STREAM_CODEC = ByteBufCodecs.registry(PetrolparkCreateRegistries.Keys.DOUGH_TYPE)
        .dispatch(IDough::getType, IDoughType::streamCodec);

    public float minimumThickness();
    
    public boolean cuttable();

    public boolean toppable();

    @OnlyIn(Dist.CLIENT)
    public Component name();

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation textureLocation();

    @OnlyIn(Dist.CLIENT)
    public int tint();

    /**
     * A string as unique as needed (should include a namespace) to distinguish this Dough in JEI.
     */
    public String uniqueString();

    //TODO texture

    public IDoughType<?> getType();
};
