package me.rufia.fightorflight.utils.msg;

import net.minecraft.network.chat.MutableComponent;

public abstract class FOFMsg {
    protected int duration;
    protected int createTime;

    public abstract MutableComponent getMessageText();

    public void setDuration(int ticks) {
        duration = ticks;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isAvailable(int guiTick) {
        return guiTick - createTime < duration;
    }

    public int getCreateTime(){
        return createTime;
    }
}
