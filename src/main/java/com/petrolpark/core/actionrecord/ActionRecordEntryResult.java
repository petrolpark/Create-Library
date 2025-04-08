package com.petrolpark.core.actionrecord;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

public sealed interface ActionRecordEntryResult permits ActionRecordEntryResult.Logged, ActionRecordEntryResult.SilentIgnore {

    public static ActionRecordEntryResult SILENT_IGNORE = new SilentIgnore();
    public static ActionRecordEntryResult enter(Component name) {
        return new Logged.Simple(name);
    };
    public static ActionRecordEntryResult enter(Component simpleName, Component advancedName) {
        if (advancedName == null) return enter(simpleName);
        return new Logged.Advanced(simpleName, advancedName);
    };
    public static ActionRecordEntryResult unsupported(CustomPacketPayload.Type<?> payloadType) {
        return new Logged.UnsupportedPayloadType(payloadType.id());
    };
    public static ActionRecordEntryResult unsupported(PacketType<?> packetType) {
        return new Logged.UnsupportedPayloadType(packetType.id());
    };

    public static non-sealed interface Logged extends ActionRecordEntryResult {

        public Component name(TooltipFlag tooltipFlag);

        public static record Simple(Component name) implements Logged {

            @Override
            public Component name(TooltipFlag tooltipFlag) {
                return name();
            };
        };

        public static record Advanced(Component simpleName, Component advancedName) implements Logged {

            @Override
            public Component name(TooltipFlag tooltipFlag) {
                return tooltipFlag.isAdvanced() ? advancedName() : simpleName();
            };

        };

        public static record UnsupportedPayloadType(ResourceLocation id) implements Logged {

            @Override
            public Component name(TooltipFlag tooltipFlag) {
                return tooltipFlag.isAdvanced() ? Component.translatable("petrolpark.packet.unsupported.advanced", id()) : Component.translatable("petrolpark.packet.unsupported");
            };

        };
    };

    public static final class SilentIgnore implements ActionRecordEntryResult {};

};
