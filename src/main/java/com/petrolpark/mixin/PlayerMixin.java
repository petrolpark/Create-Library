package com.petrolpark.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.authlib.GameProfile;
import com.petrolpark.core.extendedinventory.ExtendedInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        throw new AssertionError();
    };

    @Shadow
    private Inventory inventory;
     
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void inInit(Level level, BlockPos pos, float yRot, GameProfile gameProfile, CallbackInfo ci) {
        if (ExtendedInventory.enabled(level.enabledFeatures())) {
            ExtendedInventory extendedInv = new ExtendedInventory((Player)(Object)this);
            inventory = extendedInv;
            ExtendedInventory.refreshPlayerInventoryMenuServer((Player)(Object)this);
        };
    };

    /**
     * Handle setting the mainhand Item if the extended Hotbar is in use.
     * @param slot
     * @param stack
     * @param original
     */
    @WrapMethod(
        method = "setItemSlot"
    )
    public void inSetItemSlot(EquipmentSlot slot, ItemStack stack, Operation<Void> original) {
        if (slot == EquipmentSlot.MAINHAND) {
            Optional<ExtendedInventory> invOp = ExtendedInventory.get((Player)(Object)this);
            if (invOp.isPresent()) {
                verifyEquippedItem(stack);
                ExtendedInventory inv = invOp.get();
                ItemStack oldStack = inv.getItem(inv.selected);
                inv.setItem(inv.selected, stack);
                onEquipItem(slot, oldStack, stack);
            };
        };
        original.call(slot, stack);
    };
};
