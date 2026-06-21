package petrolpark.mc.library.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import petrolpark.mc.library.core.world.block.IPickUpPutDownBlock;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    
    @ModifyExpressionValue(
        method = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;performUseItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/GameType;isCreative()Z"
        )
    )
    private boolean petrolpark$dontRevertItemStack(boolean original, LocalPlayer player, InteractionHand hand, BlockHitResult result) {
        return original && !(player.getItemInHand(hand).getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IPickUpPutDownBlock);
    };
};
