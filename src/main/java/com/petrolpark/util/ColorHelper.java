package com.petrolpark.util;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class ColorHelper {
    
    public static final boolean isFullyTransparent(int argb) {
        return (((argb >> 16) & 0xff) == 0);
    };

    public static final int opaque(int argb) {
        return 0xFF000000 | (argb & 0x00FFFFFF);
    };

    public static final int copyAlpha(int rgb, int a) {
        return (a & 0xFF000000) | (rgb & 0x00FFFFFF); // Alpha of original pixel, and color of mean shift cluster
    };

    public static final Vec3 toOKLabVec(int argb) {
        final int r8 = (argb >> 16) & 0xFF;
        final int g8 = (argb >> 8) & 0xFF;
        final int b8 = argb & 0xFF;
        // Normalize to [0,1]
        final double r = srgbToLinear(r8 / 255.0);
        final double g = srgbToLinear(g8 / 255.0);
        final double b = srgbToLinear(b8 / 255.0);
        // Convert linear sRGB → LMS
        final double l = 0.4122214708*r + 0.5363325363*g + 0.0514459929*b;
        final double m = 0.2119034982*r + 0.6806995451*g + 0.1073969566*b;
        final double s = 0.0883024619*r + 0.2817188376*g + 0.6299787005*b;
        // Cube root
        final double l_ = Math.cbrt(l);
        final double m_ = Math.cbrt(m);
        final double s_ = Math.cbrt(s);
        // OKLab
        final double L =  0.2104542553*l_ + 0.7936177850*m_ - 0.0040720468*s_;
        final double A =  1.9779984951*l_ - 2.4285922050*m_ + 0.4505937099*s_;
        final double B =  0.0259040371*l_ + 0.7827717662*m_ - 0.8086757660*s_;
        return new Vec3(L, A, B);
    };

    public static final double srgbToLinear(double c) {
        return (c <= 0.04045) ? (c / 12.92) : Math.pow((c + 0.055) / 1.055, 2.4);
    };

    public static final int toRGB(Vec3 OKLabVec) {
        final double L = OKLabVec.x;
        final double A = OKLabVec.y;
        final double B = OKLabVec.z;
        // OKLab → cube roots of LMS
        final double l_ =  L + 0.3963377774*A + 0.2158037573*B;
        final double m_ =  L - 0.1055613458*A - 0.0638541728*B;
        final double s_ =  L - 0.0894841775*A - 1.2914855480*B;
        final double l = l_*l_*l_;
        final double m = m_*m_*m_;
        final double s = s_*s_*s_;
        // LMS → linear sRGB
        final double r = + 4.0767416621*l - 3.3077115913*m + 0.2309699292*s;
        final double g = - 1.2684380046*l + 2.6097574011*m - 0.3413193965*s;
        final double b = - 0.0041960863*l - 0.7034186147*m + 1.7076147010*s;
        // Gamma encode and clamp
        final int r8 = Mth.clamp((int)Math.round(linearToSrgb(r) * 255.0), 0, 255);
        final int g8 = Mth.clamp((int)Math.round(linearToSrgb(g) * 255.0), 0, 255);
        final int b8 = Mth.clamp((int)Math.round(linearToSrgb(b) * 255.0), 0, 255);
        return (0xFF << 24) | (r8 << 16) | (g8 << 8) | b8;
    };

    public static final double linearToSrgb(double c) {
        return (c <= 0.0031308) ? (12.92 * c) : (1.055 * Math.pow(c, 1/2.4) - 0.055);
    };

    public static final @Nullable DyeColor getColor(LivingEntity entity) {
        if (entity instanceof Sheep sheep) return sheep.getColor();
        if (entity instanceof Shulker shulker) return shulker.getColor();
        final GetEntityColorEvent event = new GetEntityColorEvent(entity);
        NeoForge.EVENT_BUS.post(event);
        return event.getColor();
    };

    public static final void setColor(LivingEntity entity, @Nullable DyeColor color) {
        if (entity instanceof Sheep sheep && color != null) sheep.setColor(color);
        if (entity instanceof Shulker shulker) shulker.setVariant(Optional.ofNullable(color));
        final SetEntityColorEvent event = new SetEntityColorEvent(entity, color);
        NeoForge.EVENT_BUS.post(event);
    };

    public static final class GetEntityColorEvent extends LivingEvent {

        @Nullable
        protected DyeColor color;

        public GetEntityColorEvent(LivingEntity entity) {
            super(entity);
        };

        public void setColor(@Nullable DyeColor color) {
            this.color = color;
        };

        public @Nullable DyeColor getColor() {
            return color;
        };

    };

    public static final class SetEntityColorEvent extends LivingEvent implements ICancellableEvent {

        @Nullable
        protected final DyeColor color;

        public SetEntityColorEvent(LivingEntity entity, DyeColor color) {
            super(entity);
            this.color = color;
        };

        public @Nullable DyeColor getColor() {
            return color;
        };

    };

    @OnlyIn(Dist.CLIENT)
    public static final void refreshChunkColors(ChunkPos centerPos) {
        final Minecraft mc = Minecraft.getInstance();
        final ClientLevel level = mc.level;
        if (level == null) return;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                final ChunkPos pos = new ChunkPos(centerPos.x + x, centerPos.z + z);
                level.tintCaches.values().forEach(cache -> cache.invalidateForChunk(pos.x, pos.z));
                for (int y = level.getMinSection(); y < level.getMaxSection(); y++) mc.levelRenderer.setSectionDirty(pos.x, y, pos.z);
            };
        };
    };
};
