package com.petrolpark.core.data;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public interface IEntityTarget extends StringRepresentable {
    
    public static final Map<ResourceLocation, LootContextParam<? extends Entity>> CUSTOM = new HashMap<>();

    public static final Codec<IEntityTarget> CODEC = Codec.stringResolver(IEntityTarget::getSerializedName, IEntityTarget::getByName);

    public static final IEntityTarget CONTEXT_THIS = Targets.TARGETS.computeIfAbsent(EntityTarget.THIS.name(), s -> new BuiltIn(EntityTarget.THIS));

    public static void register(LootContextParam<? extends Entity> lootContextParam) {
        CUSTOM.put(lootContextParam.getName(), lootContextParam);
    };

    public Component getName();

    public static Component getName(LootContextParam<? extends Entity> param) {
        return Component.translatable(Util.makeDescriptionId("loot_context_param", param.getName()));
    };

    public Entity get(LootContext context);

    public LootContextParam<? extends Entity> getReferencedParam();

    public static IEntityTarget getByName(String name) {
        try {
            EntityTarget builtInTarget = EntityTarget.getByName(name);
            return Targets.TARGETS.computeIfAbsent(name, s -> new BuiltIn(builtInTarget));
        } catch (IllegalArgumentException e) {
            LootContextParam<? extends Entity> param = CUSTOM.get(ResourceLocation.parse(name));
            if (param != null) return Targets.TARGETS.putIfAbsent(name, new Custom(param));
            throw new IllegalArgumentException("Unknown contextual Entity: " + name);
        }
    };

    static class Targets {
        private static final Map<String, IEntityTarget> TARGETS = new HashMap<>();

        static {
            register(LootContextParams.THIS_ENTITY);
            register(LootContextParams.ATTACKING_ENTITY);
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
        public Component getName() {
            return IEntityTarget.getName(target.getParam());
        };

        @Override
        public String getSerializedName() {
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
        public Component getName() {
            return IEntityTarget.getName(param);
        };

        @Override
        public String getSerializedName() {
            return param.getName().toString();
        };

        @Override
        public LootContextParam<? extends Entity> getReferencedParam() {
            return param;
        };

    };

};
