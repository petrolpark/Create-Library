package petrolpark.mc.library.core.data.numberProvider;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.itemStack.ItemStackNumberProvider;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;
import petrolpark.mc.library.util.codec.CodecHelper;

/**
 * <p>{@code petrolpark:tool_property}</p>
 * 
 * Get a {@link ItemStackNumberProvider} value of the {@link LootContextParams#TOOL Item Stack tool provided} in the {@link LootContext}.
 * 
 * Arguments:
 * <ul>
 * <li> {@code value} - An {@link ItemStackNumberProvider} to call on the tool Item Stack
 * </ul>
 * 
 * @author petrolpark
 */
@ParametersAreNonnullByDefault
public record ContextToolNumberProvider(ItemStackNumberProvider value) implements IEstimableNumberProvider {

    public static final MapCodec<ContextToolNumberProvider> CODEC = CodecHelper.singleFieldMap(ItemStackNumberProvider.CODEC, "value", ContextToolNumberProvider::value, ContextToolNumberProvider::new);

    @Override
    public float getFloat(LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool != null) return value.getFloat(tool, context);
        return 0f;
    };

    @Override
    public int getInt(LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool != null) return value.getInt(tool, context);
        return 0;
    };

    @Override
    public NumberEstimate getEstimate() {
        return value().getEstimate();
    };

    @Override
    public float getMaxFloat(LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool != null) return value.getMaxFloat(tool, context);
        return 0f;
    };

    @Override
    public LootNumberProviderType getType() {
        return PetrolparkNumberProviderTypes.CONTEXT_TOOL.get();
    };

    @Override
    public void validate(ValidationContext context) {
        IEstimableNumberProvider.super.validate(context);
        value().validate(context.forChild(".item_stack_number_provider"));
    };
    
};
