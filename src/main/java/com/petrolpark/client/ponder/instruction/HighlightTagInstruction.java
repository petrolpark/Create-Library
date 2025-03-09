package com.petrolpark.client.ponder.instruction;

import java.util.HashSet;
import java.util.Set;

import net.createmod.ponder.foundation.PonderScene;

public class HighlightTagInstruction extends TickingInstruction {

    @OnlyIn(Dist.CLIENT)
    public static final Set<ResourceLocation> highlightedTags = new HashSet<>();

    public final ResourceLocation tag;

    public HighlightTagInstruction(ResourceLocation tag, int duration) {
        super(false, duration);
        this.tag = tag;
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        if (isComplete()) {
            highlightedTags.remove(tag);
        } else {
            highlightedTags.add(tag);
        }
    }
    
}