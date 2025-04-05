package com.petrolpark.core.contamination;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.Holder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber
public class IntrinsicContaminants {

    protected static final Map<Object, Set<Holder<Contaminant>>> INTRINSIC_CONTAMINANTS = new HashMap<>();
    protected static final Map<Object, Set<Holder<Contaminant>>> SHOWN_IF_ABSENT_CONTAMINANTS = new HashMap<>();

    public static void clear() {
        INTRINSIC_CONTAMINANTS.clear();
        SHOWN_IF_ABSENT_CONTAMINANTS.clear();  
    };

    protected static <OBJECT> Set<Holder<Contaminant>> get(IContamination<OBJECT, ?> contamination) {
        return Optional.ofNullable(INTRINSIC_CONTAMINANTS.get(contamination.getType())).orElse(Collections.emptySet());
    };

    protected static <OBJECT> Set<Holder<Contaminant>> getShownIfAbsent(IContamination<OBJECT, ?> contamination) {
        return Optional.ofNullable(SHOWN_IF_ABSENT_CONTAMINANTS.get(contamination.getType())).orElse(Collections.emptySet());
    };

    @SubscribeEvent
    public static final void onTagsUpdated(TagsUpdatedEvent event) {
        clear();
        Contaminables.CONTAMINABLES.forEach(contaminable -> {
            INTRINSIC_CONTAMINANTS.putAll(contaminable.getIntrinsicContaminants(event.getRegistryAccess()));
            SHOWN_IF_ABSENT_CONTAMINANTS.putAll(contaminable.getShownIfAbsentContaminants(event.getRegistryAccess()));
        });
    };
    
};
