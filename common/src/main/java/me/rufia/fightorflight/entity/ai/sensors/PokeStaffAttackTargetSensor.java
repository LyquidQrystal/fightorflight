package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.utils.FOFTargetPriority;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attack order aimed with the poke staff. Highest priority ({@link FOFTargetPriority#COMMAND});
 * the target is only given back once the order is cancelled, switched to another mode, or its
 * target dies.
 */
public class PokeStaffAttackTargetSensor extends FOFPrioritizedTargetSensor {
    private static final Pattern ENTITY_PATTERN = Pattern.compile("ENTITY_([a-z\\d]{8}-[a-z\\d]{4}-[a-z\\d]{4}-[a-z\\d]{4}-[a-z\\d]{12})");

    private String targetUUID;
    /**
     * Last entity instance resolved for {@link #targetUUID}. The death of the ordered target has to be
     * read from this reference, not from ATTACK_TARGET: another sensor or behavior may already have
     * rewritten that memory by the time this sensor ticks, and then the corpse is invisible to us.
     */
    private WeakReference<LivingEntity> commandedTarget;

    public PokeStaffAttackTargetSensor() {
        super(10);
    }

    @Override
    public FOFTargetPriority priority() {
        return FOFTargetPriority.COMMAND;
    }

    @Override
    protected boolean wantsTarget(PokemonEntity entity) {
        if (!CobblemonFightOrFlight.commonConfig().can_use_poke_staff) {
            return false;
        }
        if (!(entity.getOwner() instanceof Player)) {
            return false;
        }
        if (!tryLoadingCommandData(entity)) {
            return false;
        }
        if (isCommandedTargetDead()) {
            //the ordered target is gone: drop the order instead of standing around in ATTACK mode forever
            targetUUID = null;
            commandedTarget = null;
            PokemonUtils.clearCommand(entity);
            return false;
        }
        return true;
    }

    @Override
    protected @Nullable LivingEntity findTarget(PokemonEntity entity, @NotNull NearestVisibleLivingEntities visible) {
        //busy with another order this round: no target, but the claim stays
        if (entity.isBusy()) {
            return null;
        }
        var uuid = targetUUID;
        if (uuid == null) {
            return null;
        }
        LivingEntity target = visible.findClosest(livingEntity -> livingEntity.getStringUUID().equals(uuid)).orElse(null);
        if (target != null) {
            commandedTarget = new WeakReference<>(target);
        }
        return target;
    }

    @Override
    protected int leaseTicks() {
        //a longer lease for player orders, so being busy for a moment does not throw the order away
        return 100;
    }

    /**
     * Whether the entity this order points at has died. Bound to the uuid so a reference left over
     * from an older order can never cancel a new one.
     */
    private boolean isCommandedTargetDead() {
        var ref = commandedTarget;
        LivingEntity target = ref == null ? null : ref.get();
        return target != null && !target.isAlive() && target.getStringUUID().equals(targetUUID);
    }

    private boolean tryLoadingCommandData(PokemonEntity pokemonEntity) {
        if (PokemonUtils.getCommandMode(pokemonEntity) == PokeStaffComponent.CMDMODE.ATTACK) {
            String data = ((PokemonInterface) pokemonEntity).getCommandData();
            Matcher m = ENTITY_PATTERN.matcher(data);
            if (m.find()) {
                targetUUID = m.group(1);
                return true;
            }
        }
        //order gone or switched to another mode: drop the cached uuid so the base class releases the claim
        targetUUID = null;
        return false;
    }
}
