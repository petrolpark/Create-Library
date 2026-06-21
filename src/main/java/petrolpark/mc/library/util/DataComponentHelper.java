package petrolpark.mc.library.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.ItemStack;

public class DataComponentHelper {

    public static final boolean equalIgnoring(DataComponentMap map1, DataComponentMap map2, Predicate<DataComponentType<?>> ignoredTypes) {
        if (map1.equals(map2)) return true;
        Set<DataComponentType<?>> checkedTypes = new HashSet<>(map1.size());
        eachType: for (TypedDataComponent<?> component : map1) {
            checkedTypes.add(component.type());
            if (ignoredTypes.test(component.type())) continue eachType;
            if (!Objects.equals(component, map2.get(component.type()))) return false;
        };
        eachType: for (TypedDataComponent<?> component : map2) {
            if (checkedTypes.contains(component.type())) continue eachType;
            if (ignoredTypes.test(component.type())) continue eachType;
            if (!Objects.equals(map2.get(component.type()), component)) return false;
        };
        return true;
    };

    public static final <T> void revert(ItemStack stack, DataComponentType<T> componentType) {
        stack.set(componentType, stack.getPrototype().get(componentType));
    };
};
