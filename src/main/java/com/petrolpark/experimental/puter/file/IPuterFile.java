package com.petrolpark.experimental.puter.file;

public sealed interface IPuterFile permits PuterFile, PuterDataStack {
    
    public long getSize();
};
