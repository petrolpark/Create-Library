package com.petrolpark.core.registrate.builder;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.EntityBuilder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;

public class PetrolparkEntityBuilder<T extends Entity, P> extends EntityBuilder<T, P> {

    public PetrolparkEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityFactory<T> factory, MobCategory classification) {
        super(owner, parent, name, callback, factory, classification);
    };
    
};
