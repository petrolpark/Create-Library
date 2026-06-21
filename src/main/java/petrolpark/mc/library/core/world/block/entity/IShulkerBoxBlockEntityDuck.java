package petrolpark.mc.library.core.world.block.entity;

import java.util.stream.Stream;

import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.flags.GenericFlagPole;

import net.minecraft.core.Holder;

public interface IShulkerBoxBlockEntityDuck {
    
    public GenericFlagPole getFlagPole();

    public void flagAll(Stream<Holder<Flag>> flags);
};
