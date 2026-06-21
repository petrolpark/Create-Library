package petrolpark.mc.library.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

public class DyeHelper {
    
    private static final Map<Block, BiMap<DyeColor, Block>> DYED_BLOCKS = new HashMap<>();

    public static final boolean isDyed(Block block) {
        populatedDyedBlocks();
        return DYED_BLOCKS.containsKey(block);
    };

    @SuppressWarnings("unchecked")
    public static final <T extends Block> Optional<T> get(T block, DyeColor color) {
        populatedDyedBlocks();
        return Optional.ofNullable(DYED_BLOCKS.get(block)).map(map -> (T)map.get(color));
    };

    public static final Optional<BiMap<DyeColor, Block>> getMap(Block block) {
        populatedDyedBlocks();
        return Optional.ofNullable(DYED_BLOCKS.get(block));
    };

    public static final void populatedDyedBlocks() {
        if (!DYED_BLOCKS.isEmpty()) return;
        DYED_BLOCKS.clear();
        final Map<Pattern, BiMap<DyeColor, Block>> byRegex = BuiltInRegistries.BLOCK.holders()
            .filter(holder -> holder.getKey().location().getPath().contains("pink"))
            .collect(Collectors.toMap(
                holder -> {
                    final String path = holder.getKey().location().getPath();
                    final int index = path.indexOf("pink");
                    return Pattern.compile("^" + Pattern.quote(path.substring(0, index)) + "(.+?)" + Pattern.quote(path.substring(index + 4)) + "$");
                },
                holder -> {
                    final BiMap<DyeColor, Block> map = HashBiMap.create();
                    map.put(DyeColor.PINK, holder.value());
                    return map;
                }
            ));
        BuiltInRegistries.BLOCK.holders().forEach(holder -> {
            for (Map.Entry<Pattern, BiMap<DyeColor, Block>> entry : byRegex.entrySet()) {
                final Matcher matcher = entry.getKey().matcher(holder.getKey().location().getPath());
                if (matcher.matches()) {
                    final DyeColor color = DyeColor.byName(matcher.group(1), null);
                    if (color != null && entry.getValue().get(DyeColor.PINK).getClass() == holder.value().getClass()) entry.getValue().put(color, holder.value());
                };
            };
        });
        for (BiMap<DyeColor, Block> map : byRegex.values()) {
            if (map.size() < 16) continue; // If we don't have all 16 (or more) colors
            for (Block block : map.values()) DYED_BLOCKS.put(block, map);
        };
    };
};
