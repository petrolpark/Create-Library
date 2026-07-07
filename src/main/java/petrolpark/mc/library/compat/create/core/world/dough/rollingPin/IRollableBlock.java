package petrolpark.mc.library.compat.create.core.world.dough.rollingPin;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.item.context.UseOnContext;

@ParametersAreNonnullByDefault
public interface IRollableBlock {
    
    public boolean canBeRollingPinRolled(UseOnContext context);

    public void rollingPinRoll(UseOnContext context);
};
