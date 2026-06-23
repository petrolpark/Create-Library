package petrolpark.mc.library.compat.create.core.world.item.attribute;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class SimplePetrolparkItemAttribute implements PetrolparkItemAttribute, ItemAttributeType {

    protected final String translationKey;
    protected final BiFunction<ItemStack, Level, Boolean> predicate;

    private final MapCodec<SimplePetrolparkItemAttribute> codec = MapCodec.unit(this);
    private final StreamCodec<ByteBuf, SimplePetrolparkItemAttribute> streamCodec = StreamCodec.unit(this);
    private final List<ItemAttribute> list = Collections.singletonList(this);

    public SimplePetrolparkItemAttribute(String translationKey, BiFunction<ItemStack, Level, Boolean> predicate) {
        this.translationKey = translationKey;
        this.predicate = predicate;
    };

    @Override
    public @NotNull SimplePetrolparkItemAttribute createAttribute() {
        return this;
    };

    @Override
    public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
        return appliesTo(stack, level) ? list : Collections.emptyList();
    };

    @Override
    public MapCodec<SimplePetrolparkItemAttribute> codec() {
        return codec;
    };

    @Override
    public StreamCodec<ByteBuf, SimplePetrolparkItemAttribute> streamCodec() {
        return streamCodec;
    };

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return predicate.apply(stack, world);
    };

    @Override
    public SimplePetrolparkItemAttribute getType() {
        return this;
    };

    @Override
    public String getTranslationKey() {
        return translationKey;
    };
    
};
