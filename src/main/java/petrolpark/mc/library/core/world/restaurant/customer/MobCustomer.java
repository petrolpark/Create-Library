package petrolpark.mc.library.core.world.restaurant.customer;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;

public class MobCustomer extends AbstractCustomer {

    public final Entity entity;

    public static final MobCustomer create(IAttachmentHolder attachmentHolder) {
        if (attachmentHolder instanceof Entity entity) return new MobCustomer(entity);
        throw new IllegalArgumentException(attachmentHolder.toString() + " is not an Entity");
    };

    public MobCustomer(Entity entity) {
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

    public static final IAttachmentSerializer<CompoundTag, MobCustomer> ATTACHMENT_SERIALIZER = new IAttachmentSerializer<CompoundTag, MobCustomer>() {

        @Override
        public MobCustomer read(@Nonnull IAttachmentHolder holder, @Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
            MobCustomer customer = create(holder);
            customer.deserializeNBT(provider, tag);
            return customer;
        };

        @Override
        public @Nullable CompoundTag write(@Nonnull MobCustomer attachment, @Nonnull HolderLookup.Provider provider) {
            return attachment.serializeNBT(provider);
        };
        
    }; 
    
};
