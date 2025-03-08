package com.petrolpark.client.ponder.instruction;

import java.util.HashSet;
import java.util.Set;

import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.PonderTag;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class HighlightTagInstruction extends TickingInstruction {

    @OnlyIn(Dist.CLIENT)
    public static final Set<PonderTag> highlightedTags = new HashSet<>();

    public final PonderTag tag;

    public HighlightTagInstruction(PonderTag tag, int duration) {
        super(false, duration);
        this.tag = tag;
    };

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        if (isComplete()) {
            highlightedTags.remove(tag);
        } else {
            highlightedTags.add(tag);
        };
    };
    
};
