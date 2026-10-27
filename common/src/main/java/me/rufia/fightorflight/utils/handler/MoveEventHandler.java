package me.rufia.fightorflight.utils.handler;

import me.rufia.fightorflight.utils.event.move.FOFMoveEvent;
import me.rufia.fightorflight.utils.event.msg.FOFMsg;

import java.util.ArrayList;
import java.util.List;

public class MoveEventHandler extends FOFAbstractEventHandler<FOFMoveEvent> {
    protected final List<FOFMoveEvent> eventList = new ArrayList<>();

    @Override
    protected List<FOFMoveEvent> getEventList() {
        return eventList;
    }

    @Override
    protected int getEventMaxCount() {
        return 0;
    }

    public void addEvent(FOFMoveEvent event) {
        super.addEvent(event);
    }

    @Override
    public void update(int tick) {
        for (FOFMoveEvent event : getEventList()) {
            if (!event.isAvailable(tick)) {
                event.use();
            }
        }
        super.update(tick);
    }
}
