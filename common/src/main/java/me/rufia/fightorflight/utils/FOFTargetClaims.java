package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Arbitration for writes to ATTACK_TARGET.
 *
 * <p>Nothing outside of this class should write that memory (sensors go through
 * {@code FOFPrioritizedTargetSensor}, behavior tasks call it directly); writes go through here so
 * that "who owns the target" is decided by priority instead of by the order in which the Brain
 * happens to tick its sensors and tasks.</p>
 *
 * <p>Rules:</p>
 * <ul>
 *     <li>no claim / claim no longer valid -&gt; the write goes through</li>
 *     <li>holding claim has higher priority -&gt; refused</li>
 *     <li>same priority and same target -&gt; allowed (that is a renewal)</li>
 *     <li>same priority but a different target -&gt; refused (first come first served, no flapping)</li>
 *     <li>holding claim has lower priority -&gt; the write takes over</li>
 * </ul>
 *
 * <p>Claims are leased: a write records {@code expireTick} and every successful write renews it.
 * When the holder stops qualifying (no longer ticking, conditions no longer met, target dead, or the
 * memory overwritten by someone else) the claim lapses within two scan periods. No reverse
 * notification between holders is needed.</p>
 *
 * <p>Server main thread only: every sensor doTick and behavior tick runs there.</p>
 */
public final class FOFTargetClaims {
    private static final int MIN_LEASE_TICKS = 20;
    /**
     * Lease used by the one-shot behavior tasks. They claim once and never renew, so it has to
     * outlast a normal fight (5 minutes) without turning into a permanent lock.
     */
    public static final int TASK_CLAIM_LEASE = 6000;
    private static final Map<PokemonEntity, Claim> CLAIMS = new WeakHashMap<>();

    private FOFTargetClaims() {
    }

    private record Claim(FOFTargetPriority priority, int targetId, WeakReference<LivingEntity> target, long expireTick) {
        boolean holds(LivingEntity livingEntity) {
            return livingEntity != null && livingEntity.getId() == targetId;
        }
    }

    /**
     * Try to claim {@code target} for {@code entity}. On success ATTACK_TARGET has already been written.
     *
     * @param leaseTicks lease length, callers normally pass twice their scan rate
     * @return whether the claim succeeded
     */
    public static boolean tryClaim(PokemonEntity entity, LivingEntity target, FOFTargetPriority priority, int leaseTicks) {
        if (target == null || !target.isAlive()) {
            return false;
        }
        long now = entity.level().getGameTime();
        Claim claim = liveClaim(entity, now);
        if (claim != null) {
            boolean sameTarget = claim.targetId() == target.getId();
            if (claim.priority().isHigherThan(priority)) {
                return false;
            }
            if (claim.priority() == priority && !sameTarget) {
                return false;
            }
        }
        entity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        CLAIMS.put(entity, new Claim(priority, target.getId(), new WeakReference<>(target),
                now + Math.max(leaseTicks, MIN_LEASE_TICKS)));
        return true;
    }

    /**
     * Hand back our own claim. Only does something when the current claim belongs to {@code priority},
     * and only clears ATTACK_TARGET when it still points at the target we claimed.
     */
    public static void release(PokemonEntity entity, FOFTargetPriority priority) {
        Claim claim = CLAIMS.get(entity);
        if (claim == null || claim.priority() != priority) {
            return;
        }
        eraseIfHeldBy(entity, claim);
        CLAIMS.remove(entity);
    }

    /** Priority of the current claim on this entity, or {@code null} when it holds none (debugging aid). */
    public static FOFTargetPriority currentPriority(PokemonEntity entity) {
        Claim claim = liveClaim(entity, entity.level().getGameTime());
        return claim == null ? null : claim.priority();
    }

    /**
     * Erase ATTACK_TARGET when it points at a dead or removed entity.
     *
     * <p>Vanilla only forgets memories that have expired ({@code Brain#forgetOutdatedMemories} checks
     * {@code ExpirableValue.hasExpired()}), so a corpse stays in memory until something else writes
     * over it. Every FOF target sensor calls this, which also covers targets that Cobblemon's own
     * goals set without going through the arbiter.</p>
     *
     * <p>Note that the first sensor of a tick does the work and the rest find nothing to do, so a
     * caller must not treat this as a per-sensor "your target died" callback.</p>
     */
    public static void clearDeadTarget(PokemonEntity entity) {
        var brain = entity.getBrain();
        var memory = brain.getMemory(MemoryModuleType.ATTACK_TARGET);
        if (memory.isEmpty()) {
            return;
        }
        LivingEntity target = memory.get();
        if (target.isAlive() && !target.isRemoved()) {
            return;
        }
        Claim claim = CLAIMS.get(entity);
        if (claim != null && claim.targetId() == target.getId()) {
            CLAIMS.remove(entity);
        }
        brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
    }

    private static Claim liveClaim(PokemonEntity entity, long now) {
        Claim claim = CLAIMS.get(entity);
        if (claim == null) {
            return null;
        }
        boolean stale = now > claim.expireTick();
        var memory = entity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (memory.isEmpty() || memory.get().getId() != claim.targetId()) {
            //memory was cleared or taken over by another writer, so the claim is void
            stale = true;
        } else {
            LivingEntity target = claim.target().get();
            //check liveness while the reference is alive; if it was collected (entity unloaded) let the lease lapse
            if (target != null && !target.isAlive()) {
                stale = true;
            }
        }
        if (stale) {
            eraseIfHeldBy(entity, claim);
            CLAIMS.remove(entity);
            return null;
        }
        return claim;
    }

    private static void eraseIfHeldBy(PokemonEntity entity, Claim claim) {
        var brain = entity.getBrain();
        var memory = brain.getMemory(MemoryModuleType.ATTACK_TARGET);
        if (memory.isPresent() && claim.holds(memory.get())) {
            brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
        }
    }
}
