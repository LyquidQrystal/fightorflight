package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.CobblemonSensors;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

public class FOFSensors {
    public static SensorType<Sensor<PokemonEntity>> POKEMON_HELP_OWNER = CobblemonSensors.register("pokemon_help_owner_fof", PokemonHelpOwnerSensor::new);

}
