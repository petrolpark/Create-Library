package com.petrolpark.compat;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.Petrolpark;
import com.petrolpark.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = Petrolpark.MOD_ID)
public class PetrolparkRemaps {
    
    private static final List<Pair<ResourceLocation, ResourceLocation>> BLOCKS = new ArrayList<>();
    private static final List<Pair<ResourceLocation, ResourceLocation>> BLOCK_ENTITY_TYPES = new ArrayList<>();
    private static final List<Pair<ResourceLocation, ResourceLocation>> ITEMS = new ArrayList<>();

    static {
        destroyItem("butter");
        destroyItem("mashed_potato");
        destroyItem("mesh");

        destroyBlockAndItem("mashed_potato_block");
        
        destroyBlockEntityAndItem("extrusion_die");
        destroyBlockEntityAndItem("redstone_programmer");
    };

    private static final void item(ResourceLocation oldRL, String newName) {
        ITEMS.add(Pair.of(oldRL, Petrolpark.asResource(newName)));
    };

    private static final void destroyItem(String name) {
        item(Mods.DESTROY.asResource(name), name);
    };

    private static final void block(ResourceLocation oldRL, String newName) {
        BLOCKS.add(Pair.of(oldRL, Petrolpark.asResource(newName)));
    };

    private static final void destroyBlock(String name) {
        block(Mods.DESTROY.asResource(name), name);
    };

    private static final void blockEntityType(ResourceLocation oldRL, String newName) {
        BLOCK_ENTITY_TYPES.add(Pair.of(oldRL, Petrolpark.asResource(newName)));
    };

    private static final void destroyBlockEntityType(String name) {
        blockEntityType(Mods.DESTROY.asResource(name), name);
    };

    private static final void destroyBlockAndItem(String name) {
        destroyItem(name);
        destroyBlock(name);
    };

    private static final void destroyBlockEntityAndItem(String name) {
        destroyItem(name);
        destroyBlock(name);
        destroyBlockEntityType(name);
    };

    @SubscribeEvent
    public static final void onRegister(RegisterEvent event) {
        Registry<?> registry = event.getRegistry();
        ResourceKey<?> key = registry.key();
        
        List<Pair<ResourceLocation, ResourceLocation>> remaps;
        if (key == Registries.BLOCK) remaps = BLOCKS;
        else if (key == Registries.ITEM) remaps = ITEMS;
        else return;

        remaps.forEach(pair -> registry.addAlias(pair.getFirst(), pair.getSecond()));
    };

    
};
