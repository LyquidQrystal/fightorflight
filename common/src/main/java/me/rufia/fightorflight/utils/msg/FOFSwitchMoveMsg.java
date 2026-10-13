package me.rufia.fightorflight.utils.msg;

import me.rufia.fightorflight.net.packet.MoveInfoPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class FOFSwitchMoveMsg extends FOFMsg {
    private String pokemonName;
    private String moveName;
    private int faction;


    public FOFSwitchMoveMsg(String pokemonName, String moveName, int faction, int createTime) {
        this.pokemonName = pokemonName;
        this.moveName = moveName;
        this.faction = faction;
        duration = 100;
        this.createTime = createTime;
    }

    public FOFSwitchMoveMsg(MoveInfoPacket packet, int createTime) {
        this.pokemonName = packet.getPokemonName();
        this.moveName = packet.getMoveName();
        this.faction = packet.getFaction();
        duration = 100;
        this.createTime = createTime;
    }

    public int getFaction() {
        return faction;
    }

    public MutableComponent getMessageText() {
        return Component.translatable("client.fightorflight.hud.change_move", pokemonName, moveName);
    }
}
