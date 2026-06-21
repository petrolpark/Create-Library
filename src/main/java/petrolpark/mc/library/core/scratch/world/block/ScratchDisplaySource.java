package petrolpark.mc.library.core.scratch.world.block;

import java.util.List;

import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.core.scratch.environment.ICreateDisplaySourceScratchEnvironment;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@RequiresCreate
public class ScratchDisplaySource extends DisplaySource {

    public final ICreateDisplaySourceScratchEnvironment environment;

    public ScratchDisplaySource(ICreateDisplaySourceScratchEnvironment environment) {
        this.environment = environment;
    };

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {
        return environment.getLines().stream().map(Component::literal).toList(); //TODO allow formatting
    };
    
};
