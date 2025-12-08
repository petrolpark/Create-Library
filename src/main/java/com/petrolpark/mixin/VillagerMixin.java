package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.core.world.entity.npc.VillagerUpdateSpecialPricesEvent;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    
    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
        throw new AssertionError();
    };

    @Inject(
        method = "updateSpecialPrices",
        at = @At("TAIL")
    )
    private void inUpdateSpecialPrices(Player player, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new VillagerUpdateSpecialPricesEvent(player, (Villager)(Object)this));
    };
};
