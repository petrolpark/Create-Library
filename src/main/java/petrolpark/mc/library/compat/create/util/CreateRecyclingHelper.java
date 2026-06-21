package petrolpark.mc.library.compat.create.util;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import petrolpark.mc.library.core.world.item.recycling.RecyclingOutput;
import petrolpark.mc.library.core.world.item.recycling.RecyclingOutputs;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;

public class CreateRecyclingHelper {

    public static final List<ProcessingOutput> asProcessingOutputs(RecyclingOutputs recyclingOutputs) {
        return streamAsProcessingOutputs(recyclingOutputs).toList();
    };
  
    public static final Stream<ProcessingOutput> streamAsProcessingOutputs(RecyclingOutputs recyclingOutputs) {
        return recyclingOutputs.stream().flatMap(CreateRecyclingHelper::streamAsProcessingOutput);
    };

    public static final Stream<ProcessingOutput> streamAsProcessingOutput(RecyclingOutput recyclingOutput) {
        final int stackSize = recyclingOutput.getItem().getMaxStackSize();
        final int stacks = (int)((long)recyclingOutput.getExpectedCount() / stackSize);
        Stream<ProcessingOutput> stream = IntStream.range(0, stacks).mapToObj($ -> new ProcessingOutput(recyclingOutput.getItem().getItem(), stackSize, 1f));
        final double remainder = recyclingOutput.getExpectedCount() - stacks * stackSize;
        final double fractionRemainder = remainder - (long)remainder;
        if ((long)remainder > 0) stream = Stream.concat(stream, Stream.of(new ProcessingOutput(recyclingOutput.getItem().getItem(), (int)remainder, 1f)));
        if (fractionRemainder > 0d) stream = Stream.concat(stream, Stream.of(new ProcessingOutput(recyclingOutput.getItem(), (float)fractionRemainder)));
        return stream;
    };
};
