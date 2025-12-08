package com.petrolpark.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonHelper {
  
    public static final boolean fuzzyMatch(@Nonnull JsonElement reference, @Nullable JsonElement element) {
        if (reference.isJsonNull()) return element == null || element.isJsonNull();
        if (element == null) return false;
        if (reference.isJsonPrimitive()) return reference.equals(element);
        if (reference.isJsonObject()) {
            if (!element.isJsonObject()) return false;
            final JsonObject elementObj = element.getAsJsonObject();
            return reference.getAsJsonObject().entrySet().stream().allMatch(entry -> fuzzyMatch(entry.getValue(), elementObj.get(entry.getKey())));
        };
        if (reference.isJsonArray()) {
            if (!element.isJsonArray()) return false;
            final JsonArray referenceArray = reference.getAsJsonArray();
            final List<JsonElement> elements = new LinkedList<>(element.getAsJsonArray().asList());
            eachReference: for (JsonElement referenceEntry : referenceArray) {
                final Iterator<JsonElement> iterator = elements.iterator();
                while (iterator.hasNext()) {
                    if (fuzzyMatch(referenceEntry, iterator.next())) {
                        iterator.remove();
                        continue eachReference;
                    };
                };
                return false;
            };
            return true;
        };
        return false;
    };
};
