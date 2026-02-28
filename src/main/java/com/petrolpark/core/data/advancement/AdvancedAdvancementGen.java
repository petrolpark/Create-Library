package com.petrolpark.core.data.advancement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.PetrolparkDataMapTypes;
import com.petrolpark.core.world.block.entity.BlockEntityTypeTagProvider;

import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public abstract class AdvancedAdvancementGen implements AdvancementProvider.AdvancementGenerator {

    protected final String modid;

    protected final Map<TagKey<BlockEntityType<?>>, List<ResourceKey<BlockEntityType<?>>>> blockEntityTypeTags = new HashMap<>();

    protected final BlockEntityTypeTagProvider blockEntityTypeTagProvider;
    protected final DataMapProvider dataMapProvider;
    protected final LanguageProvider langProvider;

    protected AdvancedAdvancementGen(GatherDataEvent event, String modid) {
        final DataGenerator generator = event.getGenerator();
		final PackOutput output = generator.getPackOutput();
		final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        this.modid = modid;
        blockEntityTypeTagProvider = new AdvancementBlockEntityTypeTagProvider(output, lookupProvider, modid, existingFileHelper);
        dataMapProvider = new AdvancementDataMapProvider(output, lookupProvider);
        langProvider = new AdvancementLangProvider(output, modid);
    };
    
    protected AdvancedAdvancementGen(String modid, BlockEntityTypeTagProvider blockEntityTypeTagProvider, DataMapProvider dataMapProvider, LanguageProvider langProvider) {
        this.modid = modid;
        this.blockEntityTypeTagProvider = blockEntityTypeTagProvider;
        this.dataMapProvider = dataMapProvider;
        this.langProvider = langProvider;
    };

    public void addProviders(GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
		final PackOutput output = generator.getPackOutput();
		final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new AdvancementProvider(output, lookupProvider, existingFileHelper, Collections.singletonList(this)));
        generator.addProvider(event.includeServer(), blockEntityTypeTagProvider);
        generator.addProvider(event.includeServer(), dataMapProvider);
        generator.addProvider(event.includeClient(), langProvider);
    };

    public AdvancedAdvancementGen.Builder advancement(String name) {
        return new AdvancedAdvancementGen.Builder(name);
    };

    class AdvancementBlockEntityTypeTagProvider extends BlockEntityTypeTagProvider {

        public AdvancementBlockEntityTypeTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, String modid, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, modid, existingFileHelper);
        };

        @Override
        protected void addTags(@Nonnull HolderLookup.Provider provider) {
            blockEntityTypeTags.forEach((key, list) -> tag(key).addAll(list));
        };

    };

    class AdvancementDataMapProvider extends DataMapProvider {

        protected AdvancementDataMapProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider) {
            super(packOutput, lookupProvider);
        };
        
    };

    class AdvancementLangProvider extends LanguageProvider {

        public AdvancementLangProvider(PackOutput output, String modid) {
            super(output, modid, "en_us");
        };

        @Override
        protected void addTranslations() {};

    };

    public class Builder extends Advancement.Builder {

        protected final String name;
        protected final ResourceLocation id;

        protected ItemStack icon = ItemStack.EMPTY;
        protected Component title = Component.literal("Undefined Title");
        protected Component description = Component.literal("Undefined Description");
        protected Optional<ResourceLocation> background = Optional.empty();
        protected AdvancementType type = AdvancementType.TASK;
        protected boolean showToast = true;
        protected boolean announceChat = true;
        protected boolean hidden = false;
        protected boolean generateDisplayInfo = false;
        protected List<ResourceKey<BlockEntityType<?>>> blockEntityTypes = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
            this.id = ResourceLocation.fromNamespaceAndPath(modid, name);
        };

        @SuppressWarnings("unchecked")
        public AdvancedAdvancementGen.Builder forBlockEntity(ResourceKey<BlockEntityType<?>>... blockEntityTypes) {
            for (ResourceKey<BlockEntityType<?>> blockEntityType : blockEntityTypes) this.blockEntityTypes.add(blockEntityType);
            return this;
        };

        public AdvancedAdvancementGen.Builder icon(ItemLike icon) {
            return icon(new ItemStack(icon));
        };

        public AdvancedAdvancementGen.Builder icon(ItemStack icon) {
            this.icon = icon;
            generateDisplayInfo = true;
            return this;
        };

        public AdvancedAdvancementGen.Builder title(String title) {
            final String translationKey = Util.makeDescriptionId("advancement", id);
            langProvider.add(translationKey, title);
            this.title = Component.translatable(translationKey);
            generateDisplayInfo = true;
            return this;
        };

        public AdvancedAdvancementGen.Builder description(String description) {
            final String translationKey = Util.makeDescriptionId("advancement", id) + ".description";
            langProvider.add(translationKey, description);
            this.description = Component.translatable(translationKey);
            generateDisplayInfo = true;
            return this;
        };

        public AdvancedAdvancementGen.Builder background(ResourceLocation background) {
            this.background = Optional.ofNullable(background);
            generateDisplayInfo = true;
            return this;
        };

        public AdvancedAdvancementGen.Builder type(AdvancementType type) {
            this.type = type;
            generateDisplayInfo = true;
            return this;
        };

        public AdvancedAdvancementGen.Builder showToast(boolean showToast) {
            this.showToast = showToast;
            generateDisplayInfo = true;
            return this;
        };

        public AdvancedAdvancementGen.Builder announceChat(boolean announceChat) {
            this.announceChat = announceChat;
            generateDisplayInfo = true;
            return this;
        };

        public AdvancedAdvancementGen.Builder hidden(boolean hidden) {
            this.hidden = hidden;
            generateDisplayInfo = true;
            return this;
        };

        @Override
        public AdvancedAdvancementGen.Builder parent(@Nonnull AdvancementHolder parent) {
            super.parent(parent);
            return this;
        };

        @Override
        public AdvancedAdvancementGen.Builder display(@Nonnull DisplayInfo display) {
            super.display(display);
            icon = display.getIcon();
            title = display.getTitle();
            description = display.getDescription();
            background = display.getBackground();
            type = display.getType();
            showToast = display.shouldShowToast();
            announceChat = display.shouldAnnounceChat();
            hidden = display.isHidden();
            return this;
        };

        @Override
        public AdvancedAdvancementGen.Builder rewards(@Nonnull AdvancementRewards.Builder rewardsBuilder) {
            super.rewards(rewardsBuilder.build());
            return this;
        };

        @Override
        public AdvancedAdvancementGen.Builder rewards(@Nonnull AdvancementRewards rewards) {
            super.rewards(rewards);
            return this;
        };

        @Override
        public AdvancedAdvancementGen.Builder addCriterion(@Nonnull String key, @Nonnull Criterion<?> criterion) {
            super.addCriterion(key, criterion);
            return this;
        };

        @Override
        public AdvancedAdvancementGen.Builder requirements(@Nonnull AdvancementRequirements.Strategy requirementsStrategy) {
            super.requirements(requirementsStrategy);
            return this;
        };

        @Override
        public AdvancedAdvancementGen.Builder requirements(@Nonnull AdvancementRequirements requirements) {
            super.requirements(requirements);
            return this;
        };

        @Override
        public AdvancedAdvancementGen.Builder sendsTelemetryEvent() {
            super.sendsTelemetryEvent();
            return this;
        };

        public AdvancementHolder build() {
            return build(id);
        };

        @Override
        @Deprecated
        public AdvancementHolder build(@Nonnull ResourceLocation id) {
            if (generateDisplayInfo) display(new DisplayInfo(icon, title, description, background, type, showToast, announceChat, hidden));
            
            if (!blockEntityTypes.isEmpty()) {
                final TagKey<BlockEntityType<?>> tag = TagKey.create(Registries.BLOCK_ENTITY_TYPE, id.withPrefix("triggers_advancement/"));
                blockEntityTypeTags.put(tag, blockEntityTypes);
                dataMapProvider.builder(PetrolparkDataMapTypes.BLOCK_ENTITY_ADVANCEMENTS).add(tag, Collections.singletonList(id), false);
            };

            return super.build(id);
        };

        public AdvancementHolder save(@Nonnull Consumer<AdvancementHolder> output) {
            final AdvancementHolder holder = build();
            output.accept(holder);
            return holder;
        };

        public AdvancementHolder save(@Nonnull Consumer<AdvancementHolder> saver, @Nonnull ExistingFileHelper fileHelper) {
            return super.save(saver, id, fileHelper);
        };

        @Override
        @Deprecated
        public AdvancementHolder save(@Nonnull Consumer<AdvancementHolder> output, @Nonnull String id) {
            return super.save(output, id);
        };

        @Override
        @Deprecated
        public AdvancementHolder save(@Nonnull Consumer<AdvancementHolder> saver, @Nonnull ResourceLocation id, @Nonnull ExistingFileHelper fileHelper) {
            return super.save(saver, id, fileHelper);
        };
    };

};
