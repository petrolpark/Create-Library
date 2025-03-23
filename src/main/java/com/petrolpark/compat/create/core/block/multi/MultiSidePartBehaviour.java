package com.petrolpark.compat.create.core.block.multi;

import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.Direction;

public abstract class MultiSidePartBehaviour<M extends IMulti<? super M>> extends MultiPartBehaviour<M> implements IMultiSideBehaviour<M> {

    public MultiSidePartBehaviour(SmartBlockEntity be) {
        super(be);
    };

    /**
     * Called after a {@link StructureTransform} to check if the face of the {@link IMulti} this side Block is on is still valid.
     * @param face
     * @return {@code false} to disassemble the Multi
     */
    public abstract boolean isStillValidMultiFace(Direction face);

    @Override
    public void transform(StructureTransform transform) {
        super.transform(transform);
        if (!isStillValidMultiFace(getMultiFace())) getOptionalMulti().ifPresent(IMulti::disassemble);
    };
    
};
