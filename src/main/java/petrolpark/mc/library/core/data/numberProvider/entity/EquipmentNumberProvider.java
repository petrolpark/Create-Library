package petrolpark.mc.library.core.data.numberProvider.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import petrolpark.mc.library.core.data.numberProvider.ContextToolNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.NumberEstimate;
import petrolpark.mc.library.core.data.numberProvider.itemStack.ItemStackNumberProvider;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;

/**
 * <p>{@code petrolpark:equipment}</p>
 * 
 * Get a {@link ItemStackNumberProvider value} of an equipped Item.
 * 
 * Arguments:
 * <ul>
 * <li> {@code slot} - Any {@link EquipmentSlot#getName() Equipment Slot name}
 * <li> {@code value} - An {@link ItemStackNumberProvider} to call on the equipped Item Stack
 * </ul>
 * 
 * @author petrolpark
 * 
 * @see ContextToolNumberProvider Getting the value from the mainhand Item directly from the LootContext
 */
@ParametersAreNonnullByDefault
public record EquipmentNumberProvider(EquipmentSlot slot, ItemStackNumberProvider value) implements EntityNumberProvider {

    public static final MapCodec<EquipmentNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StringRepresentable.fromEnum(EquipmentSlot::values).optionalFieldOf("slot", EquipmentSlot.MAINHAND).forGetter(EquipmentNumberProvider::slot),
        ItemStackNumberProvider.CODEC.fieldOf("value").forGetter(EquipmentNumberProvider::value)
    ).apply(instance, EquipmentNumberProvider::new));

    @Override
    public float getFloat(Entity entity, LootContext lootContext) {
        if (entity instanceof LivingEntity livingEntity) return value.getFloat(livingEntity.getItemBySlot(slot), lootContext);
        return 0f;
    };

    @Override
    public int getInt(Entity entity, LootContext lootContext) {
        if (entity instanceof LivingEntity livingEntity) return value.getInt(livingEntity.getItemBySlot(slot), lootContext);
        return 0;
    };

    @Override
    public float getMaxFloat(Entity entity, LootContext lootContext) {
        if (entity instanceof LivingEntity livingEntity) return value.getMaxFloat(livingEntity.getItemBySlot(slot), lootContext);
        return 0f;
    };

    @Override
    public NumberEstimate getEstimate() {
        return value().getEstimate();
    };

    @Override
    public LootEntityNumberProviderType getEntityNumberProviderType() {
        return PetrolparkNumberProviderTypes.EQUIPMENT.get();
    };

    @Override
    public void validate(ValidationContext context) {
        EntityNumberProvider.super.validate(context);
        value().validate(context.forChild(".equipment"));
    };
    
};
