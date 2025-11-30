package com.petrolpark.compat.create.common.redstone.programmer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlockItem.ItemStackRedstoneProgram;

import net.createmod.catnip.data.WorldAttached;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber
public class RedstoneProgrammerItemHandler {

    public static final WorldAttached<Map<UUID, ItemStackRedstoneProgram>> PROGRAMS = new WorldAttached<>(level -> new HashMap<>());
    public static final int TIMEOUT = 30;

    public static void tick(LevelAccessor level) {
        final Map<UUID, ItemStackRedstoneProgram> map = PROGRAMS.get(level);
        for (Iterator<Map.Entry<UUID, ItemStackRedstoneProgram>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
            final Map.Entry<UUID, ItemStackRedstoneProgram> entry = iterator.next();
            final ItemStackRedstoneProgram program = entry.getValue();
            program.ttl--;
            if (!program.shouldTransmit()) {
                program.unload();
                iterator.remove();
            };
        };
    };

    @SubscribeEvent
    public static final void onLevelTick(LevelTickEvent.Post event) {
        tick(event.getLevel());
    };
    
};
