package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FOFSensors {
    public static final Map<String, SensorType<?>> sensors = new HashMap<>();
    public static SensorType<Sensor<PokemonEntity>> POKEMON_HELP_OWNER = register("pokemon_help_owner_fof", PokemonHelpOwnerSensor::new);
    public static SensorType<Sensor<PokemonEntity>> POKEMON_WILD_PROACTIVE = register("pokemon_wild_proactive_fof",PokemonWildProactiveSensor::new);

    public static <E extends LivingEntity, U extends Sensor<E>> SensorType<U> register(String id, Supplier<U> supplier) {
        var sensorType = new SensorType<>(supplier);
        sensors.putIfAbsent(id, sensorType);
        return sensorType;
    }
}
