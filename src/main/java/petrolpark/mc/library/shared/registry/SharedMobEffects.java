package petrolpark.mc.library.shared.registry;

import static petrolpark.mc.library.Petrolpark.REGISTRATE;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.core.registrate.MobEffectEntry;
import petrolpark.mc.library.core.world.effect.SimpleMobEffect;
import petrolpark.mc.library.core.world.effect.SyncedMobEffect;
import petrolpark.mc.library.registry.PetrolparkAttributes;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.world.effect.CryingMobEffect;
import petrolpark.mc.library.shared.world.effect.HangoverMobEffect;
import petrolpark.mc.library.shared.world.effect.InebriationMobEffect;

public class SharedMobEffects {

    public static final MobEffectEntry<CryingMobEffect> CRYING = REGISTRATE.sharedMobEffect(SharedFeatureFlag.CRYING, "crying", CryingMobEffect::new)
        .category(MobEffectCategory.NEUTRAL)
        .color(0xCBF2F0)
        .register();

    public static final MobEffectEntry<HangoverMobEffect> HANGOVER = REGISTRATE.sharedMobEffect(SharedFeatureFlag.INEBRIATION, "hangover", HangoverMobEffect::new)
        .category(MobEffectCategory.HARMFUL)
        .color(0x59390B)
        .attributes((e, id) -> e
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, id, -0.05f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, id, -0.05f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        ).register();

    public static final MobEffectEntry<InebriationMobEffect> INEBRIATION = REGISTRATE.sharedMobEffect(SharedFeatureFlag.INEBRIATION, "inebriation", InebriationMobEffect::new)
        .category(MobEffectCategory.BENEFICIAL)
        .color(0xE88010)
        .attributes((e, id) -> e
            .addAttributeModifier(PetrolparkAttributes.SLIPPERINESS, id, 0.25f, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, id, 2.0f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, id, 0.5f, AttributeModifier.Operation.ADD_VALUE)
        ).register();
    
    public static final MobEffectEntry<SyncedMobEffect> NUMBNESS = REGISTRATE.mobEffect("numbness", SyncedMobEffect::new)
        .category(MobEffectCategory.HARMFUL)
        .color(0x7A2337)
        .tag(PetrolparkTags.MobEffects.CANCELS_HURT_EFFECTS.tag, PetrolparkTags.MobEffects.PREVENTS_AGGRAVATING.tag)
        .register();

    public static final MobEffectEntry<SimpleMobEffect> MIDAS_TOUCH = REGISTRATE.sharedMobEffect(SharedFeatureFlag.GOLD_CONVERSION, "midas_touch", SimpleMobEffect::new)
        .category(MobEffectCategory.NEUTRAL)
        .color(0xFFD700)
        .register();

    public static final MobEffectEntry<SimpleMobEffect> MINERS_LUCK = REGISTRATE.mobEffect("miners_luck", SimpleMobEffect::new)
        .category(MobEffectCategory.BENEFICIAL)
        .color(0x4E0B60)
        .attributes((e, id) -> e.addAttributeModifier(PetrolparkAttributes.ORE_DISCOVERY_CHANCE, id, 0.2f, AttributeModifier.Operation.ADD_VALUE))
        .register();

    public static final MobEffectEntry<SimpleMobEffect> SLIPPING = REGISTRATE.sharedMobEffect(SharedFeatureFlag.SLIPPING, "slipping", SimpleMobEffect::new)
        .category(MobEffectCategory.HARMFUL)
        .color(0xFFFF00)
        .attributes((e, id) -> e.addAttributeModifier(PetrolparkAttributes.SLIPPERINESS, id, 0.5f, AttributeModifier.Operation.ADD_VALUE))
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
