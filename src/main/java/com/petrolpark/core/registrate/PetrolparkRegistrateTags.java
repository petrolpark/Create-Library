package com.petrolpark.core.registrate;

import java.util.Arrays;
import java.util.function.Predicate;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkItems;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.RequiresCreate;
import com.petrolpark.core.badge.BadgeItem;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.data.TagGen.CreateTagAppender;
import com.simibubi.create.foundation.data.TagGen.CreateTagsProvider;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.Tags;

@RequiresCreate
public class PetrolparkRegistrateTags {
    
    public static final void addGenerators() {
        Petrolpark.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, PetrolparkRegistrateTags::genItemTags);
    };

    @SuppressWarnings("deprecation")
    private static final void genItemTags(RegistrateTagsProvider<Item> provIn) {
        final CreateTagsProvider<Item> prov = new CreateTagsProvider<>(provIn, Item::builtInRegistryHolder);
        final CreateTagAppender<Item> contaminableTagAppender = prov.tag(PetrolparkTags.Items.CONTAMINABLE.tag);
        
        tagAllNonBlocksAndEntities(contaminableTagAppender, ResourceLocation.DEFAULT_NAMESPACE,
            Items.AIR,
            Items.SADDLE,
            Items.LEAD,
            Items.NAME_TAG,
            Items.KNOWLEDGE_BOOK,
            Items.DEBUG_STICK
        );
        contaminableTagAppender
            .add(Items.STRING)
            .add(Items.NETHER_WART)
            .addTag(Tags.Items.SHULKER_BOXES)
            .addTag(Tags.Items.SEEDS);
        optionalTagAllNonBlocksAndEntities(contaminableTagAppender, Petrolpark.MOD_ID, PetrolparkItems.MENU.get(), PetrolparkItems.RECIPE_BOOK.get());
        optionalTagAllNonBlocksAndEntities(contaminableTagAppender, Create.ID);
        optionalTagAll(contaminableTagAppender, Create.ID, item -> !(item instanceof BlockItem blockItem && blockItem.getBlock() instanceof KineticBlock));
    };

    public static final void optionalTagAllNonBlocksAndEntities(TagAppender<Item> tag, String namespace, Item ... exclusions) {
        optionalTagAll(tag, namespace, item ->
            item instanceof BlockItem
            || item instanceof PackageItem
            || item instanceof BadgeItem
            || Arrays.stream(exclusions).anyMatch(item::equals)
        );
    };

    public static final void optionalTagAll(TagAppender<Item> tag, String namespace, Predicate<Item> exclude) {
        BuiltInRegistries.ITEM.holders()
            .filter(holder -> !exclude.test(holder.value()))
            .map(Holder::getKey)
            .map(ResourceKey::location)
            .filter(id -> id.getNamespace().equals(namespace))
            .forEach(tag::addOptional);
    };

    public static final void tagAllNonBlocksAndEntities(TagAppender<Item> tag, String namespace, Item ... exclusions) {
        tagAll(tag, namespace, item ->
            item instanceof BlockItem
            || item instanceof MinecartItem
            || item instanceof BoatItem
            || item instanceof SpawnEggItem
            || Arrays.stream(exclusions).anyMatch(item::equals)
        );
    };

    public static final void tagAll(TagAppender<Item> tag, String namespace, Predicate<Item> exclude) {
        BuiltInRegistries.ITEM.holders()
            .filter(holder -> !exclude.test(holder.value()))
            .map(Holder::getKey)
            .filter(key -> key.location().getNamespace().equals(namespace))
            .forEach(tag::add);
    };
};
