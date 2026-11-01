package me.rufia.fightorflight.utils;

/**
 * Claim priority for {@code MemoryModuleType.ATTACK_TARGET}.
 *
 * <p>A higher weight is a stronger claim: a lower priority sensor cannot overwrite the target a
 * higher priority sensor is holding. That keeps writes to ATTACK_TARGET deterministic even though
 * the Brain iterates its sensors in {@code HashMap} hash order, which cannot be relied on.</p>
 *
 * <p>High to low: player command &gt; self defense &gt; defend owner &gt; provoked &gt; auto battle
 * &gt; wild unprovoked aggro.</p>
 */
public enum FOFTargetPriority {
    /** Unprovoked aggro of a wild Pokemon. Lowest, only fills an empty slot. */
    WILD_PROACTIVE(20),
    /** Auto battle command from the poke staff. */
    AUTO_BATTLE(50),
    /** Grudge after a failed capture (provoked). */
    PROVOKED(70),
    /** Owner is under attack: fight back for them / inherit their target. */
    OWNER_DEFENSE(80),
    /** Got hit: retaliate. */
    SELF_DEFENSE(90),
    /** Attack order aimed with the poke staff. Highest. */
    COMMAND(100);

    private final int weight;

    FOFTargetPriority(int weight) {
        this.weight = weight;
    }

    public int weight() {
        return weight;
    }

    public boolean isHigherThan(FOFTargetPriority other) {
        return this.weight > other.weight;
    }
}
