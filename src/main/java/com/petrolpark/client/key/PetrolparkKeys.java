package com.petrolpark.client.key;

import org.lwjgl.glfw.GLFW;

import com.petrolpark.Petrolpark;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public enum PetrolparkKeys {
    
    TUBE_BUILD("tube.build", GLFW.GLFW_KEY_ENTER),
    TUBE_DELETE_CONTROL_POINT("tube.delete_control_point", GLFW.GLFW_KEY_BACKSPACE),
    TUBE_ADD_CONTROL_POINT_AFTER("tube.add_control_point_after", GLFW.GLFW_KEY_EQUAL),
    TUBE_ADD_CONTROL_POINT_BEFORE("tube.add_control_point_before", GLFW.GLFW_KEY_MINUS),
    TUBE_CANCEL("tube.cancel", GLFW.GLFW_KEY_X)
    ;

	public KeyMapping keybind;
	private String description;
	private int key;

	private PetrolparkKeys(String description, int defaultKey) {
		this.description = Petrolpark.MOD_ID + ".key." + description;
		this.key = defaultKey;
	};

    @SubscribeEvent
	public static void register(RegisterKeyMappingsEvent event) {
		for (PetrolparkKeys key : values()) {
			key.keybind = new KeyMapping(key.description, key.key, "Petrolpark's Library");
			event.register(key.keybind);
		};
	}
};
