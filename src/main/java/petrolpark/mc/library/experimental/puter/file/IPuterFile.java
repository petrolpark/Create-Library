package petrolpark.mc.library.experimental.puter.file;

public sealed interface IPuterFile permits PuterFile, PuterDataStack {
    
    public long getSize();
};
