package petrolpark.mc.library.core.data.reward.info;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import petrolpark.mc.library.util.Lang.IndentedTooltipBuilder;
import petrolpark.mc.library.util.codec.CodecHelper;

@ParametersAreNonnullByDefault
public abstract class WrappedRewardInfo implements INamedRewardInfo {

    public static final <I extends WrappedRewardInfo> MapCodec<I> codec(NonNullFunction<IRewardInfo, I> factory) {
        return CodecHelper.singleFieldMap(IRewardInfo.DIRECT_CODEC, "wrapped", WrappedRewardInfo::wrapped, factory);
    };

    public static final <I extends WrappedRewardInfo> StreamCodec<RegistryFriendlyByteBuf, I> streamCodec(NonNullFunction<IRewardInfo, I> factory) {
        return StreamCodec.composite(IRewardInfo.STREAM_CODEC, WrappedRewardInfo::wrapped, factory);
    };
    
    public final IRewardInfo wrapped;

    public WrappedRewardInfo(IRewardInfo wrapped) {
        this.wrapped = wrapped;
    };

    public final IRewardInfo wrapped() {
        return wrapped;
    };

    @Override
    public void render(GuiGraphics graphics) {
        wrapped().render(graphics);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        builder.add(translateSimple()).indent();
        wrapped().addToDescription(builder);
        builder.unindent();
    };
};
