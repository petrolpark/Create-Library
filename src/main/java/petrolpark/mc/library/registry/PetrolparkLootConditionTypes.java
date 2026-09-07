package petrolpark.mc.library.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import petrolpark.mc.library.core.data.loot.condition.NumberComparisonLootCondition;
import petrolpark.mc.library.core.data.loot.condition.ParameterSuppliedLootCondition;

public class PetrolparkLootConditionTypes {
    
    public static final RegistryEntry<LootItemConditionType, LootItemConditionType>
    
    PARAMETERS_SUPPLIED = REGISTRATE.lootConditionType("parameters_supplied", ParameterSuppliedLootCondition.CODEC),
    NUMBER_COMPARISON = REGISTRATE.lootConditionType("number_comparison", NumberComparisonLootCondition.CODEC);

    public static final void register() {};
};
