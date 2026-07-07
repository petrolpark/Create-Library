package petrolpark.mc.library.core.data.numberProvider.team;

import java.util.Collections;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.FunctionNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.data.numberProvider.entity.EntityNumberProvider;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

/**
 * <p>{@code petrolpark:member_reduction}</p>
 * 
 * Get values from every {@link ITeam#streamMembers() Team member} and combine them.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - {@link EntityNumberProvider} to get value from each member
 * <li> {@code function} - ID of {@link LootNumberProviderType} of {@link FunctionNumberProvider} to apply to all values
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record MemberReductionTeamNumberProvider(EntityNumberProvider value, LootNumberProviderType function) implements TeamNumberProvider {

    public static final MapCodec<MemberReductionTeamNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        EntityNumberProvider.CODEC.fieldOf("value").forGetter(MemberReductionTeamNumberProvider::value),
        BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE.byNameCodec().validate(FunctionNumberProvider::isFunction).fieldOf("function").forGetter(MemberReductionTeamNumberProvider::function)
    ).apply(instance, MemberReductionTeamNumberProvider::new));

    @Override
    public float getFloat(ITeam team, LootContext context) {
        return FunctionNumberProvider.get(function()).create(Collections.emptyList()).applyFloat(context, team.streamMembers().mapToDouble(player -> value.getFloat(player, context)));
    };

    @Override
    public NumberEstimate getEstimate() {
        return NumberEstimate.UNKNOWN;
    };

    @Override
    public LootTeamNumberProviderType getTeamNumberProviderType() {
        return PetrolparkNumberProviderTypes.MEMBER_REDUCTION.get();
    };

    @Override
    public void validate(ValidationContext context) {
        TeamNumberProvider.super.validate(context);
        value().validate(context.forChild(".member_number_provider"));
    };
    
};
