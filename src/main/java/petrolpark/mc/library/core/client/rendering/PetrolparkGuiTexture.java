package petrolpark.mc.library.core.client.rendering;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.Petrolpark;

public enum PetrolparkGuiTexture implements IGuiTexture {

    // JEI
	JEI_POINTING_HAND("jei/widgets", 40, 40, 18, 14),
    JEI_SHORT_DOWN_ARROW("jei/widgets", 0, 64, 18, 18),
    JEI_SHORT_RIGHT_ARROW("jei/widgets", 0, 82, 18, 16),
	JEI_EQUILIBRIUM_ARROW("jei/widgets", 0, 96, 42, 11),
	JEI_LINE("jei/widgets", 40, 38, 177, 2),
	JEI_TEXT_BOX_LONG("jei/widgets",169, 19),
	JEI_TEXT_BOX_SHORT("jei/widgets", 0, 19, 115, 19),
	JEI_DISTILLATION_TOWER_BOTTOM("jei/widgets", 0, 52, 12, 12),
	JEI_DISTILLATION_TOWER_MIDDLE("jei/widgets", 0, 40, 20, 12),
	JEI_DISTILLATION_TOWER_TOP("jei/widgets", 0, 38, 12, 2),
	JEI_DISTILLATION_TOWER_BRANCH("jei/widgets", 20, 45, 20, 2),
	JEI_EXPLOSION("jei/widgets", 169, 0, 18, 21),
    JEI_NERD_EMOJI("jei/widgets", 115, 19, 16, 14),
    JEI_GLOBE("jei/widgets", 115 + 16, 19, 16, 14),
	JEI_FLAG("jei/widgets", 64, 40, 16, 16),
	JEI_FLAGPOLE("jei/widgets", 80, 40, 16, 16),

    // Extended Inventory
    INVENTORY_BACKGROUND("inventory", 0, 0, 9, 9, 64, 64),
	INVENTORY_SLOT("inventory", 0, 22, 18, 18, 64, 64),
	HOTBAR_BACKGROUND("inventory", 9, 0, 22, 22, 64, 64),
	HOTBAR_SLOT("inventory", 31, 0, 20, 20, 64, 64),

    // Select Team
    SELECT_TEAM_BACKGROUND("team_selection", 176, 163),
    SELECT_TEAM("sprites/team_selection/team", 0, 0, 145, 35, 145, 35),
    SELECT_TEAM_SELECTED("sprites/team_selection/team_selected", 0, 0, 145, 35, 145, 35),
    SELECT_TEAM_HIGHLIGHTED("sprites/team_selection/team_highlighted", 0, 0, 145, 35, 145, 35),
    
    // Creative Mode Tab
	CREATIVE_MODE_TAB_BLANK_ROW("creative_inventory", 0, 0, 162, 18),

