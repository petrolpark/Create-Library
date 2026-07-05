package petrolpark.mc.library.core.registrate.builder.shared;

import java.util.Collections;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Function3;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import petrolpark.mc.library.core.data.condition.SharedFeatureEnabledCondition;
import petrolpark.mc.library.core.registrate.AbstractPetrolparkRegistrate;
import petrolpark.mc.library.core.registrate.DummyRegistrate;
import petrolpark.mc.library.core.registrate.builder.PetrolparkBlockBuilder;
import petrolpark.mc.library.core.registrate.dataGen.RegistrateConditionalLootTableProvider;
import petrolpark.mc.library.core.registrate.dataGen.RegistrateConditionalLootTableProvider.ConditionalLootType;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.world.item.SharedBlockItem;

public class SharedBlockBuilder<T extends Block, P> extends PetrolparkBlockBuilder<T, P> {

    public static <T extends Block, P extends AbstractPetrolparkRegistrate<?>> BlockBuilder<T, P> create(P owner, P parent, SharedFeatureFlag feature, String name, BuilderCallback callback, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return new SharedBlockBuilder<>(owner, parent, feature, name, callback, factory, () -> BlockBehaviour.Properties.of())
            .asOptional()
            .defaultLoot();
    };

    private final AbstractPetrolparkRegistrate<?> petrolparkOwner;
    public final SharedFeatureFlag featureFlag;

    public SharedBlockBuilder(AbstractPetrolparkRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, NonNullFunction<Properties, T> factory, NonNullSupplier<Properties> initialProperties) {
        super(owner, parent, name, callback, factory, initialProperties);
        this.petrolparkOwner = owner;
        this.featureFlag = featureFlag;
    };

    public <I extends Item> SharedItemBuilder<I, BlockBuilder<T, P>> sharedItem(Function3<? super T, Item.Properties, SharedFeatureFlag, I> factory) {
        return item((b, p) -> factory.apply(b, p, featureFlag));
    };

    @Override
    public SharedItemBuilder<BlockItem, BlockBuilder<T, P>> item() {
        return item(SharedBlockItem.of(featureFlag));
    };

    /**
     * Copied from {@link BlockBuilder#item()}
     */
    @Override
    public <I extends Item> SharedItemBuilder<I, BlockBuilder<T, P>> item(@Nonnull NonNullBiFunction<? super T, net.minecraft.world.item.Item.Properties, ? extends I> factory) {
        if (featureFlag.enabled()) return petrolparkOwner.<I, BlockBuilder<T, P>>sharedItem(this, featureFlag, getName(), p -> factory.apply(getEntry(), p));
        else return new SharedItemBuilder<>(DummyRegistrate.INSTANCE, this, featureFlag, getName(), petrolparkOwner.new SharedFeatureBuilderCallback(featureFlag), p -> factory.apply(getEntry(), p));
    };

    @Override
    public SharedBlockBuilder<T, P> onRegister(@Nonnull NonNullConsumer<? super T> callback) {
        if (featureFlag.enabled()) super.onRegister(callback);
        return this;
    };

    @Override
    public SharedBlockBuilder<T, P> loot(@Nonnull NonNullBiConsumer<RegistrateBlockLootTables, T> cons) {
        setData(RegistrateConditionalLootTableProvider.TYPE, (ctx, prov) -> prov.addLootAction(ConditionalLootType.BLOCK, tb -> {
            if (!ctx.getEntry().getLootTable().equals(BuiltInLootTables.EMPTY)) {
                cons.accept(tb.withConditions(Collections.singletonList(new SharedFeatureEnabledCondition(featureFlag))), ctx.getEntry());
            };
        }));
        return this;
    };
    
    
};
