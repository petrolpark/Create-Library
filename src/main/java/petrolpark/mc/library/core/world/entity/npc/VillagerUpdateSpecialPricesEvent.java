package petrolpark.mc.library.core.world.entity.npc;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Fired when a Player interacts with a Villager to modify the price, e.g. if the Player has Hero of the Village.
 * Fired on server-side only.
 */
public class VillagerUpdateSpecialPricesEvent extends PlayerEvent {

    protected final Villager villager;

    public VillagerUpdateSpecialPricesEvent(Player player, Villager villager) {
        super(player);
        this.villager = villager;
    };

    public Villager getVillager() {
        return villager;
    };

    public MerchantOffers getOffers() {
        return getVillager().getOffers();
    }
};