    // Redstone Programmer
    REDSTONE_PROGRAMMER("redstone_programmer/background", 256, 226),
	REDSTONE_PROGRAMMER_NOTE_BORDER_MIDDLE("redstone_programmer/widgets", 192, 0, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_BORDER_LEFT("redstone_programmer/widgets", 196, 0, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_BORDER_LONE("redstone_programmer/widgets", 200, 0, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_BORDER_RIGHT("redstone_programmer/widgets", 204, 0, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_0("redstone_programmer/widgets", 192, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_1("redstone_programmer/widgets", 196, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_2("redstone_programmer/widgets", 200, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_3("redstone_programmer/widgets", 204, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_4("redstone_programmer/widgets", 208, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_5("redstone_programmer/widgets", 212, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_6("redstone_programmer/widgets", 216, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_7("redstone_programmer/widgets", 220, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_8("redstone_programmer/widgets", 224, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_9("redstone_programmer/widgets", 228, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_10("redstone_programmer/widgets", 232, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_11("redstone_programmer/widgets", 236, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_12("redstone_programmer/widgets", 240, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_13("redstone_programmer/widgets", 244, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_14("redstone_programmer/widgets", 248, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_15("redstone_programmer/widgets", 252, 18, 4, 18),
	REDSTONE_PROGRAMMER_LINE("redstone_programmer/widgets", 179, 16, 2, 199),
	REDSTONE_PROGRAMMER_BARLINE("redstone_programmer/widgets", 181, 16, 2, 199),
	REDSTONE_PROGRAMMER_ITEM_SLOTS("redstone_programmer/widgets", 192, 36, 36, 18),
	REDSTONE_PROGRAMMER_DELETE_CHANNEL("redstone_programmer/widgets", 244, 36, 12, 18),
	REDSTONE_PROGRAMMER_MOVE_CHANNEL_UP("redstone_programmer/widgets", 228, 36, 12, 9),
	REDSTONE_PROGRAMMER_MOVE_CHANNEL_DOWN("redstone_programmer/widgets", 228, 45, 12, 9),
	REDSTONE_PROGRAMMER_PLAYHEAD("redstone_programmer/widgets", 185,16, 7, 199),
	REDSTONE_PROGRAMMER_REMOVE_BAR("redstone_programmer/widgets", 192, 54, 12, 13),
	REDSTONE_PROGRAMMER_ADD_BAR("redstone_programmer/widgets", 204, 54, 12, 13),
    ;

    private static final PetrolparkGuiTexture[] notes = new PetrolparkGuiTexture[]{REDSTONE_PROGRAMMER_NOTE_0, REDSTONE_PROGRAMMER_NOTE_1, REDSTONE_PROGRAMMER_NOTE_2, REDSTONE_PROGRAMMER_NOTE_3, REDSTONE_PROGRAMMER_NOTE_4, REDSTONE_PROGRAMMER_NOTE_5, REDSTONE_PROGRAMMER_NOTE_6, REDSTONE_PROGRAMMER_NOTE_7, REDSTONE_PROGRAMMER_NOTE_8, REDSTONE_PROGRAMMER_NOTE_9, REDSTONE_PROGRAMMER_NOTE_10, REDSTONE_PROGRAMMER_NOTE_11, REDSTONE_PROGRAMMER_NOTE_12, REDSTONE_PROGRAMMER_NOTE_13, REDSTONE_PROGRAMMER_NOTE_14, REDSTONE_PROGRAMMER_NOTE_15};

	public static PetrolparkGuiTexture getRedstoneProgrammerNote(int strength) {
		if (strength > 0 && strength <= 15) return notes[strength];
		return REDSTONE_PROGRAMMER_NOTE_0;
	};

    public final ResourceLocation location;
	public final int width, height, startX, startY, textureWidth, textureHeight;

	private PetrolparkGuiTexture(String location, int width, int height) {
		this(location, 0, 0, width, height);	
	};

	private PetrolparkGuiTexture(String location, int startX, int startY, int width, int height) {
		this(location, startX, startY, width, height, 256, 256);
	};

    private PetrolparkGuiTexture(String location, int startX, int startY, int width, int height, int textureWidth, int textureHeight) {
		this.location = Petrolpark.asResource("textures/gui/" + location + ".png");
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	};

    @OnlyIn(Dist.CLIENT)
    @Override
	public void bind() {
		RenderSystem.setShaderTexture(0, location);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(@Nonnull GuiGraphics graphics, int x, int y) {
		graphics.blit(location, x, y, startX, startY, width, height, textureWidth, textureHeight);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(@Nonnull GuiGraphics graphics, int x, int y, int color) {
		render(graphics, x, y, new Color(color));
	};

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y, Color c) {
		bind();
		UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
	};

    @Override
    public ResourceLocation getLocation() {
        return location;
    };

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public int getStartY() {
        return startY;
    };

    @Override
    public int getWidth() {
        return width;
    };

    @Override
    public int getHeight() {
        return height;
    };

    @Override
    public int getTextureWidth() {
        return textureWidth;
    };

    @Override
    public int getTextureHeight() {
        return textureHeight;
    };
};
