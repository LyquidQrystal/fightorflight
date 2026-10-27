package me.rufia.fightorflight.utils.event;

public abstract class FOFAbstractEvent {
    protected int duration;
    protected int createTime;

    public void setDuration(int ticks) {
        duration = ticks;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isAvailable(int tick) {
        return tick - createTime < duration;
    }

    public int getCreateTime() {
        return createTime;
    }

    public FOFAbstractEvent(int createTime, int duration) {
        this.createTime = createTime;
        this.duration = duration;
    }
}
