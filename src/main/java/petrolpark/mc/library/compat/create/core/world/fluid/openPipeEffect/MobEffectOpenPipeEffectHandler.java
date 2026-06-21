package petrolpark.mc.library.compat.create.core.world.fluid.openPipeEffect;

import java.util.function.Supplier;

import com.simibubi.create.api.effect.OpenPipeEffectHandler;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

public record MobEffectOpenPipeEffectHandler(Supplier<MobEffectInstance> effect) implements OpenPipeEffectHandler {

    @Override
    public void apply(Level level, AABB area, FluidStack fluid) {
        if (level.getGameTime() % 5 != 0) return;
        level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAffectedByPotions).forEach(entity -> entity.addEffect(effect().get()));
    };
    
};
