package me.rufia.fightorflight.utils.handler;

import me.rufia.fightorflight.utils.event.FOFAbstractEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class FOFAbstractEventHandler<T extends FOFAbstractEvent> {
    //protected final List<T> EVENT_LIST = new ArrayList<>();

    protected abstract List<T> getEventList();

    protected abstract int getEventMaxCount();

    protected int getEventCount() {
        return getEventList().size();
    }

    protected void addEvent(T event) {
        int maxCount = getEventMaxCount();
        if (getEventMaxCount() > 0) {
            if (getEventCount() > maxCount) {
                getEventList().removeFirst();
            }
        }
        getEventList().add(event);
    }

    protected T getEvent(int index) {
        if (index >= getEventCount() || index < 0) {
            return null;
        }
        return getEventList().get(index);
    }

    public void update(int tick) {
        getEventList().removeIf((event -> !event.isAvailable(tick)));
    }
}
