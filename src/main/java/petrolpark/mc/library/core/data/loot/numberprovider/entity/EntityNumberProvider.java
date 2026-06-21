package petrolpark.mc.library.core.data.loot.numberprovider.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.core.data.loot.numberprovider.ContextEntityNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.NumberEstimate;
import petrolpark.mc.library.core.data.loot.numberprovider.itemstack.ItemStackNumberProvider;
import petrolpark.mc.library.core.data.loot.numberprovider.team.TeamNumberProvider;
import petrolpark.mc.library.registry.PetrolparkRegistries;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

/**
 * Entity-specific version of {@link NumberProvider}.
 * 
 * @see ContextEntityNumberProvider Get a value from an Entity in the LootContext
 * @see ItemStackNumberProvider Item Stack equivalent
 * @see TeamNumberProvider Team equivalent
 */
@ParametersAreNonnullByDefault
public interface EntityNumberProvider extends LootContextUser {

    /**
     * Use {@link EntityNumberProvider#CODEC} instead.
     */
    @ApiStatus.Internal
    static final Codec<EntityNumberProvider> TYPED_CODEC = PetrolparkRegistries.LOOT_ENTITY_NUMBER_PROVIDER_TYPES
        .byNameCodec()
        .dispatch(EntityNumberProvider::getEntityNumberProviderType, LootEntityNumberProviderType::codec);

    public static final Codec<EntityNumberProvider> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, NumberProviders.CODEC.xmap(FlatEntityNumberProvider::new, FlatEntityNumberProvider::numberProvider)));

    public float getFloat(Entity entity, LootContext lootContext);

    /**
     * Get the approximate bounds for the {@link EntityNumberProvider#getFloat(Entity, LootContext) output} of this {@link EntityNumberProvider} on a best-effort basis.
     * @see NumberEstimate#unknown() 
     */
    public NumberEstimate getEstimate();

    @ApiStatus.Experimental
    public default float getMaxFloat(Entity entity, LootContext context) {
        return getFloat(entity, context);
    };

    public LootEntityNumberProviderType getEntityNumberProviderType();
};
