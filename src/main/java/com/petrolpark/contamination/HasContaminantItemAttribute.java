package com.petrolpark.contamination;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.PetrolparkItemAttributes;
import com.petrolpark.util.NBTHelper;
import com.simibubi.create.content.logistics.item.filter.attribute.AllItemAttributeTypes;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import com.simibubi.create.content.logistics.item.filter.attribute.attributes.ItemNameAttribute;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiresCreate
public class HasContaminantItemAttribute implements ItemAttribute {

    private @Nullable Contaminant contaminant;

    public HasContaminantItemAttribute(@Nullable Contaminant contaminant) {
        this.contaminant = contaminant;
    };

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return ItemContamination.get(stack).has(contaminant);
    };

    @Override
    public ItemAttributeType getType() {
        return PetrolparkItemAttributes.HAS_CONTAMINANT;
    }

    @Override
    public void save(CompoundTag nbt) {
        if(contaminant == null) return;
        NBTHelper.writeRegistryObject(nbt, "Contaminant", PetrolparkRegistries.Keys.CONTAMINANT, contaminant);
    }

    @Override
    public void load(CompoundTag nbt) {
        if (nbt.contains("Contaminant")) {
            contaminant = NBTHelper.readRegistryObject(nbt, "Contaminant", PetrolparkRegistries.Keys.CONTAMINANT);
        }
    }

    @Override
    public String getTranslationKey() {
        return "has_contaminant";
    };

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{contaminant.getName()};
    };

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new HasContaminantItemAttribute(null);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            IContamination<?, ?> contamination = ItemContamination.get(stack);
            List<ItemAttribute> list = new ArrayList<>(contamination.streamAllContaminants().map(HasContaminantItemAttribute::new).map(ItemAttribute.class::cast).toList());
            IntrinsicContaminants.getShownIfAbsent(contamination).forEach(c -> {list.add(new HasContaminantItemAttribute(c));});
            return list;
        };
    }
    
};
