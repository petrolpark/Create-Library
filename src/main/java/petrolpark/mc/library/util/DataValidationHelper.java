package petrolpark.mc.library.util;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;

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
};
