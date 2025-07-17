package com.petrolpark.shadereffects.packet;

import com.petrolpark.network.packet.S2CPacket;
import com.petrolpark.util.IGameRendererMixin;
import com.petrolpark.util.IMobEffectInstanceMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class MEIShaderRemovePacket extends S2CPacket {
    ResourceLocation mobEffect;

    public MEIShaderRemovePacket(MobEffect mobEffect) {
        this.mobEffect = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
    }

    public MEIShaderRemovePacket(FriendlyByteBuf buffer) {
        this.mobEffect = buffer.readResourceLocation();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.mobEffect);
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if ( player == null ) return;

            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(mobEffect);
            if ( effect == null ) return;

            MobEffectInstance instance = player.getEffect(effect);
            if ( instance == null ) return;

            IGameRendererMixin gameRenderer = (( IGameRendererMixin ) Minecraft.getInstance().gameRenderer);
            gameRenderer.removeMobEffectInstanceShader((( IMobEffectInstanceMixin ) instance));
        });
        return true;
    }
}
