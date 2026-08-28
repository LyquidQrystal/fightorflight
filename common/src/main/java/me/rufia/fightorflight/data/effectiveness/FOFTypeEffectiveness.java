package me.rufia.fightorflight.data.effectiveness;

import me.rufia.fightorflight.utils.FOFUtils;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FOFTypeEffectiveness {
    public static Map<String, List<FOFTypeEffectiveness>> TYPE_EFFECTIVENESS = new HashMap<>();
    private final List<String> id;
    private final List<String> entityTypeTag;
    private final List<String> attachedElementalType;
    private final List<String> weakness;
    private final List<String> resistance;
    private final List<String> immune;
    private final boolean ignoredByMoldBreaker;

    public FOFTypeEffectiveness(List<String> id, List<String> entityTypeTag, List<String> attachedElementalType, List<String> weakness, List<String> resistance, List<String> immune, boolean ignoredByMoldBreaker) {
        this.id = id;
        this.entityTypeTag = entityTypeTag;
        this.attachedElementalType = attachedElementalType;
        this.weakness = weakness;
        this.resistance = resistance;
        this.immune = immune;
        this.ignoredByMoldBreaker = ignoredByMoldBreaker;
    }

    public List<String> getId() {
        return id;
    }

    public List<String> getEntityTypeTag() {
        return entityTypeTag;
    }

    public List<String> getAttachedElementalType() {
        return attachedElementalType;
    }

    public List<String> getWeakness() {
        return weakness;
    }

    public List<String> getResistance() {
        return resistance;
    }

    public List<String> getImmune() {
        return immune;
    }

    public boolean isIgnoredByMoldBreaker() {
        return ignoredByMoldBreaker;
    }

    public boolean testTarget(LivingEntity target) {
        if (entityTypeTag.isEmpty()) {
            return true;
        }
        for (String tag : entityTypeTag) {
            if (!FOFUtils.findEntityTypeTag(target, tag)) {
                return false;
            }
        }
        return true;
    }
}
