package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.utils.FOFTargetPriority;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Auto battle order from the poke staff. Priority {@link FOFTargetPriority#AUTO_BATTLE}: it is beaten
 * by player orders, owner defense and provocation, and only fills an empty slot.
 */
public class PokeStaffAutoBattleSensor extends FOFPrioritizedTargetSensor {
    public PokeStaffAutoBattleSensor() {
        super(10);
    }

    @Override
    public FOFTargetPriority priority() {
        return FOFTargetPriority.AUTO_BATTLE;
    }

    @Override
    protected boolean wantsTarget(PokemonEntity entity) {
        if (!CobblemonFightOrFlight.commonConfig().can_use_poke_staff
                || !CobblemonFightOrFlight.commonConfig().enable_auto_battle_command) {
            return false;
        }
        if (!(entity.getOwner() instanceof Player)) {
            return false;
        }
        if (PokemonUtils.getCommandMode(entity) != PokeStaffComponent.CMDMODE.AUTO_BATTLE) {
            return false;
        }
        int requiredFriendship = CobblemonFightOrFlight.commonConfig().auto_battle_friendship_requirement;
        return requiredFriendship <= 0 || entity.getPokemon().getFriendship() >= requiredFriendship;
    }

    @Override
    protected @Nullable LivingEntity findTarget(PokemonEntity entity, @NotNull NearestVisibleLivingEntities visible) {
        return visible.findClosest(livingEntity -> {
            if (livingEntity instanceof PokemonEntity targetPokemon) {
                if (PokemonUtils.tryToAvoidWildShiny(targetPokemon)) {
                    return false;
                }
                Pokemon thisPKM = entity.getPokemon();
                Pokemon targetPKM = targetPokemon.getPokemon();
                if (thisPKM.getLevel() < targetPKM.getLevel() && CobblemonFightOrFlight.commonConfig().auto_battle_weaker_pokemon_only) {
                    return false;
                }
                return targetPokemon.getOwner() == null;
            }
            return false;
        }).orElse(null);
    }
}
