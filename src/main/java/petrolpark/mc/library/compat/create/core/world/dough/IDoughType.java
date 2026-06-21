package petrolpark.mc.library.compat.create.core.world.dough;

import com.mojang.serialization.MapCodec;
import petrolpark.mc.library.compat.create.RequiresCreate;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

@RequiresCreate
public interface IDoughType<DOUGH extends IDough> {
    
    public MapCodec<DOUGH> codec();

    public StreamCodec<? super RegistryFriendlyByteBuf, DOUGH> streamCodec();
};
