package petrolpark.mc.library.core.data.advancement.criterion;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Sets;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

@ParametersAreNonnullByDefault
public abstract class AdvancedCriterionTrigger<I extends AdvancedCriterionTrigger.AdvancedInstance> implements CriterionTrigger<I> {
    
    protected final Map<PlayerAdvancements, Set<CriterionTrigger.Listener<I>>> players = new IdentityHashMap<>();

    @Override
    public final void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener<I> listener) {
        players.computeIfAbsent(playerAdvancements, $ -> Sets.newHashSet()).add(listener);
    };

    @Override
    public final void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener<I> listener) {
        final Set<CriterionTrigger.Listener<I>> set = players.get(playerAdvancements);
        if (set != null) {
            set.remove(listener);
            if (set.isEmpty()) {
                players.remove(playerAdvancements);
            };
        };
    };

    @Override
    public final void removePlayerListeners(PlayerAdvancements playerAdvancements) {
        players.remove(playerAdvancements);
    };

    public LootContextParamSet paramSet() {
        return LootContextParamSets.ADVANCEMENT_ENTITY;
    };

    protected final void trigger(ServerPlayer player, Predicate<I> testTrigger) {
        trigger(player, testTrigger, UnaryOperator.identity());
    };

    protected final void trigger(ServerPlayer player, Predicate<I> testTrigger, UnaryOperator<LootParams.Builder> modifyParams) {
        trigger(player, testTrigger, modifyParams, UnaryOperator.identity());
    };

    protected void trigger(ServerPlayer player, Predicate<I> testTrigger, UnaryOperator<LootParams.Builder> modifyParams, UnaryOperator<LootContext.Builder> modifyContext) {
        final PlayerAdvancements advancements = player.getAdvancements();
        final Set<CriterionTrigger.Listener<I>> set = players.get(advancements);
        if (set != null && !set.isEmpty()) {

            final LootParams.Builder lootParamsBuilder = new LootParams.Builder(player.serverLevel())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.ORIGIN, player.position());
            modifyParams.apply(lootParamsBuilder);

            final LootContext.Builder lootContextBuilder = new LootContext.Builder(lootParamsBuilder.create(paramSet()));
            modifyContext.apply(lootContextBuilder);
            final LootContext lootContext = lootContextBuilder.create(Optional.empty());

            for (final CriterionTrigger.Listener<I> listener : set) {
                final I triggerInstance = listener.trigger();
                if (
                    testTrigger.test(triggerInstance) &&
                    triggerInstance.player().map(p -> p.matches(lootContext)).orElse(true)
                ) listener.run(advancements);
            };
        };
    };

    public interface AdvancedInstance extends CriterionTriggerInstance {

        public AdvancedCriterionTrigger<?> trigger();

        public Optional<ContextAwarePredicate> player();

        @Override
        public default void validate(CriterionValidator validator) {
            player().ifPresent(p -> validator.validate(p, trigger().paramSet(), ".player"));
        };
    };
};
