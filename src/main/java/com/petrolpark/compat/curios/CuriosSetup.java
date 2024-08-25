package com.petrolpark.compat.curios;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.petrolpark.badge.BadgeItem;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.world.item.Item;

/**
 * This class will be loaded without the guarantee that Curios is installed.
 */
public class CuriosSetup {
    
    /**
     * Destroy's alternate Engineer's Goggles
     */
    public static final Set<ItemBuilder<?, ?>> ENGINEERS_GOGGLES = new HashSet<>();

    public static <I extends Item, R, T extends ItemBuilder<I, R>> NonNullFunction<T, T> goggles() {
        return b -> {
            ENGINEERS_GOGGLES.add(b);
            return b;
        };
    };

    /**
     * Any Curio rendered on the Head
     */
    public static final Map<ItemBuilder<?, ?>, HeadwearCurioRenderInfo> RENDERED_ON_HEAD = new HashMap<>();

    public static <I extends Item, R, T extends ItemBuilder<I, R>> NonNullFunction<T, T> renderOnHead() {
        return renderOnHead(HeadwearCurioRenderInfo.defaultInfo());
    };

    public static <I extends Item, R, T extends ItemBuilder<I, R>> NonNullFunction<T, T> renderOnHead(HeadwearCurioRenderInfo info) {
        return b -> {
            RENDERED_ON_HEAD.put(b, info);
            return b;
        };
    };

    public static class HeadwearCurioRenderInfo {

        public static HeadwearCurioRenderInfo defaultInfo() {
            return new HeadwearCurioRenderInfo();
        };
    };

    public static final Set<ItemEntry<? extends BadgeItem>> BADGES = new HashSet<>();
};
