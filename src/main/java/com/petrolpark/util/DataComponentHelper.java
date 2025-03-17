package com.petrolpark.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;

public class DataComponentHelper {
    
    public static final boolean equalIgnoring(DataComponentMap map1, DataComponentMap map2, DataComponentType<?> ...ignoredTypes) {
        if (map1.equals(map2)) return true;
        Set<DataComponentType<?>> checkedTypes = new HashSet<>(map1.size());
        eachType: for (TypedDataComponent<?> component : map1) {
            checkedTypes.add(component.type());
            for (DataComponentType<?> ignoredType : ignoredTypes) if (component.type().equals(ignoredType)) continue eachType;
            if (!Objects.equals(component, map2.get(component.type()))) return false;
        };
        eachType: for (TypedDataComponent<?> component : map2) {
            if (checkedTypes.contains(component.type())) continue eachType;
            for (DataComponentType<?> ignoredType : ignoredTypes) if (component.type().equals(ignoredType)) continue eachType;
            if (!Objects.equals(map2.get(component.type()), component)) return false;
        };
        return true;
    };
};
