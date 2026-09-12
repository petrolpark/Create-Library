package petrolpark.mc.library.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import petrolpark.mc.library.core.world.entity.npc.VillagerUpdateSpecialPricesEvent;
import petrolpark.mc.library.core.world.restaurant.serving.EatRestaurantServingBehavior;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {
    
    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
        throw new AssertionError();
    };

    @Inject(
        method = "registerBrainGoals",
        at = @At("RETURN")
    )
    private void petrolpark$addRestaurantEatBehavior(Brain<Villager> villagerBrain, CallbackInfo ci) {
        EatRestaurantServingBehavior.addCompoundBehavior(this, villagerBrain);
    };

    @Inject(
        method = "updateSpecialPrices",
        at = @At("TAIL")
    )
    private void petrolpark$postSpecialPricesEvent(Player player, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new VillagerUpdateSpecialPricesEvent(player, (Villager)(Object)this));
    };
};
