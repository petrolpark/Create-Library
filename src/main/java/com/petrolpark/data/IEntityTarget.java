package com.petrolpark.data;

import java.util.Map;

import java.util.HashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public interface IEntityTarget {
    
    public static final Map<ResourceLocation, LootContextParam<? extends Entity>> CUSTOM = new HashMap<>();

    public static void register(LootContextParam<? extends Entity> lootContextParam) {
        CUSTOM.put(lootContextParam.getName(), lootContextParam);
    };

    public Entity get(LootContext context);

    public String name();

    public LootContextParam<? extends Entity> getReferencedParam();

    public static IEntityTarget getByName(String name) {
        try {
            EntityTarget builtInTarget = EntityTarget.getByName(name);
            return Targets.TARGETS.computeIfAbsent(name, s -> new BuiltIn(builtInTarget));
        } catch (IllegalArgumentException e) {
            LootContextParam<? extends Entity> param = CUSTOM.get(ResourceLocation.fromNamespaceAndPath(name));
            if (param != null) return Targets.TARGETS.putIfAbsent(name, new Custom(param));
            throw new IllegalArgumentException("Unknown contextual Entity: " + name);
        }
    };

    static class Targets {
        private static final Map<String, IEntityTarget> TARGETS = new HashMap<>();

        static {
            register(LootContextParams.THIS_ENTITY);
            register(LootContextParams.KILLER_ENTITY);
            register(LootContextParams.LAST_DAMAGE_PLAYER);
        };
    };

    static class BuiltIn implements IEntityTarget {

        public final EntityTarget target;

        public BuiltIn(EntityTarget target) {
            this.target = target;
        };

        @Override
        public Entity get(LootContext context) {
            return context.getParamOrNull(target.getParam());
        };

        @Override
        public String name() {
            return target.getName();
        };

        @Override
        public LootContextParam<? extends Entity> getReferencedParam() {
            return target.getParam();
        };

    };

    static class Custom implements IEntityTarget {

        public final LootContextParam<? extends Entity> param;

        public Custom(LootContextParam<? extends Entity> param) {
            this.param = param;
        };

        @Override
        public Entity get(LootContext context) {
            return context.getParamOrNull(param);
        };

        @Override
        public String name() {
            return param.getName().toString();
        };

        @Override
        public LootContextParam<? extends Entity> getReferencedParam() {
            return param;
        };

    };

};
