package petrolpark.mc.library.compat.create.shared.registry;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import petrolpark.mc.library.Petrolpark;

public class SharedCreatePoiTypes {

    public static final RegistryEntry<PoiType, PoiType> SEAT = Petrolpark.REGISTRATE.simple("seat", Registries.POINT_OF_INTEREST_TYPE, () ->
        new PoiType(
            Stream.of(AllBlocks.SEATS.toArray())
                .map(BlockEntry::get)
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .flatMap(List::stream)
                .collect(Collectors.toSet()),
            1, 1
        )
    );

    public static final void register() {};
};
