package petrolpark.mc.library.util.function;

@FunctionalInterface
public interface ObjDouble2DoubleFunction<T> {
    
    public double apply(T object, double input);
};
