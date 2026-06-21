package petrolpark.mc.library.core.world.block.ttPipe.valve;

public final class DummyValve implements IValve<DummyValve> {

    public static final DummyValve INSTANCE = new DummyValve();

    private DummyValve() {};

    @Override
    public DummyValve then(DummyValve valve) {
        return INSTANCE;
    };

    @Override
    public DummyValve or(DummyValve valve) {
        return INSTANCE;
    };
    
};
