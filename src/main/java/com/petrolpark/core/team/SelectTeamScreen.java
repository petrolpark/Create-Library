package com.petrolpark.core.team;

import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.core.team.packet.BindTeamPacket;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SelectTeamScreen extends AbstractSimiScreen {

    private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/stonecutter/scroller_disabled");

    public final BindTeamPacket.Factory packetFactory;
    protected final List<ITeam> selectableTeams;
    protected ITeam selectedTeam = null;

    private float scrollOffset = 0f;
    private boolean scrolling = false;
    private int startIndex = 0;

    private PetrolparkGuiTexture background;

    public SelectTeamScreen(Component title, List<ITeam> selectableTeams, BindTeamPacket.Factory packetFactory) {
        super(title);
        this.selectableTeams = selectableTeams;
        this.packetFactory = packetFactory;

        background = PetrolparkGuiTexture.SELECT_TEAM_BACKGROUND;
    };

    public ITeam getSelectedTeam() {
        return selectedTeam;
    };

    public void sendTeamSelection() {
        if (getSelectedTeam() != null) CatnipServices.NETWORK.sendToServer(packetFactory.create(getSelectedTeam().getProvider()));
    };

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        super.init();
    };

    @Override
    protected void renderWindow(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        background.render(graphics, x, y);

        // Title
        graphics.drawString(font, title, x + (background.getWidth() - font.width(title)) / 2, y + 5, 0x333333, false);

        // Scroller
        int k = (int)((float)(SCROLL_AREA_HEIGHT - 11) * scrollOffset);
        ResourceLocation scrollerRL = isScrollBarActive() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
        graphics.blitSprite(scrollerRL, x + SCROLL_AREA_X, y + 15 + k, 12, 15);

        x = guiLeft + SELECTION_AREA_X;
        y = guiTop + SELECTION_AREA_Y;

        // Teams
        for (int index = startIndex; index < startIndex + 4; index++) {
            int visibleIndex = index - startIndex;
            if (index >= selectableTeams.size()) break;
            ITeam team = selectableTeams.get(index);

            PetrolparkGuiTexture texture = team == selectedTeam ? PetrolparkGuiTexture.SELECT_TEAM_SELECTED : PetrolparkGuiTexture.SELECT_TEAM;

            double relMouseX = mouseX - (double)x;
            double relMouseY = mouseY - (double)(y + visibleIndex * (double)TEAM_BUTTON_HEIGHT);
            if (relMouseX >= 0d && relMouseY >= 0d && relMouseX < (double)SELECTION_AREA_WIDTH && relMouseY < (double)TEAM_BUTTON_HEIGHT) texture = PetrolparkGuiTexture.SELECT_TEAM_HIGHLIGHTED;

            texture.render(graphics, x, y + visibleIndex * TEAM_BUTTON_HEIGHT);

            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate(x + 4, y + 4 + visibleIndex * TEAM_BUTTON_HEIGHT, 0);
            ms.pushPose();
            ms.scale(1.7f, 1.7f, 1f);
            team.renderIcon(graphics);
            ms.popPose();
            ms.translate(32, 3, 0);
            Minecraft mc = minecraft;
            if (mc != null) {
                graphics.drawString(mc.font, team.getName(), 0, 0, 0xFFFFFF);
                graphics.drawString(mc.font, team.getRenderedMemberList(100).copy().withStyle(ChatFormatting.GRAY), 0, 12, 0xFFFFFF);
            }
            ms.popPose();
        };
    };

    protected static final int SELECTION_AREA_X = 8;
    protected static final int SELECTION_AREA_Y = 15;
    protected static final int SELECTION_AREA_WIDTH = 145;
    protected static final int TEAM_BUTTON_HEIGHT = 35;

    protected static final int SCROLL_AREA_X = 156;
    protected static final int SCROLL_AREA_HEIGHT = 140;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        scrolling = false;
    
        int x = guiLeft + SELECTION_AREA_X;
        int y = guiTop + SELECTION_AREA_Y;
        int maxIndex = startIndex + 4;

        for (int index = startIndex; index < maxIndex; index++) {
            int visibleIndex = index - startIndex;
            double relMouseX = mouseX - (double)x;
            double relMouseY = mouseY - (double)(y + visibleIndex * (double)TEAM_BUTTON_HEIGHT);
            if (relMouseX >= 0d && relMouseY >= 0d && relMouseX < (double)SELECTION_AREA_WIDTH && relMouseY < (double)TEAM_BUTTON_HEIGHT && index >= 0 && index < selectableTeams.size()) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1f));
                selectedTeam = selectableTeams.get(index);
                return true;
            };
        };

        x = guiLeft + SCROLL_AREA_X;
        y = guiTop + 9;
        if (mouseX >= (double)x && mouseX < (double)(x + 12) && mouseY >= (double)y && mouseY < (double)(y + SCROLL_AREA_HEIGHT)) {
            scrolling = true;
        };
        
        return super.mouseClicked(mouseX, mouseY, button);
    };

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrolling && isScrollBarActive()) {
            int top = guiTop + SELECTION_AREA_Y;
            int bottom = top + SCROLL_AREA_HEIGHT;
            scrollOffset = ((float)mouseY - (float)top - 7.5F) / ((float)(bottom - top) - 15.0F);
            scrollOffset = Mth.clamp(scrollOffset, 0.0F, 1.0F);
            startIndex = (int)((double)(scrollOffset * (float)getOffscreenRows()) + 0.5);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    };

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isScrollBarActive()) {
            int i = getOffscreenRows();
            float f = (float)scrollY / (float)i;
            scrollOffset= Mth.clamp(scrollOffset - f, 0.0F, 1.0F);
            startIndex = (int)((double)(scrollOffset* (float)i) + 0.5);
        };

        return true;
    };

    private boolean isScrollBarActive() {
        return selectableTeams.size() > 4;
    };

    protected int getOffscreenRows() {
        return selectableTeams.size() - 4;
    };

    @Override
    public void onClose() {
        super.onClose();
        sendTeamSelection();
    };
    
};
