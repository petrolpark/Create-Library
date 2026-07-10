package petrolpark.mc.library.util;

import java.util.function.Function;

import com.mojang.serialization.DataResult;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class DataValidationHelper {
    
    public static final <T extends LootContextUser> void validateHolder(Holder<T> holder, ValidationContext context, String childString) {
        holder.unwrap()
            .ifLeft(key -> {
                final String registry = key.registry().toString();
                final String id = key.location().toString();
                if (!context.allowsReferences()) {
                    context.reportProblem("Uses reference to " + registry + " called '" + id + "', but references are not allowed");
                } else if (context.hasVisitedElement(key)) {
                    context.reportProblem(registry + " called '" + id + "' is recursively called");
                } else {
                    context.resolver().get(key.registryKey(), key).ifPresentOrElse(
                        reference -> reference.value().validate(context.enterElement("->{" + id + "}", key.registryKey())),
                        () -> context.reportProblem("Unknown " + registry + " called '" + id + "'")
                    );
                };
            })
            .ifRight(value -> value.validate(context.forChild("." + childString)));
    };

    public static final <T extends LootContextUser> void validateHolderSet(HolderSet<T> holderSet, ValidationContext context, String childString) {
        holderSet.unwrap()
            .ifLeft(tagKey -> {
                if (!(context.allowsReferences())) context.reportProblem("Uses" + tagKey.registry().location().toString() + " tag named'" + tagKey.location().toString() + "', but references are not allowed");
            })
            .ifRight(list -> {
                for (int i = 0; i < list.size(); i++) validateHolder(list.get(i), context, childString + "[" + i + "]");
            });
    };

    public static final <T extends LootContextUser> Function<T, DataResult<T>> validateParamSet(LootContextParamSet params, String name) {
        return object -> {
            final ProblemReporter.Collector problemReporterCollector = new ProblemReporter.Collector();
            object.validate(new ValidationContext(problemReporterCollector, params));
            return problemReporterCollector.getReport()
                .map(error -> DataResult.<T>error(() -> "Validation error in " + name + ": " + error))
                .orElseGet(() -> DataResult.success(object));
        };
    };
};
