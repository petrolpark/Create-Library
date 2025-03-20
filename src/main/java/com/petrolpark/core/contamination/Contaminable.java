package com.petrolpark.core.contamination;

import java.util.Collections;
import java.util.Set;

public abstract class Contaminable<OBJECT, OBJECT_STACK> {

    public abstract boolean isContaminable(OBJECT object);

    public abstract boolean isContaminableStack(OBJECT_STACK stack);
  
    public abstract IContamination<OBJECT, OBJECT_STACK> getContamination(Object stack);

    public abstract Set<Contaminant> getIntrinsicContaminants(OBJECT object);

    public abstract Set<Contaminant> getShownIfAbsentContaminants(OBJECT object);

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
        };

        @Override
        public Set<Contaminant> getIntrinsicContaminants(Object object) {
            return Collections.emptySet();
        };

        @Override
        public Set<Contaminant> getShownIfAbsentContaminants(Object object) {
            return Collections.emptySet();
        };

    };
};
