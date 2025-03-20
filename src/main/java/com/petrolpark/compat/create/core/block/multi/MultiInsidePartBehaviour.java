package com.petrolpark.compat.create.core.block.multi;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

public abstract class MultiInsidePartBehaviour<M extends IMulti<? super M>> extends MultiPartBehaviour<M> {

    public MultiInsidePartBehaviour(SmartBlockEntity be) {
        super(be);
    };
    
};
