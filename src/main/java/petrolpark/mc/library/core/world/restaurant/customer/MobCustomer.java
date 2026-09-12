package petrolpark.mc.library.core.world.restaurant.customer;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.order.IRestaurantOrder;
import petrolpark.mc.library.core.world.restaurant.serving.IServingBlockEntity;
import petrolpark.mc.library.registry.PetrolparkAttachmentTypes;
import petrolpark.mc.library.registry.PetrolparkCustomerProviderTypes;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.registry.SharedMemoryModuleTypes;
import petrolpark.mc.library.util.AiHelper;

@ParametersAreNonnullByDefault
public class MobCustomer extends AbstractCustomer {

    public final LivingEntity entity;

    public static final AbstractCustomer.Factory<MobCustomer> factory(IAttachmentHolder attachmentHolder) {
        if (attachmentHolder instanceof LivingEntity entity) return new MobCustomer.Factory(entity);
        throw new IllegalArgumentException(attachmentHolder.toString() + " is not an Entity");
    };

    public MobCustomer(LivingEntity entity, Holder<Restaurant> restaurant, ITeam.Provider teamProvider, IRestaurantOrder order, long orderTime) {
        super(restaurant, teamProvider, order, orderTime);
        this.entity = entity;
    };

    @Override
    public Component getName() {
        return entity.getDisplayName();
    };

    @Override
    public BlockPos getPosition() {
        return entity.blockPosition();
    };

    @Override
    public <BE extends BlockEntity & IServingBlockEntity> void notifyOfServing(BE be) {
        final Level level = be.getLevel();
        if (SharedFeatureFlag.RESTAURANT_SEATING.enabled() && level != null && entity.getBrain().checkMemory(SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get(), MemoryStatus.VALUE_ABSENT))
            entity.getBrain().setMemory(SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get(), GlobalPos.of(level.dimension(), be.getBlockPos()));
    };

    @Override
    public void cancelOrder(ServerLevel level, Player player) {
        super.cancelOrder(level, player);
        if (SharedFeatureFlag.RESTAURANT_SEATING.enabled()) {
            if (entity.getBrain().checkMemory(SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get(), MemoryStatus.VALUE_PRESENT))
                entity.getBrain().eraseMemory(SharedMemoryModuleTypes.RESTAURANT_SERVING_POS.get());
            if (entity.getBrain().checkMemory(SharedMemoryModuleTypes.SEAT_POS.get(), MemoryStatus.VALUE_PRESENT)) {
                AiHelper.releasePoi(level, entity, SharedMemoryModuleTypes.SEAT_POS.get());
                entity.getBrain().eraseMemory(SharedMemoryModuleTypes.SEAT_POS.get());
            };
        };
        entity.removeData(PetrolparkAttachmentTypes.ENTITY_CUSTOMER);
    };

    @Override
    public void supplyLootParams(ServerLevel level, LootParams.Builder builder) {
        super.supplyLootParams(level, builder);
        builder.withOptionalParameter(PetrolparkLootContextParams.CUSTOMER_ENTITY, entity);
    };

    @Override
    public void tickWhileOrderItemHeld(ItemStack stack, Level level, Player player, int slotId) {
        entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 2, 0, true, false));
    };

    @Override
    public @Nonnull MobCustomer.Provider getProvider() {
        return new MobCustomer.Provider(entity.getId(), entity.getUUID(), order.id());
    };

    public record Factory(LivingEntity entity) implements AbstractCustomer.Factory<MobCustomer> {
        
        @Override
        public MobCustomer create(Holder<Restaurant> restaurant, ITeam.Provider teamProvider, IRestaurantOrder order, long orderTime) {
            return new MobCustomer(entity(), restaurant, teamProvider, order, orderTime);
        };
    };

    public static final IAttachmentSerializer<CompoundTag, ICustomer> ATTACHMENT_SERIALIZER = new IAttachmentSerializer<>() {

        @Override
        public ICustomer read(@Nonnull IAttachmentHolder holder, @Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
            return AbstractCustomer.deserializeNBT(provider, tag, factory(holder));
        };

        @Override
        public @Nullable CompoundTag write(@Nonnull ICustomer attachment, @Nonnull HolderLookup.Provider provider) {
            if (!(attachment instanceof MobCustomer customer)) return null;
            try {
                return customer.serializeNBT(provider);
            } catch (IllegalStateException e) {
                return null;
            }
        };
        
    };

    public static final AttachmentSyncHandler<ICustomer> ATTCHMENT_SYNC_HANDLER = new AttachmentSyncHandler<>() {

        @Override
        public void write(RegistryFriendlyByteBuf buf, ICustomer attachment, boolean initialSync) {
            if (attachment instanceof MobCustomer customer) {
                buf.writeBoolean(true);
                customer.writeBuffer(buf);
            } else
                buf.writeBoolean(false);
        };

        @Override
        public @Nullable ICustomer read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable ICustomer previousValue) {
            return buf.readBoolean() ? AbstractCustomer.readBuffer(buf, factory(holder)) : null;
        };
        
    };

    public record Provider(int entityId, UUID entityUuid, int orderId) implements ICustomer.Provider {

        public static final MapCodec<MobCustomer.Provider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("entity_id").forGetter(MobCustomer.Provider::entityId),
            UUIDUtil.CODEC.fieldOf("entity_uuid").forGetter(MobCustomer.Provider::entityUuid),
            Codec.INT.fieldOf("order_id").forGetter(MobCustomer.Provider::orderId)
        ).apply(instance, MobCustomer.Provider::new));

        public static final StreamCodec<ByteBuf, MobCustomer.Provider> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MobCustomer.Provider::entityId,
            UUIDUtil.STREAM_CODEC, MobCustomer.Provider::entityUuid,
            ByteBufCodecs.INT, MobCustomer.Provider::orderId,
            MobCustomer.Provider::new
        );

        @Override
        public ICustomer provideCustomer(Level level) {
            final Entity entity = level instanceof ServerLevel serverLevel ? serverLevel.getEntity(entityUuid()) : level.getEntity(entityId());
            if (!(entity instanceof LivingEntity && entity.getUUID().equals(entityUuid()))) return ICustomer.none();
            final ICustomer customer = entity.getData(PetrolparkAttachmentTypes.ENTITY_CUSTOMER);
            return !customer.isNone() && customer.getOrder().id() == orderId() ? customer : ICustomer.none();
        };

        @Override
        public ICustomer.Provider getUpdated(Level level) {
            if (!(level instanceof ServerLevel serverLevel)) return this;
            if (!(serverLevel.getEntity(entityUuid()) instanceof LivingEntity entity)) return ICustomer.none();
            if (entity.getId() != entityId()) return new MobCustomer.Provider(entity.getId(), entityUuid(), orderId());
            return this;
        };

        @Override
        public ICustomer.ProviderType getProviderType() {
            return PetrolparkCustomerProviderTypes.MOB.get();
        };
    
    };
    
};
