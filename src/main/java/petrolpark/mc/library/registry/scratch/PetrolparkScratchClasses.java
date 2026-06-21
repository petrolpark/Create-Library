package petrolpark.mc.library.registry.scratch;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import petrolpark.mc.library.core.scratch.classes.BlockPosScratchClass;
import petrolpark.mc.library.core.scratch.classes.BooleanScratchClass;
import petrolpark.mc.library.core.scratch.classes.DirectionScratchClass;
import petrolpark.mc.library.core.scratch.classes.IScratchClassType;
import petrolpark.mc.library.core.scratch.classes.IntegerScratchClass;
import petrolpark.mc.library.core.scratch.classes.ListScratchClass;
import petrolpark.mc.library.core.scratch.classes.NullScratchClass;
import petrolpark.mc.library.core.scratch.classes.RealScratchClass;
import petrolpark.mc.library.core.scratch.classes.ScratchClassType;
import petrolpark.mc.library.core.scratch.classes.StringScratchClass;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class PetrolparkScratchClasses {

    public static final RegistryEntry<IScratchClassType, NullScratchClass> NULL = REGISTRATE.simpleScratchClass("null", NullScratchClass::new);
    
    public static final RegistryEntry<IScratchClassType, BooleanScratchClass> BOOLEAN = REGISTRATE.simpleScratchClass("boolean", BooleanScratchClass::new);
    public static final RegistryEntry<IScratchClassType, IntegerScratchClass> INTEGER = REGISTRATE.simpleScratchClass("boolean", IntegerScratchClass::new);
    public static final RegistryEntry<IScratchClassType, RealScratchClass> REAL = REGISTRATE.simpleScratchClass("boolean", RealScratchClass::new);
    public static final RegistryEntry<IScratchClassType, StringScratchClass> STRING = REGISTRATE.simpleScratchClass("string", StringScratchClass::new);
    public static final RegistryEntry<IScratchClassType, DirectionScratchClass> DIRECTION = REGISTRATE.simpleScratchClass("direction", DirectionScratchClass::new);
    public static final RegistryEntry<IScratchClassType, BlockPosScratchClass> BLOCK_POS = REGISTRATE.simpleScratchClass("block_pos", BlockPosScratchClass::new);

    public static final RegistryEntry<IScratchClassType, ScratchClassType<ListScratchClass<?, ?>>> LIST = REGISTRATE.scratchClassType("list", ListScratchClass.CODEC, ListScratchClass.STREAM_CODEC);

    public static final void register() {};
};
