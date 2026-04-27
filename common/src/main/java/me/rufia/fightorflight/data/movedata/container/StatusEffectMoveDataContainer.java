package me.rufia.fightorflight.data.movedata.container;

import me.rufia.fightorflight.data.movedata.MoveDataContainer;
import me.rufia.fightorflight.data.movedata.movedatas.StatusEffectMoveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusEffectMoveDataContainer extends MoveDataContainer<StatusEffectMoveData> {
    public StatusEffectMoveDataContainer(String type, String target, String triggerEvent, float chance, boolean canActivateSheerForce, String name, List<String> move_list) {
        super(type, target, triggerEvent, chance, canActivateSheerForce, name, move_list);
    }

    @Override
    public Map<String, StatusEffectMoveData> build() {
        Map<String, StatusEffectMoveData> dataMap = new HashMap<>();
        for (String moveName : getMoveList()) {
            StatusEffectMoveData data = new StatusEffectMoveData(getTarget(), getTriggerEvent(), getChance(), canActivateSheerForce(), getName());
            dataMap.put(moveName, data);
        }
        return dataMap;
    }
}
