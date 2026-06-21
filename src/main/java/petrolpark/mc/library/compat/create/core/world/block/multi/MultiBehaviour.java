package petrolpark.mc.library.compat.create.core.world.block.multi;

import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

public abstract class MultiBehaviour<M extends IMulti<? super M>> extends BlockEntityBehaviour implements IMultiBehaviour<M> {

    public MultiBehaviour(SmartBlockEntity be) {
        super(be);
    };

    public abstract boolean isMultiController();

    public abstract void multiDisassembled();

    public abstract void transform(StructureTransform transform);

    @Override
    public void destroy() {
        super.destroy();
        disassembleMulti();
    };

    public final void disassembleMulti() {
        getOptionalMulti().ifPresent(IMulti::disassemble);
    };

    @Override
    public abstract BehaviourType<? extends MultiBehaviour<? extends M>> getType();
    
};
