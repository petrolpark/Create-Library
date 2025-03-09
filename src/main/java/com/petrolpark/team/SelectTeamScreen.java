package com.petrolpark.team;

import java.util.List;

import com.petrolpark.network.PetrolparkMessages;
import com.petrolpark.team.packet.BindTeamPacket;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SelectTeamScreen extends Screen {

    public final BindTeamPacket.Factory packetFactory;

    protected final List<ITeam<?>> selectableTeams;

    protected ITeam<?> selectedTeam = null;

    public SelectTeamScreen(Component title, List<ITeam<?>> selectableTeams, BindTeamPacket.Factory packetFactory) {
        super(title);
        this.selectableTeams = selectableTeams;
        this.packetFactory = packetFactory;
        if (!selectableTeams.isEmpty()) selectedTeam = selectableTeams.get(0);
    };

    @SuppressWarnings("unchecked")
    public <T extends ITeam<? super T>> T getSelectedTeam() {
        return (T)selectedTeam;
    };

    public void sendTeamSelection() {
        if (getSelectedTeam() != null) PetrolparkMessages.sendToServer(packetFactory.create(getSelectedTeam()));
    };

    @Override
    public void onClose() {
        super.onClose();
        sendTeamSelection();
    };
    
};
