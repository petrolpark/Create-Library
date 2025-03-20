package com.petrolpark.core.team;

import java.util.List;

import com.petrolpark.core.team.packet.BindTeamPacket;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SelectTeamScreen extends Screen {

    public final BindTeamPacket.Factory packetFactory;

    protected final List<ITeam> selectableTeams;

    protected ITeam selectedTeam = null;

    public SelectTeamScreen(Component title, List<ITeam> selectableTeams, BindTeamPacket.Factory packetFactory) {
        super(title);
        this.selectableTeams = selectableTeams;
        this.packetFactory = packetFactory;
        if (!selectableTeams.isEmpty()) selectedTeam = selectableTeams.get(0);
    };

    public ITeam getSelectedTeam() {
        return selectedTeam;
    };

    public void sendTeamSelection() {
        if (getSelectedTeam() != null) CatnipServices.NETWORK.sendToServer(packetFactory.create(getSelectedTeam().getProvider()));
    };

    @Override
    public void onClose() {
        super.onClose();
        sendTeamSelection();
    };
    
};
