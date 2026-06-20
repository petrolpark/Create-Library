package com.petrolpark.core.scratch.world.block;

import java.util.List;

import com.petrolpark.compat.create.RequiresCreate;
import com.petrolpark.core.scratch.environment.ICreateDisplaySourceScratchEnvironment;
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
