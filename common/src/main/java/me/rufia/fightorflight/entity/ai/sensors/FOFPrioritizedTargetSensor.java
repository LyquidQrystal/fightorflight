package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.utils.FOFTargetClaims;
import me.rufia.fightorflight.utils.FOFTargetPriority;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Base class for sensors that write ATTACK_TARGET: picking a target and writing it are separated, and
 * every write goes through {@link FOFTargetClaims}, so a subclass only decides "do I still want a
 * target" and "who is it".
 *
 * <p>Contracts for subclasses:</p>
 * <ul>
 *     <li>{@link #wantsTarget} holds only "I do not want a target any more" conditions (config switch,
 *     command mode, owner state). Returning false hands the claim back.</li>
 *     <li>{@link #findTarget} holds "I cannot get one right now" conditions (busy, target out of sight).
 *     Returning null keeps the existing claim, so a single lost sighting does not drop the target.</li>
 * </ul>
 */
public abstract class FOFPrioritizedTargetSensor extends Sensor<PokemonEntity> {
    private final int scanRate;

    protected FOFPrioritizedTargetSensor() {
        this(10);
    }

    protected FOFPrioritizedTargetSensor(int scanRate) {
        super(scanRate);
        this.scanRate = scanRate;
    }

    /** Priority this sensor competes with for ATTACK_TARGET. */
    public abstract FOFTargetPriority priority();

    /** Whether a target is still wanted. Returning false gives the claim back. */
    protected abstract boolean wantsTarget(PokemonEntity entity);

    /** Pick a target out of the visible entities, or null when there is none this round. */
    protected abstract @Nullable LivingEntity findTarget(PokemonEntity entity, @NotNull NearestVisibleLivingEntities visible);

    @Override
    protected void doTick(ServerLevel level, PokemonEntity entity) {
        //vanilla never forgets a dead target on its own
        FOFTargetClaims.clearDeadTarget(entity);
        if (!wantsTarget(entity)) {
            FOFTargetClaims.release(entity, priority());
            return;
        }
        var visible = entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        if (visible.isEmpty()) {
            return;
        }
        LivingEntity target = findTarget(entity, visible.get());
        if (target == null) {
            return;
        }
        FOFTargetClaims.tryClaim(entity, target, priority(), leaseTicks());
    }

    @Override
    public @NotNull Set<MemoryModuleType<?>> requires() {
        //every subclass picks targets out of NEAREST_VISIBLE_LIVING_ENTITIES, so declare it once here
        return Set.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    protected int leaseTicks() {
        return scanRate * 2;
    }
}
