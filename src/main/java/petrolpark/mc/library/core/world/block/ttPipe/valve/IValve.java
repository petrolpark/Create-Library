package petrolpark.mc.library.core.world.block.ttPipe.valve;

public interface IValve<VALVE extends IValve<VALVE>> {
    
    public VALVE then(VALVE valve);

    public VALVE or(VALVE valve);
};
