package com.petrolpark.core.scratch;

import com.petrolpark.core.scratch.ScratchParameterTypes.EmptyScratchParameterTypes;
import com.petrolpark.core.scratch.ScratchParameterTypes.ScratchParameterTypes1;
import com.petrolpark.core.scratch.ScratchParameterTypes.ScratchParameterTypes2;
import com.petrolpark.core.scratch.ScratchParameters.ScratchParameters1;
import com.petrolpark.core.scratch.ScratchParameters.ScratchParameters2;
import com.petrolpark.core.scratch.type.IScratchType;

public sealed interface ScratchParameterTypes<PARAMETERS extends ScratchParameters> permits
    EmptyScratchParameterTypes,
    ScratchParameterTypes1,
    ScratchParameterTypes2
{
    static final class EmptyScratchParameterTypes implements ScratchParameterTypes<ScratchParameters> {};
    public static final EmptyScratchParameterTypes NONE = new EmptyScratchParameterTypes();

    public static record ScratchParameterTypes1 <
        TYPE_1
    > (
        IScratchType<? super TYPE_1> type1
    ) implements ScratchParameterTypes<ScratchParameters1<
        TYPE_1
    >> {};

    public static record ScratchParameterTypes2 <
        TYPE_1,
        TYPE_2
    > (
        IScratchType<? super TYPE_1> type1,
        IScratchType<? super TYPE_2> type2
    ) implements ScratchParameterTypes<ScratchParameters2<
        TYPE_1,
        TYPE_2
    >> {};
};
