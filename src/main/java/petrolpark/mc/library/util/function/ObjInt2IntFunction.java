package petrolpark.mc.library.util.function;

@FunctionalInterface
public interface ObjInt2IntFunction<T> {
    
    public int apply(T object, int input);
};
