package petrolpark.mc.library.core.flags;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

public abstract class Flaggable<OBJECT, OBJECT_STACK> {

    public abstract boolean isFlaggable(OBJECT object);

    public abstract boolean isFlaggableStack(OBJECT_STACK stack);
  
    @Nullable
    public abstract IFlagPole<OBJECT, OBJECT_STACK> getFlagPole(Object stack);

    public final Optional<IFlagPole<OBJECT, OBJECT_STACK>> getFlagPoleOptional(Object stack) {
        return Optional.ofNullable(getFlagPole(stack));
    };

    public abstract Collection<Holder<Flag>> getIntrinsicFlags(OBJECT object);

    public abstract Collection<Holder<Flag>> getShownIfAbsentFlags(OBJECT object);

    public void onTagsLoaded(TagsUpdatedEvent event) {};

    public static class GenericFlaggable extends Flaggable<Object,Object> {

        @Override
        public boolean isFlaggable(Object object) {
            return false;
        };

        @Override
        public boolean isFlaggableStack(Object stack) {
            return false;
        };

        @Override
        public IFlagPole<Object, Object> getFlagPole(Object stack) {
            return null;
        }

        @Override
        public Collection<Holder<Flag>> getIntrinsicFlags(Object object) {
            return Collections.emptySet();
        };

        @Override
        public Collection<Holder<Flag>> getShownIfAbsentFlags(Object object) {
            return Collections.emptySet();
        };

    };
};
