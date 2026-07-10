package petrolpark.mc.library.registry;

import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.badge.PlayerBadges;
import petrolpark.mc.library.core.data.loot.wish.PlayerWishList;
import petrolpark.mc.library.core.world.entity.player.team.singleplayer.SinglePlayerTeam;
import petrolpark.mc.library.core.world.restaurant.customer.MobCustomer;

public class PetrolparkAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Petrolpark.MOD_ID);

	public static final Supplier<AttachmentType<PlayerBadges>> BADGES = ATTACHMENT_TYPES.register(
		"badges", AttachmentType.builder(PlayerBadges::empty)
			.serialize(PlayerBadges.CODEC)
			.copyOnDeath()
			::build
	);

	public static final Supplier<AttachmentType<SinglePlayerTeam>> SINGLE_PLAYER_TEAM_COMPONENTS = ATTACHMENT_TYPES.register(
		"single_team_components", AttachmentType.builder(SinglePlayerTeam::create)
			.serialize(SinglePlayerTeam.ATTACHMENT_SERIALIZER)
			.copyOnDeath()
			::build
	);

	public static final Supplier<AttachmentType<PlayerWishList>> WISHLIST = ATTACHMENT_TYPES.register(
		"wishlist", AttachmentType.builder(PlayerWishList::new)
			.serialize(PlayerWishList.SERIALIZER)
			.copyOnDeath()
			.copyHandler(PlayerWishList::new)
			::build
	);

	public static final Supplier<AttachmentType<MobCustomer>> ENTITY_CUSTOMER = ATTACHMENT_TYPES.register(
		"customer", AttachmentType.builder(MobCustomer::create).serialize(MobCustomer.ATTACHMENT_SERIALIZER)::build
	);

	@ApiStatus.Internal
	public static void register(IEventBus modEventBus) {
		ATTACHMENT_TYPES.register(modEventBus);
	};
};
