package com.petrolpark.registrate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.petrolpark.Petrolpark;
import com.petrolpark.advancement.SimpleAdvancementTrigger;
import com.petrolpark.badge.Badge;
import com.petrolpark.badge.BadgeAdvancementRewards;
import com.petrolpark.badge.BadgeItem;
import com.petrolpark.compat.curios.CuriosSetup;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

public class BadgeBuilder<T extends Badge, P> extends AbstractBuilder<Badge, T, P, BadgeBuilder<T, P>> {
    
    private final NonNullSupplier<T> factory;

    protected ItemEntry<BadgeItem> item;
    protected Ingredient duplicationIngredient;

    public static <T extends Badge, P> BadgeBuilder<T, P> create(PetrolparkRegistrate owner, P parent, String name, BuilderCallback callback, NonNullSupplier<T> factory) {
        return new BadgeBuilder<>(owner, parent, name, callback, factory);
    };

    public BadgeBuilder(PetrolparkRegistrate owner, P parent, String name, BuilderCallback callback, NonNullSupplier<T> factory) {
        super(owner, parent, name, callback, Petrolpark.BADGE_REGISTRY_KEY);
        this.factory = factory;

        duplicationIngredient = Ingredient.EMPTY;
        item = getOwner().item("badge/"+getName(), p -> new BadgeItem(p, () -> this.getEntry()))
            .tab(null)
            .properties(p -> p
                .stacksTo(1)
            ).register();

        CuriosSetup.BADGES.add(item);
    };

    public BadgeBuilder<T, P> duplicationIngredient(Ingredient ingredient) {
        duplicationIngredient = ingredient;
        return this;
    };

    @Override
    protected @NonnullType T createEntry() {
        T badge = factory.get();

        badge.setId(new ResourceLocation(getOwner().getModid(), getName()));

        SimpleAdvancementTrigger advancementTrigger = new SimpleAdvancementTrigger(Petrolpark.asResource("get_badge_"+getOwner().getModid()+"_"+getName()));
        badge.setAdvancementTrigger(advancementTrigger);
        CriteriaTriggers.register(advancementTrigger);

        badge.setBadgeItem(item);

        badge.setDuplicationItem(duplicationIngredient);

        return badge;
    };

    public static Map<ResourceLocation, Advancement.Builder> getAdvancements() {
        Map<ResourceLocation, Advancement.Builder> advancements = new HashMap<>();
        for (Badge badge : Badge.badgeRegistry().getValues()) {
            Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
            advancementBuilder
               .parent(Petrolpark.asResource("badge_root"))
                    // .display(
                    //     badge::getItem,
                    //     badge.getName(),
                    //     badge.getDescription(),
                    //     null,
                    //     FrameType.CHALLENGE,
                    //     false,
                    //     false,
                    //     true
                    // )
                    .rewards(new BadgeAdvancementRewards(badge))
                    .addCriterion("get_badge", badge.advancementTrigger.instance())
                    .requirements(new String[][]{new String[]{"get_badge"}}); 
            advancements.put(new ResourceLocation(badge.getId().getNamespace(), "badge/"+badge.getId().getPath()), advancementBuilder);
        };
        return advancements;
    };

    public static Collection<CraftingRecipe> getExampleDuplicationRecipes() {
        return Badge.badgeRegistry().getValues().stream().map(Badge::getExampleDuplicationRecipe).filter(r -> r != null).toList();
    };
    
};
