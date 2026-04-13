package com.petrolpark.core.contamination;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

public abstract class Contaminable<OBJECT, OBJECT_STACK> {

    public abstract boolean isContaminable(OBJECT object);

    public abstract boolean isContaminableStack(OBJECT_STACK stack);
  
    public abstract IContamination<OBJECT, OBJECT_STACK> getContamination(Object stack);

    public abstract Collection<Holder<Contaminant>> getIntrinsicContaminants(OBJECT object);

    public abstract Collection<Holder<Contaminant>> getShownIfAbsentContaminants(OBJECT object);

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
        public Collection<Holder<Contaminant>> getIntrinsicContaminants(Object object) {
            return Collections.emptySet();
        };

        @Override
        public Collection<Holder<Contaminant>> getShownIfAbsentContaminants(Object object) {
            return Collections.emptySet();
        };

    };
};
