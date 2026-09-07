package petrolpark.mc.library.core.data.recipe;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

@ParametersAreNonnullByDefault
public abstract class SimpleRecipeBuilder<R extends Recipe<?>, B extends SimpleRecipeBuilder<R, B>> implements RecipeBuilder {

    protected final String prefix;
    protected final R recipe;
    protected String groupName = null;
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public SimpleRecipeBuilder(String prefix, R recipe) {
        this.prefix = prefix.isEmpty() ? "" : prefix + "/";
        this.recipe = recipe;
    };

    public abstract B self();

    @Override
    public B unlockedBy(String name, Criterion<?> criterion) {
        criteria.put(name, criterion);
        return self();
    };

    @Override
    @Deprecated
    public B group(@Nullable String groupName) {
        return self();
    };

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        id = id.withPrefix(prefix);
        final Advancement.Builder advancementBuilder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR);
        criteria.forEach(advancementBuilder::addCriterion);
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    };
    
};
