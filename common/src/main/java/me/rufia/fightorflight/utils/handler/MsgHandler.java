package me.rufia.fightorflight.utils.handler;

import me.rufia.fightorflight.utils.event.msg.FOFMsg;

import java.util.ArrayList;
import java.util.List;

public class MsgHandler extends FOFAbstractEventHandler<FOFMsg> {
    protected final List<FOFMsg> msgList = new ArrayList<>();

    @Override
    protected List<FOFMsg> getEventList() {
        return msgList;
    }

    @Override
    protected int getEventMaxCount() {
        return 5;
    }

    public void addMsg(FOFMsg msg) {
        addEvent(msg);
    }

    public int getMsgCount() {
        return getEventCount();
    }

    public FOFMsg getMsg(int index) {
        return getEvent(index);
    }
}
