package me.rufia.fightorflight.data.movedata;

import java.util.List;
import java.util.Map;

public abstract class MoveDataContainer<T extends MoveData> {
    private final List<String> move_list;
    private final String type;
    private final String target;
    private final float chance;
    private final boolean canActivateSheerForce;
    private final String triggerEvent;
    private final String name;


    public String getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public float getChance() {
        return chance;
    }

    public boolean canActivateSheerForce() {
        return canActivateSheerForce;
    }

    public String getName() {
        return name;
    }

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public List<String> getMoveList() {
        return move_list;
    }

    public MoveDataContainer(String type, String target, String triggerEvent, float chance, boolean canActivateSheerForce, String name, List<String> move_list) {
        this.type = type;
        this.target = target;
        this.chance = chance;
        this.canActivateSheerForce = canActivateSheerForce;
        this.name = name;
        this.move_list = move_list;
        this.triggerEvent = triggerEvent;
    }

    public abstract Map<String, T> build();

    @Override
    public String toString() {
        return super.toString();
    }
}
