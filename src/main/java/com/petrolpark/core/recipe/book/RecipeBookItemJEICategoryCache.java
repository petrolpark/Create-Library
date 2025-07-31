package com.petrolpark.core.recipe.book;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import com.petrolpark.compat.jei.PetrolparkJEI;

import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class RecipeBookItemJEICategoryCache {

    protected static final int LIFETIME = 1200;
    
    private final Map<RecipeType<?>, Map<RecipeHolder<?>, Entry>> MAP = new HashMap<>();

    public Optional<IRecipeLayoutDrawable<?>> get(RecipeReferenceDataComponent recipeReference, RecipeManager recipeManager) {
        if (recipeReference.jeiRecipeTypeId().isEmpty()) return Optional.empty();
        return get(recipeReference.jeiRecipeTypeId().get(), recipeReference.getRecipeHolder(recipeManager));
    };

    public Optional<IRecipeLayoutDrawable<?>> get(ResourceLocation jeiRecipeTypeId, Optional<RecipeHolder<?>> recipeHolderOp) {
        if (recipeHolderOp.isEmpty()) return Optional.empty();
        Optional<RecipeType<?>> jeiRecipeTypeOp = PetrolparkJEI.JEI_RUNTIME.getRecipeManager().getRecipeType(jeiRecipeTypeId);
        if (jeiRecipeTypeOp.isEmpty()) return Optional.empty();
        RecipeType<?> jeiRecipeType = jeiRecipeTypeOp.get();

        return Optional.ofNullable(MAP.computeIfAbsent(jeiRecipeType, rl -> new HashMap<>()).computeIfAbsent(recipeHolderOp.get(), rh -> 
            getRecipeLayoutDrawable(recipeHolderOp, jeiRecipeType).map(Entry::new).orElse(null)
        )).map(Entry::getRecipeLayout);
    };

    private <R> Optional<IRecipeLayoutDrawable<R>> getRecipeLayoutDrawable(Optional<RecipeHolder<?>> recipeHolderOp, RecipeType<R> recipeType) {
        Optional<R> typedRecipeHolderOp = recipeHolderOp.map(r -> {
            try {
                return recipeType.getRecipeClass().cast(r);
            } catch (ClassCastException e) {
                return null;
            }
        });
        if (typedRecipeHolderOp.isEmpty()) return Optional.empty();

        return PetrolparkJEI.JEI_RUNTIME.getRecipeManager().createRecipeLayoutDrawable(
            PetrolparkJEI.JEI_RUNTIME.getRecipeManager().getRecipeCategory(recipeType),
            typedRecipeHolderOp.get(),
            PetrolparkJEI.JEI_RUNTIME.getJeiHelpers().getFocusFactory().getEmptyFocusGroup()
        );
    };

    private static class Entry {
        final IRecipeLayoutDrawable<?> recipeLayout;
        int ttl = LIFETIME;

        Entry(IRecipeLayoutDrawable<?> recipeLayout) {
            this.recipeLayout = recipeLayout;
        };

        IRecipeLayoutDrawable<?> getRecipeLayout() {
            return recipeLayout;
        };
    };

    @SubscribeEvent
    private final void tick(ClientTickEvent.Pre pre) {
        Iterator<Map<RecipeHolder<?>, Entry>> recipeTypeIterator = MAP.values().iterator();
        while (recipeTypeIterator.hasNext()) {
            Map<RecipeHolder<?>, Entry> recipeMap = recipeTypeIterator.next();
            Iterator<Entry> recipeIterator = recipeMap.values().iterator();
            while (recipeIterator.hasNext()) {
                Entry recipe = recipeIterator.next();
                recipe.getRecipeLayout().tick();
                recipe.ttl--;
                if (recipe.ttl == 0) recipeIterator.remove();
            };
            if (recipeMap.isEmpty()) recipeTypeIterator.remove();
        };
    };
};
