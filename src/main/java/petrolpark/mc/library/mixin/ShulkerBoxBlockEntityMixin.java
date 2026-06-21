package petrolpark.mc.library.mixin;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.flags.GenericFlagPole;
import petrolpark.mc.library.core.world.block.entity.IShulkerBoxBlockEntityDuck;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin extends RandomizableContainerBlockEntity implements IShulkerBoxBlockEntityDuck {

    @Unique
    private GenericFlagPole flagPole;

    protected ShulkerBoxBlockEntityMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        throw new AssertionError();
    };

    @Override
    public GenericFlagPole getFlagPole() {
        if (flagPole == null) flagPole = new GenericFlagPole();
        return flagPole;
    };

    @Override
    public void flagAll(Stream<Holder<Flag>> flags) {
        flagPole.flagAll(flags);
    };

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void petrolpark$createFlags(CallbackInfo ci) {
        getFlagPole();
    };

    @Inject(
        method = "loadFromTag",
        at = @At("HEAD")
    )
    public void petrolpark$loadFlags(CompoundTag tag, HolderLookup.Provider levelRegistry, CallbackInfo ci) {
        flagPole = new GenericFlagPole().readNBT(tag.get("Flags"), levelRegistry);
    };

    @Inject(
        method = "saveAdditional",
        at = @At("HEAD")
    )
    public void petrolpark$saveFlags(CompoundTag tag, HolderLookup.Provider levelRegistry, CallbackInfo ci) {
        tag.put("Flags", getFlagPole().writeNBT(levelRegistry));
    };
    
};
