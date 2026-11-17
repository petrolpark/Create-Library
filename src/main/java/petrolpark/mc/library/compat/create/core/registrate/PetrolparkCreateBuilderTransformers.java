package petrolpark.mc.library.compat.create.core.registrate;

import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateRegistrateProviderTypes;

public class PetrolparkCreateBuilderTransformers {
    
    public static final <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> potatoCannonProjectile(NonNullBiFunction<DataGenContext<Item, T>, PotatoCannonProjectileType.Builder, PotatoCannonProjectileType.Builder> builderTransformer) {
        return potatoCannonProjectile(null, builderTransformer);
    };

    public static final <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> potatoCannonProjectile(String name, NonNullBiFunction<DataGenContext<Item, T>, PotatoCannonProjectileType.Builder, PotatoCannonProjectileType.Builder> builderTransformer) {
        return b -> b.setData(PetrolparkCreateRegistrateProviderTypes.POTATO_CANNON_PROJECTILE, (ctx, prov) -> prov.register(
            name == null ? ctx.getId() : ResourceLocation.fromNamespaceAndPath(b.getOwner().getModid(), name),
            builderTransformer.apply(ctx, new PotatoCannonProjectileType.Builder().addItems(ctx.getEntry())).build()
        ));
    };
};
