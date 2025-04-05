package com.petrolpark.core.contamination;

import java.util.stream.Stream;

import net.minecraft.core.Holder;

public final class IncontaminableContamination implements IContamination<Object, Object> {

    public static final IncontaminableContamination INSTANCE = new IncontaminableContamination();

    private static final Object OBJECT = new Object();

    @Override
    public Contaminable<Object, Object> getContaminable() {
        return Contaminables.NOT;
    };

    @Override
    public Object getType() {
        return OBJECT;
    };

    @Override
    public double getAmount() {
        return 0d;
    };

    @Override
    public void save() {};

    @Override
    public boolean has(Holder<Contaminant> contaminant) {
        return false;
    };

    @Override
    public boolean hasAnyContaminant() {
        return false;
    };

    @Override
    public boolean hasAnyExtrinsicContaminant() {
        return false;
    };

    @Override
    public Stream<Holder<Contaminant>> streamAllContaminants() {
        return Stream.empty();
    };

    @Override
    public Stream<Holder<Contaminant>> streamOrphanExtrinsicContaminants() {
        return Stream.empty();
    };

    @Override
    public boolean contaminate(Holder<Contaminant> contaminant) {
        return false;
    };

    @Override
    public boolean contaminateAll(Stream<Holder<Contaminant>> contaminantsStream) {
        return false;
    };

    @Override
    public boolean decontaminate(Holder<Contaminant> contaminant) {
        return false;
    };

    @Override
    public boolean decontaminateOnly(Holder<Contaminant> contaminant) {
        return false;
    };

    @Override
    public boolean fullyDecontaminate() {
        return false;
    };
    
};
