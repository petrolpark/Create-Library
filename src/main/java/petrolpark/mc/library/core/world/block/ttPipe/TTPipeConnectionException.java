package petrolpark.mc.library.core.world.block.ttPipe;

import net.minecraft.network.chat.Component;

public class TTPipeConnectionException extends Exception {
  
    public final Component component;

    public TTPipeConnectionException(Component component) {
        this.component = component;
    };
};
