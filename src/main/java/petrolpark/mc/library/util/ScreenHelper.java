package petrolpark.mc.library.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenHelper {
    
    public static void openScreen(Screen screen) {
		Minecraft.getInstance().tell(() -> Minecraft.getInstance().setScreen(screen));
	};
};
