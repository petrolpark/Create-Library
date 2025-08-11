package com.petrolpark.core.team.singleplayer;

import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkAttachmentTypes;
import com.petrolpark.core.team.AbstractTeam;
import com.petrolpark.core.team.ITeam;
import com.petrolpark.core.team.NoTeam;
import com.petrolpark.core.team.PetrolparkTeamProviderTypes;
import com.petrolpark.util.CodecHelper;

import net.createmod.catnip.platform.CatnipServices;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

/**
 * The {@link ITeam} consiting of a single Player.
 */
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

    public static final ITeam get(Player player) {
        return provider(player).provideTeam(player.level());
    };

    protected SinglePlayerTeam(Player player, DataComponentPatch components) {
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
    public Stream<String> streamMemberUsernames() {
        return Stream.of(player.getGameProfile().getName());
    };

    @Override
    @OnlyIn(Dist.DEDICATED_SERVER)
    public Stream<Player> streamMembers() {
        return Stream.of(player);
    };

    @Override
    public boolean isAdmin(Player player) {
        return true;
    };

    @Override
    public Component getName() {
        return player.getName();
    };

    @Override
    public void setChanged(DataComponentPatch patch) {
        if (player instanceof ServerPlayer serverPlayer) CatnipServices.NETWORK.sendToClient(serverPlayer, new SinglePlayerTeamComponentChangedPacket(patch));
    };

    @Override
    public void renderIcon(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (player == null) return;
        PlayerFaceRenderer.draw(graphics, mc.getSkinManager().getInsecureSkin(player.getGameProfile()), 0, 0, 16);
    };

    @Override
    public Component getRenderedMemberList(int maxTextWidth) {
        return player.getName();
    };

    public static record Provider(UUID playerUUID) implements ITeam.Provider {

        public static final MapCodec<Provider> CODEC = CodecHelper.singleFieldMap(UUIDUtil.CODEC, "player", Provider::playerUUID, Provider::new);
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
