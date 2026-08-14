package petrolpark.mc.library.compat.create.core.world.item;

import java.util.function.DoubleSupplier;

import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.api.stress.BlockStressValues;

import net.minecraft.world.item.Item;

/**
 * Item equivalent of {@link BlockStressValues}
 */
public class ItemStressValues {
    /**
	 * Registry for suppliers of stress impacts. Determine the base impact at 1 RPM.
	 */
	public static final SimpleRegistry<Item, DoubleSupplier> IMPACTS = SimpleRegistry.create();
	/**
	 * Registry for suppliers of stress capacities. Determine the base capacity at 1 RPM.
	 */
	public static final SimpleRegistry<Item, DoubleSupplier> CAPACITIES = SimpleRegistry.create();

    public static double getImpact(Item block) {
		DoubleSupplier supplier = IMPACTS.get(block);
		return supplier == null ? 0 : supplier.getAsDouble();
	};

	public static double getCapacity(Item block) {
		DoubleSupplier supplier = CAPACITIES.get(block);
		return supplier == null ? 0 : supplier.getAsDouble();
	};

	public interface IKineticStatsDuck {

		public void setItem(Item item);
	};
};
