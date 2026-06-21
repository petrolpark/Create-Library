package petrolpark.mc.library.registry;

import org.lwjgl.glfw.GLFW;

import petrolpark.mc.library.Petrolpark;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(Dist.CLIENT)
public enum PetrolparkKeyBinds {
    
	// Tubes
    TUBE_BUILD("tube.build", GLFW.GLFW_KEY_ENTER),
    TUBE_DELETE_CONTROL_POINT("tube.delete_control_point", GLFW.GLFW_KEY_BACKSPACE),
    TUBE_ADD_CONTROL_POINT_AFTER("tube.add_control_point_after", GLFW.GLFW_KEY_EQUAL),
    TUBE_ADD_CONTROL_POINT_BEFORE("tube.add_control_point_before", GLFW.GLFW_KEY_MINUS),
    TUBE_CANCEL("tube.cancel", GLFW.GLFW_KEY_X),

	// Extended Hotbar
	HOTBAR_SLOT_9("hotbar.10", GLFW.GLFW_KEY_0),
	HOTBAR_SLOT_10("hotbar.11", GLFW.GLFW_KEY_MINUS),
	HOTBAR_SLOT_11("hotbar.12", GLFW.GLFW_KEY_EQUAL),
	HOTBAR_SLOT_12("hotbar.13", -1),
	HOTBAR_SLOT_13("hotbar.14", -1),
	HOTBAR_SLOT_14("hotbar.15", -1),
	HOTBAR_SLOT_15("hotbar.16", -1),
	HOTBAR_SLOT_16("hotbar.17", -1);
    ;

	public KeyMapping keybind;
	private String description;
	private int key;

	private PetrolparkKeyBinds(String description, int defaultKey) {
		this.description = Petrolpark.MOD_ID + ".key." + description;
		this.key = defaultKey;
	};

    @SubscribeEvent
	public static void register(RegisterKeyMappingsEvent event) {
		for (PetrolparkKeyBinds key : values()) {
			key.keybind = new KeyMapping(key.description, key.key, "Petrolpark's Library");
			event.register(key.keybind);
		};
	}
};
