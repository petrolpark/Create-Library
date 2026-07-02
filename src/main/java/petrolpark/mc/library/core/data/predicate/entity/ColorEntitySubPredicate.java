package petrolpark.mc.library.core.data.predicate.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import petrolpark.mc.library.util.ColorHelper;

/**
 * <p>{@code petrolpark:color}</p>
 * 
 * Check if an Entity's color matches a list. By default, this is applicable to Sheep, Shulkers and Tropical Fish.
 * 
 * Arguments:
 * 
 * <ul>
 * <li>{@code colors} - List of {@link DyeColor}s to match against. If empty, then the entity must also be colorless (which is true of all other vanilla entities than those mentioned). Note that Sheep are never colorless.
 * <li>{@code secondary} - Whether to check the secondary color of the entity (defaults to {@code false}). This is only applicable to Tropical Fish by default, which have a primary and secondary color.
 * </li>
 * 
 * @author petrolpark
 */
public record ColorEntitySubPredicate(List<DyeColor> colors, boolean secondary) implements EntitySubPredicate {

    public static final MapCodec<ColorEntitySubPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DyeColor.CODEC.listOf().fieldOf("colors").forGetter(ColorEntitySubPredicate::colors),
        Codec.BOOL.optionalFieldOf("secondary", false).forGetter(ColorEntitySubPredicate::secondary)
    ).apply(instance, ColorEntitySubPredicate::new));

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    };

    @Override
    public boolean matches(@Nonnull Entity entity, @Nonnull ServerLevel level, @Nullable Vec3 position) {
        if (entity instanceof LivingEntity livingEntity) {
            DyeColor color = ColorHelper.getColor(livingEntity, secondary);
            if (color == null) return colors.isEmpty();
            return colors.contains(color);
        };
        return false;
    };
    
};
