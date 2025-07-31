package com.petrolpark.compat.create.core.recipe;

import java.util.Set;

import com.petrolpark.core.recipe.book.IRecipeBookProviderBlock;
import com.petrolpark.util.Pair;

import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class RecipeBookProviderHighlighter {

    @SubscribeEvent
    public final void onTick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel world = mc.level;
		LocalPlayer player = mc.player;
        if (player == null || world == null) return;
		HitResult target = mc.hitResult;
		if (target == null || !(target instanceof BlockHitResult bhr)) return;
        BlockPos pos = bhr.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof IRecipeBookProviderBlock rbpBlock && rbpBlock.shouldHighlightConnectedRecipeBookAcceptors(world, pos, state)) {
            Set<Pair<BlockPos, IRecipeBookProviderBlock.ProvisionType>> provisions = rbpBlock.getRecipeBookProvisions(world, pos, state);
            Outliner.getInstance().showCluster("recipe book acceptors", provisions.stream().filter(pair -> pair.getSecond() == IRecipeBookProviderBlock.ProvisionType.PROVIDES).map(Pair::getFirst).toList())
                .colored(Color.SPRING_GREEN)
                .lineWidth(1 / 16f);
            Outliner.getInstance().showCluster("potential recipe book acceptors", provisions.stream().filter(pair -> pair.getSecond() == IRecipeBookProviderBlock.ProvisionType.CAN_PROVIDE).map(Pair::getFirst).toList())
                .colored(0xDDC166)
                .lineWidth(1 / 16f);
        };
    };
};
