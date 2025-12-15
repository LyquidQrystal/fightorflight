package me.rufia.fightorflight.data.movedata.container;

import me.rufia.fightorflight.data.movedata.MoveDataContainer;
import me.rufia.fightorflight.data.movedata.movedatas.StatChangeMoveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatChangeMoveDataContainer extends MoveDataContainer<StatChangeMoveData> {
    private final int stage;

    public StatChangeMoveDataContainer(String type, String target, String triggerEvent, float chance, boolean canActivateSheerForce, String name, List<String> move_list, int stage) {
        super(type, target, triggerEvent, chance, canActivateSheerForce, name, move_list);
        this.stage = stage;
    }

    @Override
    public Map<String, StatChangeMoveData> build() {
        Map<String, StatChangeMoveData> dataMap = new HashMap<>();
        for (String moveName : getMoveList()) {
            StatChangeMoveData data = new StatChangeMoveData(getTarget(), getTriggerEvent(), getChance(), canActivateSheerForce(), getName(), stage);
            dataMap.put(moveName, data);
        }
        return dataMap;
    }
}
