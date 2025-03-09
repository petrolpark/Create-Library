package com.petrolpark.data.advancement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.CriterionTrigger.Listener;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimpleAdvancementTrigger implements CriterionTrigger<SimpleAdvancementTrigger.Instance> {

	public SimpleAdvancementTrigger(ResourceLocation id) {
        this.id = id;
	};

	private final ResourceLocation id;
	protected final Map<PlayerAdvancements, Set<Listener<SimpleAdvancementTrigger.Instance>>> listeners = Maps.newHashMap();

    @Override
    public SimpleAdvancementTrigger.Instance createInstance(JsonObject json, DeserializationContext context) {
        return new Instance();
    };

	public SimpleAdvancementTrigger.Instance instance() {
		return new Instance();
	};
    
    @Override
	public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<SimpleAdvancementTrigger.Instance> listener) {
		listeners.computeIfAbsent(playerAdvancementsIn, k -> new HashSet<>()).add(listener);
	};

	@Override
	public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<SimpleAdvancementTrigger.Instance> listener) {
		Set<Listener<SimpleAdvancementTrigger.Instance>> playerListeners = listeners.get(playerAdvancementsIn);
		if (playerListeners != null) {
			playerListeners.remove(listener);
			if (playerListeners.isEmpty()) listeners.remove(playerAdvancementsIn);
		};
	};

	@Override
	public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	};

	@Override
	public ResourceLocation getId() {
		return id;
	};

	public void trigger(ServerPlayer player) {
		PlayerAdvancements playerAdvancements = player.getAdvancements();
		Set<Listener<SimpleAdvancementTrigger.Instance>> playerListeners = listeners.get(playerAdvancements);
		if (playerListeners != null) playerListeners.forEach(listener -> listener.run(playerAdvancements));
	};

	public class Instance implements CriterionTriggerInstance {

		public Instance() {
			super(getId(), ContextAwarePredicate.ANY);
		}

		@Override
		public void validate(CriterionValidator validator) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'validate'");
		};
	};

};

