package me.rufia.fightorflight.utils.msg;

public class FOFUseMoveMsg extends FOFMsg {
    private String pokemonName;
    private String moveName;

    public FOFUseMoveMsg(String pokemonName, String moveName) {
        this.pokemonName = pokemonName;
        this.moveName = moveName;
    }
}
