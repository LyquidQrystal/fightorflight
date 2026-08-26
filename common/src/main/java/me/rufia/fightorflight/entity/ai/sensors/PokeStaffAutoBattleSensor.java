package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class PokeStaffAutoBattleSensor extends Sensor<PokemonEntity> {
    public PokeStaffAutoBattleSensor() {
        super(10);
    }

    @Override
    protected void doTick(ServerLevel level, PokemonEntity entity) {
        if (!CobblemonFightOrFlight.commonConfig().can_use_poke_staff || !CobblemonFightOrFlight.commonConfig().enable_auto_battle_command) {
            return;
        }
        var owner = entity.getOwner();
        if (owner instanceof Player) {
            if (PokemonUtils.getCommandMode(entity) != PokeStaffComponent.CMDMODE.AUTO_BATTLE) {
                return;
            }
            int requiredFriendship = CobblemonFightOrFlight.commonConfig().auto_battle_friendship_requirement;
            if (requiredFriendship > 0 && entity.getPokemon().getFriendship() < requiredFriendship) {
                return;
            }
            entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent(
                    (visibleLivingEntities -> {
                        findNearestTarget(entity, visibleLivingEntities, owner);
                    })
            );
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    private void findNearestTarget(PokemonEntity pokemonEntity, NearestVisibleLivingEntities visibleMobs, LivingEntity owner) {
        var possibleTarget = visibleMobs.findClosest(livingEntity -> {
            if (livingEntity instanceof PokemonEntity targetPokemon) {
                if (PokemonUtils.tryToAvoidWildShiny(targetPokemon)) {
                    return false;
                }
                Pokemon thisPKM = pokemonEntity.getPokemon();
                Pokemon targetPKM = targetPokemon.getPokemon();
                if (thisPKM.getLevel() < targetPKM.getLevel() && CobblemonFightOrFlight.commonConfig().auto_battle_weaker_pokemon_only) {
                    return false;
                }
                return targetPokemon.getOwner() == null;
            }
            return false;
        });
        var noTarget = pokemonEntity.getBrain().checkMemory(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT);
        if (noTarget) {
            possibleTarget.ifPresent(livingEntity -> pokemonEntity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, livingEntity));
        }
    }
}
