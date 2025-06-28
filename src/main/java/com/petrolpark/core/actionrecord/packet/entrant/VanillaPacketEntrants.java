package com.petrolpark.core.actionrecord.packet.entrant;

import static com.petrolpark.core.actionrecord.packet.entrant.PacketEntrants.register;
import static com.petrolpark.core.actionrecord.packet.entrant.PacketEntrants.registerBoolean;
import static com.petrolpark.core.actionrecord.packet.entrant.PacketEntrants.registerSimple;
import static com.petrolpark.core.actionrecord.packet.entrant.PacketEntrants.registerSingleTranslationArg;

import java.text.DecimalFormat;

import com.petrolpark.util.Lang;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.common.CommonPacketTypes;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Items;

public class VanillaPacketEntrants {

    public static final DecimalFormat DF = new DecimalFormat();
    static {
        DF.setMinimumFractionDigits(0);
        DF.setMaximumFractionDigits(0);
    };
    
    public static final IVanillaPacketEntrant<?, ?>
    
    CUSTOM = register(CommonPacketTypes.SERVERBOUND_CUSTOM_PAYLOAD, new ServerboundCustomPacketEntrant()),

    CONTAINER_BUTTON_CLICK = registerSingleTranslationArg(GamePacketTypes.SERVERBOUND_CONTAINER_BUTTON_CLICK, ServerboundContainerButtonClickPacket::buttonId),
    CONTAINER_CLICK = register(GamePacketTypes.SERVERBOUND_CONTAINER_CLICK, new ContainerClickPacketEntrant()),
    CONTAINER_CLOSE = registerSimple(GamePacketTypes.CLIENTBOUND_CONTAINER_CLOSE),
    CONTAINER_SLOT_STATE_CHANGED = registerSimple(GamePacketTypes.SERVERBOUND_CONTAINER_SLOT_STATE_CHANGED, p -> new Object[]{p.slotId(), Lang.enabled(p.newState())}),
    EDIT_BOOK = registerSimple(GamePacketTypes.SERVERBOUND_EDIT_BOOK, p -> new Object[]{p.title().<FormattedText>map(Component::literal).orElse(Items.WRITTEN_BOOK.getDescription()), p.slot()}),
    //TODO ServerboundInteractionPacket
    //TODO all the other movement packets
    MOVE_PLAYER_STATUS_ONLY = registerBoolean(GamePacketTypes.SERVERBOUND_MOVE_PLAYER_STATUS_ONLY, ServerboundMovePlayerPacket.StatusOnly::isOnGround),
    //TODO vehicle movement
    PADDLE_BOAT = registerSimple(GamePacketTypes.SERVERBOUND_PADDLE_BOAT),
    PICK_ITEM = registerSingleTranslationArg(GamePacketTypes.SERVERBOUND_PICK_ITEM, ServerboundPickItemPacket::getSlot),
    PLACE_RECIPE = registerSimple(GamePacketTypes.SERVERBOUND_PLACE_RECIPE, p -> new Object[0], p -> new Object[]{p.getRecipe().toString()}),
    FLY = registerBoolean(GamePacketTypes.SERVERBOUND_PLAYER_ABILITIES, ServerboundPlayerAbilitiesPacket::isFlying),
    PLAYER_ACTION = register(GamePacketTypes.SERVERBOUND_PLAYER_ACTION, new PlayerActionPacketEntrant()),
    //TODO ServerboundPlayerInputPacket
    RENAME_ITEM = registerSingleTranslationArg(GamePacketTypes.SERVERBOUND_RENAME_ITEM, ServerboundRenameItemPacket::getName),
    SELECT_TRADE = registerSingleTranslationArg(GamePacketTypes.SERVERBOUND_SELECT_TRADE, ServerboundSelectTradePacket::getItem),
    SELECT_BEACON = registerSimple(GamePacketTypes.SERVERBOUND_SET_BEACON, p -> new Object[]{p.primary().map(Holder::value).map(MobEffect::getDisplayName).orElse(Lang.none()), p.secondary().map(Holder::value).map(MobEffect::getDisplayName).orElse(Lang.none())}),
    SET_CARRIED_ITEM = registerSingleTranslationArg(GamePacketTypes.SERVERBOUND_SET_CARRIED_ITEM, p -> p.getSlot() + 1),
    SET_CREATIVE_MODE_SLOT = registerSimple(GamePacketTypes.SERVERBOUND_SET_CREATIVE_MODE_SLOT, p -> new Object[]{p.slotNum(), p.itemStack().getDisplayName()}),
    SIGN_UPDATE = registerSimple(GamePacketTypes.SERVERBOUND_SIGN_UPDATE, p -> new Object[]{p.getLines()[0], p.getLines()[1], p.getLines()[2], p.getLines()[3], p.getPos().toShortString()}),
    SWING = registerSingleTranslationArg(GamePacketTypes.SERVERBOUND_SWING, p -> Lang.hand(p.getHand())),
    USE_ITEM_ON = registerSimple(GamePacketTypes.SERVERBOUND_USE_ITEM_ON, p -> new Object[]{Lang.hand(p.getHand()), p.getHitResult().getBlockPos().toShortString(), Lang.direction(p.getHitResult().getDirection())}),
    USE_ITEM = registerSimple(GamePacketTypes.SERVERBOUND_USE_ITEM, p -> new Object[]{Lang.hand(p.getHand()), DF.format(p.getXRot()), DF.format(p.getYRot())})
    ;
};
