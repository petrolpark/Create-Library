package petrolpark.mc.library.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.RequiresCreate;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

/**
 * Copied from {@link CStress Create source code}.
 */
@RequiresCreate
public class PetrolparkStressConfig extends ConfigBase {

    private static final Object2DoubleMap<ResourceLocation> DEFAULT_IMPACTS = new Object2DoubleOpenHashMap<>();
	private static final Object2DoubleMap<ResourceLocation> DEFAULT_CAPACITIES = new Object2DoubleOpenHashMap<>();

	protected final Map<ResourceLocation, ConfigValue<Double>> capacities = new HashMap<>();
	protected final Map<ResourceLocation, ConfigValue<Double>> impacts = new HashMap<>();

	@Override
	public void registerAll(@Nonnull ModConfigSpec.Builder builder) {
		builder.comment(".", Comments.su, Comments.impact)
			.push("impact");
		DEFAULT_IMPACTS.forEach((id, value) -> impacts.put(id, builder.define(id.getPath(), value)));
		builder.pop();

		builder.comment(".", Comments.su, Comments.capacity)
			.push("capacity");
		DEFAULT_CAPACITIES.forEach((id, value) -> capacities.put(id, builder.define(id.getPath(), value)));
		builder.pop();
	};

	@Override
	public String getName() {
		return "stressValues";
	};

	@Nullable
	public DoubleSupplier getImpact(Block block) {
		final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(block);
		final ConfigValue<Double> value = this.impacts.get(id);
		return value == null ? null : value::get;
	};

	@Nullable
	public DoubleSupplier getCapacity(Block block) {
		final ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow(block);
		final ConfigValue<Double> value = this.capacities.get(id);
		return value == null ? null : value::get;
	};

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setNoImpact() {
		return setImpact(0);
	};

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double value) {
		return builder -> {
			assertFromPetrolparkLibrary(builder);
			final ResourceLocation id = Petrolpark.asResource(builder.getName());
			DEFAULT_IMPACTS.put(id, value);
			return builder;
		};
	};

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setCapacity(double value) {
		return builder -> {
			assertFromPetrolparkLibrary(builder);
			final ResourceLocation id = Petrolpark.asResource(builder.getName());
			DEFAULT_CAPACITIES.put(id, value);
			return builder;
		};
	};

	private static void assertFromPetrolparkLibrary(BlockBuilder<?, ?> builder) {
		if (!builder.getOwner().getModid().equals(Petrolpark.MOD_ID)) {
			throw new IllegalStateException("Non-Petrolpark blocks cannot be added to Petrolpark's config.");
		};
	};

	private static class Comments {
		static String su = "[in Stress Units]";
		static String impact = "Individual coefficients of stress impact of kinetic blocks.";
		static String capacity = "Individual stress capacities of kinetic blocks.";
	};
    
};
