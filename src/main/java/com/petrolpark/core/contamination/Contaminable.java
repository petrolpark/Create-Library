package com.petrolpark.core.contamination;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

public abstract class Contaminable<OBJECT, OBJECT_STACK> {

    public abstract boolean isContaminable(OBJECT object);

    public abstract boolean isContaminableStack(OBJECT_STACK stack);
  
    public abstract IContamination<OBJECT, OBJECT_STACK> getContamination(Object stack);

    public abstract Map<OBJECT, Set<Holder<Contaminant>>> getIntrinsicContaminants(RegistryAccess registryAccess);

    public abstract Map<OBJECT, Set<Holder<Contaminant>>> getShownIfAbsentContaminants(RegistryAccess registryAccess);

    public void onTagsLoaded(TagsUpdatedEvent event) {};

    public static class GenericContaminable extends Contaminable<Object,Object> {

        @Override
        public boolean isContaminable(Object object) {
            return false;
        };

        @Override
        public boolean isContaminableStack(Object stack) {
            return false;
        };

        @Override
        public IContamination<Object, Object> getContamination(Object stack) {
            return null;
        }

        @Override
        public Map<Object, Set<Holder<Contaminant>>> getIntrinsicContaminants(RegistryAccess registryAccess) {
            return Collections.emptyMap();
        };

        @Override
        public Map<Object, Set<Holder<Contaminant>>> getShownIfAbsentContaminants(RegistryAccess registryAccess) {
            return Collections.emptyMap();
        };

    };
};
