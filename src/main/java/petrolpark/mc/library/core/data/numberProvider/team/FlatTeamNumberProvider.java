package petrolpark.mc.library.core.data.numberProvider.team;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

/**
 * <p>{@code petrolpark:flat}</p>
 * 
 * Get a simple {@link NumberProvider} value, without reference to the Team.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - A {@link NumberProvider}
 * </ul>
 * 
 * Note that this has inline serialization. You can just refer to a {@link NumberProvider} directly when an {@link TeamNumberProvider} is expected and it will be converted.
 * 
 * @author petrolpark
 */
public record FlatTeamNumberProvider(NumberProvider numberProvider) implements TeamNumberProvider {

    public static final MapCodec<FlatTeamNumberProvider> CODEC = CodecHelper.singleFieldMap(NumberProviders.CODEC, "provider", FlatTeamNumberProvider::numberProvider, FlatTeamNumberProvider::new);

    @Override
    public float getFloat(ITeam Team, LootContext lootContext) {
        return numberProvider().getFloat(lootContext);
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.get(numberProvider());
    };

    @Override
    public LootTeamNumberProviderType getTeamNumberProviderType() {
        return PetrolparkNumberProviderTypes.FLAT_TEAM.get();
    };
    
};
