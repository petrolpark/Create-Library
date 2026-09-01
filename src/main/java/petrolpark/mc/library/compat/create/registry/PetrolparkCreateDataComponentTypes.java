package petrolpark.mc.library.compat.create.registry;

import java.util.UUID;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.shared.content.redstone.programmer.RedstoneProgram;
import petrolpark.mc.library.compat.create.shared.content.redstone.programmer.RedstoneProgrammerBlockItem.ItemStackRedstoneProgram;

public class PetrolparkCreateDataComponentTypes {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Petrolpark.MOD_ID);

    public static final DataComponentType<UUID> REDSTONE_PROGRAM_UUID = register(
        "redstone_program_uuid",
        builder -> builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC)
    );

    public static final DataComponentType<ItemStackRedstoneProgram> REDSTONE_PROGRAM = register(
        "redstone_program",
        builder -> builder.persistent(RedstoneProgram.codec(ItemStackRedstoneProgram::new)).networkSynchronized(RedstoneProgram.streamCodec(ItemStackRedstoneProgram::new))
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<Builder<T>> builder) {
		DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
		DATA_COMPONENTS.register(name, () -> type);
		return type;
	};

	@ApiStatus.Internal
	public static final void register(IEventBus modEventBus) {
		DATA_COMPONENTS.register(modEventBus);
	};
};
