package me.rufia.fightorflight.utils.event.msg;

import me.rufia.fightorflight.utils.event.FOFAbstractEvent;
import net.minecraft.network.chat.MutableComponent;

public abstract class FOFMsg extends FOFAbstractEvent {
    public FOFMsg(int createTime, int duration) {
        super(createTime, duration);
    }

    public abstract MutableComponent getMessageText();
}
