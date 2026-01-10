package com.petrolpark.client.creativemodetab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.petrolpark.client.rendering.PetrolparkGuiTexture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * A more versatile Creative Mode Tab that allows you to leave Slots empty and add labels.
 * Initialize with {@link CustomTab.Builder#Builder(Row, int)}.
 */
public class CustomTab extends CreativeModeTab {

    public final ImmutableList<ITabEntry> entries;
    /**
     * A map of slots to entries which should be rendered at that slot
     */
    public final Map<Integer, ITabEntry> renderedEntries;

    protected CustomTab(Builder builder) {
        super(builder);
        entries = ImmutableList.copyOf(builder.entries);
        renderedEntries = new HashMap<>();
    };

    @Override
    public void buildContents(@Nonnull ItemDisplayParameters parameters) {
        List<ItemStack> items = new ArrayList<>(entries.size());
        displayItemsSearchTab = new HashSet<>(entries.size());
        for (ITabEntry entry : entries) {
            entry.addItems(items, parameters, i -> renderedEntries.put(i, entry));
            displayItemsSearchTab.addAll(entry.getItemsToAddToSearch(parameters));
        };
        displayItems = items;
    };

    public static class Builder extends CreativeModeTab.Builder {

        public final List<ITabEntry> entries = new ArrayList<>();

        public Builder(CreativeModeTab.Row row, int column) {
            super(row, column);
            withTabFactory(b -> {
                if (!(b instanceof Builder cb)) throw new IllegalArgumentException("Supplied builder must extend CustomTab.Builder");
                return new CustomTab(cb);
            });
        };

        public Builder add(ITabEntry ...entries) {
            for (ITabEntry entry : entries) this.entries.add(entry);
            return this;
        };

        /**
         * @deprecated Use {@link Builder#add(ITabEntry...)} instead
         */
        @Override
        @Deprecated
        public Builder displayItems(@Nonnull DisplayItemsGenerator displayItemsGenerator) {
            return this;
        };

    };

    public static interface ITabEntry {

        /**
         * How many contiguous slots this entry occupies
         * @return
         */
        public default int getSize() {
            return 1;
        };

        /**
         * Whether this entry should begin on a new line
         * @return
         */
        public default boolean newLine() {
            return false;
        };

        /**
         * @param stacks The ordered list of stacks to which to add this entry
         * @param parameters
         * @param specialRenderLocation If this entry {@link ITabEntry#hasSpecialRendering() has special rendering}, pass this the slot to render it at 
         */
        public default void addItems(List<ItemStack> stacks, ItemDisplayParameters parameters, IntConsumer specialRenderLocation) {
            if (newLine()) stacks.addAll(Collections.nCopies(((9 - (stacks.size() % 9)) % 9), ItemStack.EMPTY));
            if (hasSpecialRendering()) specialRenderLocation.accept(stacks.size());
            stacks.addAll(Collections.nCopies(getSize(), ItemStack.EMPTY));
        };

        public default boolean hasSpecialRendering() {
            return false;
        };

        public default void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {};

        public default Collection<ItemStack> getItemsToAddToSearch(ItemDisplayParameters parameters) {
            return Collections.emptyList();
        };

        public static final ITabEntry EMPTY = new ITabEntry() {};

        public static final ITabEntry LINE_BREAK = new ITabEntry() {

            @Override
            public int getSize() {
                return 0;
            };

            @Override
            public boolean newLine() {
                return true;
            };
        };

        /**
         * A simple ItemStack, like in a normal {@link CreativeModeTab}.
         */
        public static class Item implements ITabEntry {

            public final Supplier<ItemStack> stack;

            public Item(Supplier<ItemStack> stack) {
                this.stack = stack;
            };

            @Override
            public void addItems(List<ItemStack> stacks, ItemDisplayParameters parameters, IntConsumer specialRenderLocation) {
                stacks.add(stack.get());
            };

            @Override
            public Collection<ItemStack> getItemsToAddToSearch(ItemDisplayParameters parameters) {
                return Collections.singleton(stack.get());
            };

        };

        /**
         * Add an ItemStack to this {@link CustomTab}, but only add it to the search bar if the condition passes.
         */
        public static class ConditionalItem extends Item {

            public final Supplier<Boolean> condition;

            public ConditionalItem(Supplier<ItemStack> stack, Supplier<Boolean> condition) {
                super(stack);
                this.condition = condition;
            };

            @Override
            public void addItems(List<ItemStack> stacks, ItemDisplayParameters parameters, IntConsumer specialRenderLocation) {
                if (condition.get()) super.addItems(stacks, parameters, specialRenderLocation);
            };

            @Override
            public Collection<ItemStack> getItemsToAddToSearch(ItemDisplayParameters parameters) {
                if (!condition.get()) return Collections.emptySet();
                return super.getItemsToAddToSearch(parameters);
            };

        };

        /**
         * Add an ItemStack to this {@link CustomTab}, but do not add it to the search bar, as it is already there.
         */
        public static class DuplicateItem extends Item {

            public DuplicateItem(Supplier<ItemStack> stack) {
                super(stack);
            };

            @Override
            public Collection<ItemStack> getItemsToAddToSearch(ItemDisplayParameters parameters) {
                return Collections.emptyList();
            };
        };

        /**
         * Add a caption that takes up a whole row in this {@link CustomTab}.
         */
        public static class Subheading implements ITabEntry {

            public final Component subheading;

            public Subheading(Component subheading) {
                this.subheading = subheading;
            };

            @Override
            public int getSize() {
                return 9;
            };
            
            @Override
            public boolean newLine() {
                return true;
            };

            @Override
            public boolean hasSpecialRendering() {
                return true;
            };

            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                Minecraft mc = Minecraft.getInstance();
                PetrolparkGuiTexture.CREATIVE_MODE_TAB_BLANK_ROW.render(graphics, 0, 0);
                graphics.drawString(mc.font, subheading, 3, 5, 0x5A575A, false);
            };

        };
    };
    
};
