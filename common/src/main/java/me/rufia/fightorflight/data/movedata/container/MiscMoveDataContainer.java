package me.rufia.fightorflight.data.movedata.container;

import me.rufia.fightorflight.data.movedata.MoveDataContainer;
import me.rufia.fightorflight.data.movedata.movedatas.MiscMoveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscMoveDataContainer extends MoveDataContainer<MiscMoveData> {
    public MiscMoveDataContainer(String type, String target, String triggerEvent, float chance, boolean canActivateSheerForce, String name, List<String> move_list) {
        super(type, target, triggerEvent, chance, canActivateSheerForce, name, move_list);
    }

    @Override
    public Map<String, MiscMoveData> build() {
        Map<String, MiscMoveData> dataMap = new HashMap<>();
        for (String moveName : getMoveList()) {
            MiscMoveData data = new MiscMoveData(getTarget(), getTriggerEvent(), getChance(), canActivateSheerForce(), getName());
            dataMap.put(moveName, data);
        }
        return dataMap;
    }
}
