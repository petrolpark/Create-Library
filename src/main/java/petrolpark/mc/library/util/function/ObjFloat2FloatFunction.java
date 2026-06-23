package petrolpark.mc.library.util.function;

@FunctionalInterface
public interface ObjFloat2FloatFunction<T> {
    
    public float apply(T object, float input);
};
