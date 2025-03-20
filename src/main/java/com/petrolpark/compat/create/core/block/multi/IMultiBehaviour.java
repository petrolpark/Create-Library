package com.petrolpark.compat.create.core.block.multi;

import java.util.Optional;

public interface IMultiBehaviour<M extends IMulti<? super M>> {

    public abstract Optional<IMulti<? extends M>> getOptionalMulti();
    
};
