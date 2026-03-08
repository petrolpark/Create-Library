package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber
public class PetrolparkAttributes {
    
    public static final RegistryEntry<Attribute, Attribute>

    SLIPPERINESS = REGISTRATE.attribute("generic.slipperiness", () -> new RangedAttribute("attribute.petrolpark.generic.slipperiness", 1d, 0d, 2048d)
        .setSyncable(true)
        .setSentiment(Attribute.Sentiment.NEUTRAL)
    ),

    EXTRA_HOTBAR_SLOTS = REGISTRATE.attribute("player.extra_hotbar_slots", () -> new RangedAttribute("attribute.petrolpark.player.extra_hotbar_slots", 0d, 0d, 32d)
        .setSyncable(true)
        .setSentiment(Attribute.Sentiment.POSITIVE)
    ),

    EXTRA_INVENTORY_SIZE = REGISTRATE.attribute("player.extra_inventory_size", () -> new RangedAttribute("attribute.petrolpark.player.extra_inventory_size", 0d, 0d, 64d)
        .setSyncable(true)
        .setSentiment(Attribute.Sentiment.POSITIVE)
    ),

    ORE_DISCOVERY_CHANCE = REGISTRATE.attribute("player.ore_discovery_chance", () -> new RangedAttribute("attribute.petrolpark.player.ore_discovery_chance", 0d, 0d, 1d)
        .setSentiment(Attribute.Sentiment.POSITIVE)
    );

    @SubscribeEvent
    public static final void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, EXTRA_HOTBAR_SLOTS);
        event.add(EntityType.PLAYER, EXTRA_INVENTORY_SIZE);
        event.add(EntityType.PLAYER, ORE_DISCOVERY_CHANCE);
    };

    public static final void register() {};
};
