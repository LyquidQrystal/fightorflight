package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PokemonHelpOwnerSensor extends Sensor<PokemonEntity> {
    private static final int REFRESH_RATE = 1200;

    public PokemonHelpOwnerSensor() {
        super(10);
    }

    @Override
    protected void doTick(ServerLevel level, PokemonEntity entity) {
        if (!CobblemonFightOrFlight.commonConfig().do_pokemon_defend_owner) {
            return;
        }
        var owner = entity.getOwner();
        if (owner != null) {
            entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent(visibleLivingEntities -> setNearestTarget(entity, visibleLivingEntities, owner));
        }
    }

    @Override
    public @NotNull Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    private void setNearestTarget(PokemonEntity pokemonEntity, NearestVisibleLivingEntities visibleMobs, LivingEntity owner) {
        //Inherit the target. lastHurtBy will be cleared automatically
        int ownerLastHurtTick = ((PokemonInterface) pokemonEntity).getOwnerLastHurtTick();
        var ownerLastHurtTarget = ((PokemonInterface) pokemonEntity).getOwnerLastHurt();
        if (ownerLastHurtTarget != null && ownerLastHurtTarget.isAlive() && pokemonEntity.tickCount - ownerLastHurtTick < REFRESH_RATE) {
            pokemonEntity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, ownerLastHurtTarget);
            return;
        }
        //Finding target
        var nearestAttacker = visibleMobs.findClosest(livingEntity -> {
            var lastHurtByMob = livingEntity.getLastHurtByMob();
            if (lastHurtByMob instanceof PokemonEntity pokemonEntity1) {
                if (pokemonEntity1.getPokemon().getShiny() && !pokemonEntity1.getPokemon().isPlayerOwned() && CobblemonFightOrFlight.commonConfig().not_attacking_wild_shiny) {
                    return false;
                }
            }
            return lastHurtByMob != null && lastHurtByMob.is(owner);
        });
        //Trying to set target
        LivingEntity livingEntity = nearestAttacker.orElse(null);
        ((PokemonInterface) pokemonEntity).setOwnerLastHurt(livingEntity);
        if (livingEntity != null) {
            pokemonEntity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, nearestAttacker);
        }
    }
}
