package com.petrolpark.mixin.compat.create;

import java.util.ArrayList;
import java.util.Arrays;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.kinetics.press.PressingBehaviour;

@Mixin(PressingBehaviour.Mode.class)
public abstract class PressingBehaviourModeMixin {
    
    @Shadow
    @Final
    @Mutable
    private static PressingBehaviour.Mode[] $VALUES;

    @SuppressWarnings("unused")
    private static final PressingBehaviour.Mode PETROLPARK_MESH_BASIN = petrolpark$addMeshBasin();

    @Invoker("<init>")
    public static PressingBehaviour.Mode invokeInit(String internalName, int internalId, float headOffset) {
        throw new AssertionError();
    };

     /**
     * Creates a new entry in the {@link com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.HeatLevel HeatLevel enum}.
     * The technique is copied from <a href="https://github.com/LudoCrypt/Noteblock-Expansion-Forge/blob/main/src/main/java/net/ludocrypt/nbexpand/mixin/NoteblockInstrumentMixin.java">here</a>.
     */
    private static PressingBehaviour.Mode petrolpark$addMeshBasin() {
        final ArrayList<PressingBehaviour.Mode> modes = new ArrayList<>(Arrays.asList(PressingBehaviourModeMixin.$VALUES));
        final PressingBehaviour.Mode mode = invokeInit("PETROLPARK_MESH_BASIN", modes.get(modes.size() - 1).ordinal() + 1, 24 / 16f);
        modes.add(mode);
        PressingBehaviourModeMixin.$VALUES = modes.toArray(new PressingBehaviour.Mode[0]);
        return mode;
    };
};
