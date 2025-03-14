package com.petrolpark.team.scoreboard;

import java.util.function.Supplier;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.network.packet.S2CPacket;
import com.petrolpark.team.data.ITeamDataType;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public class ScoreboardTeamDataChangedPacket extends S2CPacket {

    private final String teamName;
    private final ITeamDataType<?> dataType;
    private final CompoundTag tag;

    public <T> ScoreboardTeamDataChangedPacket(Level level, ScoreboardTeam team, ITeamDataType<T> dataType) {
        this(level, team.team.getName(), dataType, team.getTeamData(dataType));
    };

    public <T> ScoreboardTeamDataChangedPacket(Level level, String teamName, ITeamDataType<T> dataType, T data) {
        this.teamName = teamName;
        this.dataType = dataType;
        this.tag = dataType.save(level, data);
    };

    public ScoreboardTeamDataChangedPacket(FriendlyByteBuf buffer) {
        teamName = buffer.readUtf();
        dataType = buffer.readRegistryIdUnsafe(PetrolparkRegistries.getRegistry(PetrolparkRegistries.Keys.TEAM_DATA_TYPE));
        tag = buffer.readAnySizeNbt();
    };

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUtf(teamName);
        buffer.writeRegistryIdUnsafe(PetrolparkRegistries.getRegistry(PetrolparkRegistries.Keys.TEAM_DATA_TYPE), dataType);
        buffer.writeNbt(tag);
    };

    @Override
    public boolean handle(Supplier<Context> supplier) {
        Minecraft mc = Minecraft.getInstance();
        supplier.get().enqueueWork(() -> Petrolpark.SCOREBOARD_TEAMS.setData(mc.level, teamName, dataType, tag));
        return true;
    };
    
};
