package com.petrolpark.core.actionrecord.packet.entrant;

import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.Util;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public final class SimpleVanillaPacketEntrant<LISTENER extends PacketListener, PACKET extends Packet<LISTENER>> extends AlwaysEnterPacketEntrant<PACKET> implements IVanillaPacketEntrant<LISTENER, PACKET> {

    public final PacketType<PACKET> type;
    public final String translationKey;
    private final Function<PACKET, Object[]> translationArgs;
    private final Function<PACKET, Object[]> advancedTranslationArgs;

    public SimpleVanillaPacketEntrant(PacketType<PACKET> type, Function<PACKET, Object[]> translationArgs) {
        this(type, translationArgs, null);
    };

    public SimpleVanillaPacketEntrant(PacketType<PACKET> type, Function<PACKET, Object[]> translationArgs, Function<PACKET, Object[]> advancedTranslationArgs) {
        this.type = type;
        translationKey = Util.makeDescriptionId("packet", type.id());
        this.translationArgs = translationArgs;
        this.advancedTranslationArgs = advancedTranslationArgs;
    };

    @Override
    public Component getDescription(PACKET packet) {
        return Component.translatable(translationKey, translationArgs.apply(packet));
    };

    @Override
    public Component getAdvancedDescription(PACKET packet) {
        if (advancedTranslationArgs == null) return super.getAdvancedDescription(packet);
        return Component.translatable(translationKey + ".advanced", ArrayUtils.addAll(translationArgs.apply(packet), advancedTranslationArgs.apply(packet)));
    };
    
};
