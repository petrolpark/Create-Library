package com.petrolpark.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.spongepowered.include.com.google.common.base.Strings;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.inventory.ClickType;
import net.neoforged.neoforge.common.Tags;

public class Lang {

    public static final DecimalFormat INT_DF = new DecimalFormat();
    static {
        INT_DF.setMinimumFractionDigits(0);
        INT_DF.setMaximumFractionDigits(0);
    };
    
    public static String asId(String string) {
        return string.toLowerCase(Locale.ROOT);
    };

    public static Component shortList(List<? extends Component> elements, int maxTextWidth, Font font) {
        if (elements.isEmpty()) return Component.translatable("petrolpark.generic.list.none");
        if (elements.size() == 1) return elements.get(0);
        int namedElements = 1;
        Component namedList = elements.get(0), extendedList = namedList, list;
        do {
            list = extendedList;
            Component nextElement = elements.get(namedElements);
            namedElements++;
            if (namedElements < elements.size()) {
                namedList = Component.translatable("petrolpark.generic.list.comma", namedList, nextElement);
                extendedList = Component.translatable("petrolpark.generic.list.and_more", namedList, elements.size() - namedElements);
            } else {
                extendedList = Component.translatable("petrolpark.generic.list.and", namedList, nextElement);
            };
        } while (font.width(extendedList) < maxTextWidth && namedElements < elements.size());
        return list;
    };

    public static String shortList(String[] elements) {
        if (elements.length == 0) return Component.translatable("petrolpark.generic.list.none").getString();
        if (elements.length == 1) return elements[0];
        int namedElements = 1;
        String namedList = elements[0], extendedList = namedList, list;
        do {
            list = extendedList;
            String nextElement = elements[namedElements];
            namedElements++;
            if (namedElements < elements.length) {
                namedList = Component.translatable("petrolpark.generic.list.comma", namedList, nextElement).getString();
                extendedList = Component.translatable("petrolpark.generic.list.and_more", namedList, elements.length - namedElements).getString();
            } else {
                extendedList = Component.translatable("petrolpark.generic.list.and", namedList, nextElement).getString();
            };
        } while (namedElements < elements.length);
        return list;
    };

    protected static Component generic(String keyEnd, Object... translationArgs) {
        return Component.translatable("petrolpark.generic."+keyEnd, translationArgs);
    };

    public static Component none() {
        return generic("list.none");
    };

    public static Component direction(Direction direction) {
        return generic("direction."+direction.getName());
    };

    public static Component clickType(ClickType clickType, int slot) {
        return generic("click_type."+asId(clickType.name()), slot);
    };

    public static Component hand(InteractionHand hand) {
        return generic("hand."+asId(hand.name()));
    };

    public static Component action(ServerboundPlayerActionPacket.Action action, BlockPos pos) {
        return generic("action."+asId(action.name()), pos.toShortString());
    };

    public static Component enabled(boolean enabled) {
        return enabled ? generic("enabled") : generic("disabled");
    };

    public static Component gossipType(GossipType type) {
        return generic("gossip_type."+type.name());
    };

    public static Component tag(TagKey<?> tagKey) {
		String tagTranslationKey = Tags.getTagTranslationKey(tagKey);
		return Component.translatableWithFallback(tagTranslationKey, "#" + tagKey.location());
	};

    public static Component loot(ResourceLocation id) {
        return Component.translatableWithFallback(Util.makeDescriptionId("loot_table", id), "" + id);
    };

    public static Component unknownRange() {
        return generic("range.unknown");
    };

    public static Component range(float min, float max, DecimalFormat df) {
        return range(min, max, false, df);
    };

    public static Component range(float min, float max, boolean approximate, DecimalFormat df) {
        String postfix;
        String[] args;
        if (min == Float.NaN) {
            if (max == Float.NaN) return unknownRange();
            postfix = "range.at_most";
            args = new String[]{df.format(max)};
        } else if (max == Float.NaN) {
            postfix = "range.at_least";
            args = new String[]{df.format(min)};
        } else {
            postfix = "range";
            args = new String[]{df.format(min), df.format(max)};
        }
        if (approximate) postfix += ".approximate";
        return generic(postfix, (Object[])args);
    };

    public static class IndentedTooltipBuilder {

        protected List<Component> components;
        protected int indents = 0;

        public IndentedTooltipBuilder(List<Component> components) {
            this.components = components;
        };

        public IndentedTooltipBuilder indent() {
            indents++;
            return this;
        };

        public IndentedTooltipBuilder unindent() {
            indents--;
            return this;
        };

        public IndentedTooltipBuilder add(Component component) {
            components.add(withIndent(component));
            return this;
        };

        public IndentedTooltipBuilder addAll(Stream<Component> components) {
            this.components.addAll(components.map(this::withIndent).toList());
            return this;
        };

        protected Component withIndent(Component unindentedComponent) {
            return Component.literal(Strings.repeat(" ", indents)).append(unindentedComponent);
        };
    };
};
