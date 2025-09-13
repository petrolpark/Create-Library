package com.petrolpark;

import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;

import com.petrolpark.core.badge.PlayerBadges;
import com.petrolpark.core.shop.customer.EntityCustomer;
import com.petrolpark.core.team.singleplayer.SinglePlayerTeam;
import com.petrolpark.core.wish.PlayerWishList;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

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

	public static final Supplier<AttachmentType<EntityCustomer>> ENTITY_CUSTOMER = ATTACHMENT_TYPES.register(
		"customer", AttachmentType.builder(EntityCustomer::create).serialize(EntityCustomer.ATTACHMENT_SERIALIZER)::build
	);

	@ApiStatus.Internal
	public static void register(IEventBus modEventBus) {
		ATTACHMENT_TYPES.register(modEventBus);
	};
};
