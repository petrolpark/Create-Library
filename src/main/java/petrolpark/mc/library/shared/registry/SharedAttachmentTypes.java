package petrolpark.mc.library.shared.registry;

import java.util.function.Supplier;

import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.shared.world.effect.HangoverMobEffect;

public class SharedAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Petrolpark.MOD_ID);

    public static final Supplier<AttachmentType<DynamicGameEventListener<HangoverMobEffect.VibrationListener>>> HANGOVER_VIBRATION_LISTENER = ATTACHMENT_TYPES.register(
        "hangover_vibration_listener", AttachmentType.builder(HangoverMobEffect.VibrationListener::create)
            .serialize(new HangoverMobEffect.VibrationListener.AttachmentSerializer())
            ::build
    );
  
    public static final Supplier<AttachmentType<VibrationSystem.Data>> HANGOVER_VIBRATION_DATA = ATTACHMENT_TYPES.register(
		"hangover_vibration_data", AttachmentType.builder(VibrationSystem.Data::new)
			.serialize(VibrationSystem.Data.CODEC)
			.copyOnDeath()
			::build
	);

    public static final void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    };
};
