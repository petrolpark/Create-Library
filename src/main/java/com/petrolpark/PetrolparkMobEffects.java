package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.registrate.MobEffectEntry;
import com.petrolpark.core.world.effect.SimpleMobEffect;
import com.petrolpark.core.world.effect.SyncedMobEffect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

public class PetrolparkMobEffects {
    
    public static final MobEffectEntry<SyncedMobEffect> NUMBNESS = REGISTRATE.mobEffect("numbness", SyncedMobEffect::new)
        .category(MobEffectCategory.HARMFUL)
        .color(0x7A2337)
        .tag(PetrolparkTags.MobEffects.CANCELS_HURT_EFFECTS.tag, PetrolparkTags.MobEffects.PREVENTS_AGGRAVATING.tag)
        .register();

    public static final MobEffectEntry<SimpleMobEffect> MINERS_LUCK = REGISTRATE.mobEffect("miners_luck", SimpleMobEffect::new)
        .category(MobEffectCategory.BENEFICIAL)
        .color(0x4E0B60)
        .attributes((e, id) -> e.addAttributeModifier(PetrolparkAttributes.ORE_DISCOVERY_CHANCE.getDelegate(), id, 0.2f, AttributeModifier.Operation.ADD_VALUE))
        .register();

    public static final MobEffectEntry<SimpleMobEffect> SLIPPING = REGISTRATE.sharedMobEffect(SharedFeatureFlag.SLIPPING, "slipping", SimpleMobEffect::new)
        .category(MobEffectCategory.HARMFUL)
        .color(0xFFFF00)
        .attributes((e, id) -> e.addAttributeModifier(PetrolparkAttributes.SLIPPERINESS.getDelegate(), id, 0.5f, AttributeModifier.Operation.ADD_VALUE))
        .potion(b -> b.duration(3600).amplifier(1))
        .recipe((r, b, e) -> b.potionMixes.add(new PotionBrewing.Mix<>(Potions.AWKWARD, Ingredient.of(PetrolparkTags.Items.SLIPPING_POTION_INGREDIENTS.tag), e.getDelegate())))
        .defaultLong(2.6666667f)
        .build()
        .defaultStrong()
        .build()
        .build()
        .register();

    public static final void register() {};
};
