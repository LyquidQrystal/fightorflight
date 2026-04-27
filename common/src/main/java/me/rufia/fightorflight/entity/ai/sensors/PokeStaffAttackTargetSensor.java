package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokeStaffAttackTargetSensor extends Sensor<PokemonEntity> {
    String targetUUID;

    public PokeStaffAttackTargetSensor() {
        super(10);
    }

    @Override
    protected void doTick(ServerLevel level, PokemonEntity entity) {
        if (!CobblemonFightOrFlight.commonConfig().can_use_poke_staff) {
            return;
        }
        var owner = entity.getOwner();
        if (owner instanceof Player && !entity.isBusy()) {
            if (tryLoadingCommandData(entity)) {
                entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent(
                        (visibleLivingEntities -> {
                            setTarget(entity, visibleLivingEntities);
                        })
                );
            }
        }
    }


    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    private boolean tryLoadingCommandData(PokemonEntity pokemonEntity) {
        if (PokemonUtils.getCommandMode(pokemonEntity).equals(PokeStaffComponent.CMDMODE.ATTACK)) {
            String data = ((PokemonInterface) pokemonEntity).getCommandData();
            if (data.startsWith("ENTITY_")) {
                Pattern pattern = Pattern.compile("ENTITY_([a-z\\d]{8}-[a-z\\d]{4}-[a-z\\d]{4}-[a-z\\d]{4}-[a-z\\d]{12})");
                Matcher m = pattern.matcher(data);
                if (m.find()) {
                    targetUUID = m.group(1);
                    return true;
                }
            }
        }

        return false;
    }

    private void setTarget(PokemonEntity pokemonEntity, NearestVisibleLivingEntities entities) {
        var targetOpt = entities.findClosest(
                livingEntity -> {
                    return livingEntity.getStringUUID().equals(targetUUID);
                }
        );
        targetOpt.ifPresent(livingEntity -> pokemonEntity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, livingEntity));
    }
}
