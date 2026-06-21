package petrolpark.mc.library.core.registrate.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PetrolparkBlockEntityBuilder<T extends BlockEntity, P> extends BlockEntityBuilder<T, P> implements IPetrolparkBlockEntityBuilder<T, P, PetrolparkBlockEntityBuilder<T, P>> {

    public static <T extends BlockEntity, P> PetrolparkBlockEntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        return new PetrolparkBlockEntityBuilder<>(owner, parent, name, callback, factory);
    };

    protected PetrolparkBlockEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        super(owner, parent, name, callback, factory);
    };

    @Override
    public PetrolparkBlockEntityBuilder<T, P> self() {
        return this;
    };

    @Override
    public BlockEntityType<T> getEntry() {
        return super.getEntry();
    };
    
};
