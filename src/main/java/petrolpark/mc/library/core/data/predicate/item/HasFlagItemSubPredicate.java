package petrolpark.mc.library.core.data.predicate.item;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.flags.ItemFlagPole;
import petrolpark.mc.library.util.codec.CodecHelper;

import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

public record HasFlagItemSubPredicate(Holder<Flag> flag) implements ItemSubPredicate {

    public static final Codec<HasFlagItemSubPredicate> CODEC = CodecHelper.singleField(Flag.CODEC, "flag", HasFlagItemSubPredicate::flag, HasFlagItemSubPredicate::new);

    @Override
    public boolean matches(@Nonnull ItemStack stack) {
        return ItemFlagPole.get(stack).has(flag);
    };
    
};
