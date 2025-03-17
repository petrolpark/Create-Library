package com.petrolpark.shop.customer;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.PetrolparkLootContextParams;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class EntityCustomer extends AbstractCustomer {

    public final Entity entity;

    public static final EntityCustomer create(IAttachmentHolder attachmentHolder) {
        if (attachmentHolder instanceof Entity entity) return new EntityCustomer(entity);
        throw new IllegalArgumentException(attachmentHolder.toString()+" is not an Entity");
    };

    public EntityCustomer(Entity entity) {
        this.entity = entity;
    };

    @Override
    public Component getName() {
        return entity.getDisplayName();
    };

    @Override
    public void supplyLootParams(LootParams.Builder builder) {
        builder.withParameter(PetrolparkLootContextParams.CUSTOMER_ENTITY, entity);
    };

    public static final IAttachmentSerializer<CompoundTag, EntityCustomer> ATTACHMENT_SERIALIZER = new IAttachmentSerializer<CompoundTag, EntityCustomer>() {

        @Override
        public EntityCustomer read(@Nonnull IAttachmentHolder holder, @Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
            EntityCustomer customer = create(holder);
            customer.deserializeNBT(provider, tag);
            return customer;
        };

        @Override
        public @Nullable CompoundTag write(@Nonnull EntityCustomer attachment, @Nonnull HolderLookup.Provider provider) {
            return attachment.serializeNBT(provider);
        };
        
    }; 
    
};
