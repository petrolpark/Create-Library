package petrolpark.mc.library.util;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.spongepowered.include.com.google.common.base.Strings;

import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.Tags;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.flags.IFlagPole;

public class Lang {

    public static final DecimalFormat INT_DF = new DecimalFormat();
    static {
        INT_DF.setMinimumFractionDigits(0);
        INT_DF.setMaximumFractionDigits(0);
    };

    public static final DecimalFormat ONE_DP_DF = new DecimalFormat();
    static {
        ONE_DP_DF.setMinimumFractionDigits(1);
        ONE_DP_DF.setMaximumFractionDigits(1);
    };

    public static final DecimalFormat TWO_DP_DF = new DecimalFormat();
    static {
        TWO_DP_DF.setMinimumFractionDigits(2);
        TWO_DP_DF.setMaximumFractionDigits(2);
    }
    
    public static String asId(String string) {
        return string.toLowerCase(Locale.ROOT);
    };

    public static String shorten(String string, Font font, int maxWidth) {
        if (font.width(string) <= maxWidth) return string;
        if (string.isBlank()) return "";
        String elipses = "...";
        int elipsesWidth = font.width(elipses);
        while (font.width(string) > maxWidth - elipsesWidth || string.charAt(string.length() - 1) == ' ') {
            string = string.substring(0, string.length() - 1);
            if (string.isBlank()) return "";
        };
        string += elipses;
        return string;
    };

    public static Component shorten(Component component, Font font, int maxWidth) {
        return Component.literal(shorten(component.getString(), font, maxWidth)).withStyle(component.getStyle());
    };

    public static final String prependPath(String prefix, String path) {
        final int index = path.lastIndexOf('/');
        if (index == -1) return prefix + path;
        return path.substring(0, index + 1) + prefix + path.substring(index + 1);
    };

    public static Component shortList(List<? extends Component> elements, int maxTextWidth) {
        return shortList(elements, maxTextWidth, Minecraft.getInstance().font);
    };

    public static Component shortList(List<? extends Component> elements, int maxTextWidth, Font font) {
        if (elements.isEmpty()) return Component.translatable(Petrolpark.translationKey("generic.list.none"));
        if (elements.size() == 1) return elements.get(0);
        int namedElements = 1;
        Component namedList = elements.get(0), extendedList = namedList, list;
        do {
            list = extendedList;
            Component nextElement = elements.get(namedElements);
            namedElements++;
            if (namedElements < elements.size()) {
                namedList = Component.translatable(Petrolpark.translationKey("generic.list.comma"), namedList, nextElement);
                extendedList = Component.translatable(Petrolpark.translationKey("generic.list.and_more"), namedList, elements.size() - namedElements);
            } else {
                extendedList = Component.translatable(Petrolpark.translationKey("generic.list.and"), namedList, nextElement);
            };
        } while (font.width(extendedList) < maxTextWidth && namedElements < elements.size());
        list = extendedList;
        return list;
    };

    public static String shortList(String[] elements) {
        if (elements.length == 0) return Component.translatable(Petrolpark.translationKey("generic.list.none")).getString();
        if (elements.length == 1) return elements[0];
        int namedElements = 1;
        String namedList = elements[0], extendedList = namedList, list;
        do {
            list = extendedList;
            String nextElement = elements[namedElements];
            namedElements++;
            if (namedElements < elements.length) {
                namedList = Component.translatable(Petrolpark.translationKey("generic.list.comma"), namedList, nextElement).getString();
                extendedList = Component.translatable(Petrolpark.translationKey("generic.list.and_more"), namedList, elements.length - namedElements).getString();
            } else {
                extendedList = Component.translatable(Petrolpark.translationKey("generic.list.and"), namedList, nextElement).getString();
            };
        } while (namedElements < elements.length);
        list = extendedList;
        return list;
    };

    public static final void addFlags(Consumer<Component> tooltip, IFlagPole<?, ?> flags) {
        addFlags(tooltip, flags, false);
    };

