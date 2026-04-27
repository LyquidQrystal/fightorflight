package me.rufia.fightorflight.utils;

//This class is used to save the move data on the server side temporarily. The function getMoveFromName will get the move using the name from this class
public class FOFMove {
    private final String name;
    private int remainingCooldown;//attackTime
    private int originalCooldown;//maxAttackTime

    public FOFMove(String name, int remainingCooldown, int originalCooldown) {
        this.name = name;
        this.remainingCooldown = remainingCooldown;
        this.originalCooldown = originalCooldown;
    }

    public String getName() {
        return name;
    }

    public int getRemainingCooldown() {
        return remainingCooldown;
    }

    public int getOriginalCooldown() {
        return originalCooldown;
    }

    public void setRemainingCooldown(int cooldown) {
        remainingCooldown = cooldown;
    }

    public void setOriginalCooldown(int cooldown) {
        originalCooldown = cooldown;
    }
}
