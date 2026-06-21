package petrolpark.mc.library.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;

import petrolpark.mc.library.util.Lang;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(StressGaugeBlockEntity.class)
public abstract class StressGaugeBlockEntityMixin extends GaugeBlockEntity implements ThresholdSwitchObservable {

    public StressGaugeBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        throw new AssertionError();
    };

    @Override
    public int getMaxValue() {
        return 100;
    };
	
    @Override
	public int getMinValue() {
        return 0;
    };
	
    @Override
	public int getCurrentValue() {
        return (int)(100f * stress / (capacity == 0 ? 1 : capacity));
    };

    @Override
	public MutableComponent format(int value) {
        return Lang.translate("gui.threshold_switch.stress_capacity_percentage", value);
    };
    
};
