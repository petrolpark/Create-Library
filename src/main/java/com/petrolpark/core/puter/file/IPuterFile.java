package com.petrolpark.core.puter.file;

public sealed interface IPuterFile permits PuterFile, PuterDataStack {
    
    public long getSize();
};