    public static final void addFlags(Consumer<Component> tooltip, IFlagPole<?, ?> flags, boolean addBlankLine) {
        final List<Component> flagComponents = Stream.concat(flags.streamShownFlags().map(Flag::getNameColored), flags.streamShownIfAbsentFlags().map(Flag::getAbsentNameColored)).toList();
        if (flagComponents.isEmpty()) return;
        if (addBlankLine) tooltip.accept(Component.literal(" "));
        flagComponents.forEach(tooltip);
    };

    public static final LangBuilder appendFlags(LangBuilder builder, IFlagPole<?, ?> flags) {
        final List<Component> flagComponents = Stream.concat(flags.streamShownFlags().map(Flag::getNameColored), flags.streamShownIfAbsentFlags().map(Flag::getAbsentNameColored)).toList();
        if (flagComponents.isEmpty()) return builder;
        builder.add(Component.literal(" ("));
        for (int i = 0; i < flagComponents.size(); i++) {
            builder.add(flagComponents.get(i));
            if (i != flagComponents.size() - 1) builder.add(Component.literal(", "));
        };
        return builder.add(Component.literal(")"));
    };

    public static final MutableComponent translate(String keyEnd, Object ... args) {
        return Component.translatable(Petrolpark.translationKey(keyEnd), args);
    };

    public static final LangBuilder builder() {
        return new LangBuilder(Petrolpark.MOD_ID);
    };

    public static final String genericTranslationKey(String keyEnd) {
        return Petrolpark.translationKey("generic." + keyEnd);
    };

    public static final String mathTranslationKey(String key) {
        return genericTranslationKey("math." + key);
    };

    public static Component generic(String keyEnd, Object... translationArgs) {
        return Component.translatable(genericTranslationKey(keyEnd), translationArgs);
    };

    public static Component none() {
        return generic("list.none");
    };

    public static Component rarity(Rarity rarity) {
        return generic("rarity."+rarity.getSerializedName());
    };

    public static Component axis(Axis axis) {
        return generic("axis."+axis.getName());
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

    public static final Component loot(ResourceLocation id) {
        return Component.translatableWithFallback(Util.makeDescriptionId("loot_table", id), "" + id);
    };

    public static final Component unknownRange() {
        return generic("range.unknown");
    };

    public static final Component range(float min, float max, DecimalFormat df) {
        return range(min, max, false, df);
    };

    public static final Component range(float min, float max, boolean approximate, DecimalFormat df) {
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

    public static final Component rangeWorded(float min, float max, boolean inverse, DecimalFormat df) {
        String postfix;
        String[] args;
        if (min == Float.NaN) {
            if (max == Float.NaN) return unknownRange();
            postfix = inverse ? "range.at_least.worded" : "range.at_most.worded";
            args = new String[]{df.format(max)};
        } else if (max == Float.NaN) {
            postfix = inverse ? "range.at_most.worded" : "range.at_least.worded";
            args = new String[]{df.format(min)};
        } else {
            postfix = inverse ? "range.outside.worded" : "range.worded";
            args = new String[]{df.format(min), df.format(max)};
        }
        return generic(postfix, (Object[])args);
    };

    public static final Collector<Component, MutableComponent, Component> toComponent() {
        return toComponent(Component.literal(" "));
    };

    private static final Set<Collector.Characteristics> COMPONENT_COLLECTOR_CHARACTERISTICS = Set.of(Collector.Characteristics.IDENTITY_FINISH);

    public static Collector<Component, MutableComponent, Component> toComponent(Component joiner) {
        return new Collector<Component,MutableComponent,Component>() {

            @Override
            public BiConsumer<MutableComponent, Component> accumulator() {
                return (mutableComponent, component) -> mutableComponent.append(joiner).append(component);
            };

            @Override
            public Set<Characteristics> characteristics() {
                return COMPONENT_COLLECTOR_CHARACTERISTICS;
            };

            @Override
            public BinaryOperator<MutableComponent> combiner() {
                return (component1, component2) -> component1.append(joiner).append(component2);
            };

            @Override
            public Function<MutableComponent, Component> finisher() {
                return (component) -> (Component)component;
            };

            @Override
            public Supplier<MutableComponent> supplier() {
                return Component::empty;
            };

        };
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
