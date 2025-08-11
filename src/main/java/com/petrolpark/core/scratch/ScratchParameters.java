package com.petrolpark.core.scratch;

public interface ScratchParameters {

    public static interface ScratchParameters1<TYPE_1> extends ScratchParameters {
        public TYPE_1 get1();
    };

    public static interface ScratchParameters2<TYPE_1, TYPE_2> extends ScratchParameters1<TYPE_1> {
        public TYPE_2 get2();
    };

    public static interface ScratchParameters3<TYPE_1, TYPE_2, TYPE_3> extends ScratchParameters2<TYPE_1, TYPE_2> {
        public TYPE_3 get3();
    };

};
