package com.petrolpark.team;

import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkAttachmentTypes;
import com.petrolpark.team.data.ITeamDataType;
import com.petrolpark.util.NetworkHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class SinglePlayerTeam extends AbstractTeam {

    public final Player player;

    public static ITeam.Provider provider(Player player) {
        return new Provider(player.getUUID());
    };

    public static final SinglePlayerTeam create(IAttachmentHolder attachmentHolder) {
        if (attachmentHolder instanceof Player player) {
            return new SinglePlayerTeam(player, DataComponentPatch.EMPTY);
        } else throw new IllegalStateException(attachmentHolder.toString() + " is not a Player");
    };

    public SinglePlayerTeam(Player player, DataComponentPatch components) {
        super(components);
        this.player = player;
    };

    @Override
    public ITeam.Provider getProvider() {
        return provider(player);
    };

    @Override
    public boolean isMember(Player player) {
        return player.equals(this.player);
    };

    @Override
    public int memberCount() {
        return 1;
    };

    @Override
    public Stream<String> streamMemberUsernames(Level level) {
        return Stream.of(player.getGameProfile().getName());
    };

    @Override
    public Stream<Player> streamMembers(Level level) {
        return Stream.of(player);
    };

    @Override
    public boolean isAdmin(Player player) {
        return true;
    };

    @Override
    public Component getName(Level level) {
        return player.getDisplayName();
    };

    @Override
    public void setChanged(Level level, ITeamDataType<?> dataType) {
        // Doesn't need to be changed, capabilities are always saved
    };

    @Override
    public void renderIcon(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (player == null) return;
        PlayerFaceRenderer.draw(graphics, mc.getSkinManager().getInsecureSkin(player.getGameProfile()), 0, 0, 16);
    };

    @Override
    public Component getRenderedMemberList(int maxTextWidth) {
        return player.getDisplayName();
    };

    // CAPABILITY

    // @Override
    // public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    //     if (cap == CAPABILITY) return LazyOptional.of(() -> this).cast();
    //     return LazyOptional.empty();
    // };

    // @Override
    // public CompoundTag serializeNBT() {
    //     return saveTeamData(player.level());
    // };

    // @Override
    // public void deserializeNBT(CompoundTag nbt) {
    //     loadTeamData(player.level(), nbt);
    // };

    // TYPE

    public static record Provider(UUID playerUUID) implements ITeam.Provider {

        public static final MapCodec<Provider> CODEC = NetworkHelper.singleFieldMapCodec(UUIDUtil.CODEC, "player", Provider::playerUUID, Provider::new);
        public static final StreamCodec<FriendlyByteBuf, Provider> STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, Provider::playerUUID, Provider::new);

        @Override
        public ITeam provideTeam(Level level) {
            Player player = level.getPlayerByUUID(playerUUID);
            if (player != null) return player.getData(PetrolparkAttachmentTypes.SINGLE_PLAYER_TEAM_COMPONENTS.get());
            return NoTeam.INSTANCE;
        };

        @Override
        public ProviderType getProviderType() {
            return PetrolparkTeamProviderTypes.SINGLE_PLAYER.get();
        };

    };

    public static final IAttachmentSerializer<Tag, SinglePlayerTeam> ATTACHMENT_SERIALIZER = new IAttachmentSerializer<Tag, SinglePlayerTeam>() {

        @Override
        public SinglePlayerTeam read(@Nonnull IAttachmentHolder holder, @Nonnull Tag tag, @Nonnull HolderLookup.Provider provider) {
            if (!(holder instanceof Player player)) throw new IllegalArgumentException(holder.toString() + " is not a Player");
            return new SinglePlayerTeam(player, DataComponentPatch.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow());
        };

        @Override
        public @Nullable Tag write(@Nonnull SinglePlayerTeam attachment, @Nonnull HolderLookup.Provider provider) {
            return attachment.writeDataComponentsTag();
        };

    };
    
};
