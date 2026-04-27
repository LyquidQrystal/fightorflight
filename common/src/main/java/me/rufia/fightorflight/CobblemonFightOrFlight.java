package me.rufia.fightorflight;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.architectury.registry.ReloadListenerRegistry;
import me.rufia.fightorflight.compat.CobblemonSizeVariationCompat;
import me.rufia.fightorflight.config.FightOrFlightCommonConfigModel;
import me.rufia.fightorflight.config.FightOrFlightMoveConfigModel;
import me.rufia.fightorflight.config.FightOrFlightVisualEffectConfigModel;
import me.rufia.fightorflight.data.movedata.movedatas.MiscMoveData;
import me.rufia.fightorflight.net.CobblemonFightOrFlightNetwork;
import me.rufia.fightorflight.utils.FOFAggressionCalculator;
import me.rufia.fightorflight.utils.FOFUtils;
import me.rufia.fightorflight.utils.TargetingWhitelist;
import me.rufia.fightorflight.utils.listeners.MoveDataListener;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.Mob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class CobblemonFightOrFlight {
    public static final String MODID = "fightorflight";
    public static final String COBBLEMON_MOD_ID = "cobblemon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    private static FightOrFlightCommonConfigModel commonConfig;
    private static FightOrFlightMoveConfigModel moveConfig;
    private static FightOrFlightVisualEffectConfigModel visualEffectConfig;
    public static CobblemonSizeVariationCompat sizeVariationCompat;
    //private static TriConsumer<PokemonEntity, Integer, Goal> goalAdder;


    public static FightOrFlightCommonConfigModel commonConfig() {
        return commonConfig;
    }

    public static FightOrFlightMoveConfigModel moveConfig() {
        return moveConfig;
    }

    public static FightOrFlightVisualEffectConfigModel visualEffectConfig() {
        return visualEffectConfig;
    }

    public static void init(Predicate<String> modCompatPredicate) {
        sizeVariationCompat = new CobblemonSizeVariationCompat();
        sizeVariationCompat.tryLoad(modCompatPredicate);
        AutoConfig.register(FightOrFlightCommonConfigModel.class, JanksonConfigSerializer::new);
        AutoConfig.register(FightOrFlightMoveConfigModel.class, JanksonConfigSerializer::new);
        AutoConfig.register(FightOrFlightVisualEffectConfigModel.class, JanksonConfigSerializer::new);
        commonConfig = AutoConfig.getConfigHolder(FightOrFlightCommonConfigModel.class).getConfig();
        moveConfig = AutoConfig.getConfigHolder(FightOrFlightMoveConfigModel.class).getConfig();
        visualEffectConfig = AutoConfig.getConfigHolder(FightOrFlightVisualEffectConfigModel.class).getConfig();
        CobblemonFightOrFlightNetwork.init();
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new MoveDataListener(), ResourceLocation.fromNamespaceAndPath(MODID, "movedata"));
        TargetingWhitelist.init();
    }

    @Deprecated
    public static void addPokemonGoal(PokemonEntity pokemonEntity) {
        /*
        float minimum_movement_speed = CobblemonFightOrFlight.commonConfig().minimum_movement_speed;
        float maximum_movement_speed = CobblemonFightOrFlight.commonConfig().maximum_movement_speed;
        float speed_limit = CobblemonFightOrFlight.commonConfig().speed_stat_limit;
        float speed = pokemonEntity.getPokemon().getSpeed();
        float speedMultiplier = Mth.lerp(speed / speed_limit, minimum_movement_speed, maximum_movement_speed);

        float fleeSpeed = 1.3f * speedMultiplier;
        float pursuitSpeed = 1.2f * speedMultiplier;

        goalAdder.accept(pokemonEntity, 2, new PokemonGoToPosGoal(pokemonEntity, pursuitSpeed));
        goalAdder.accept(pokemonEntity, 3, new PokemonAttackGoal(pokemonEntity, pursuitSpeed));
        goalAdder.accept(pokemonEntity, 4, new PokemonAvoidGoal(pokemonEntity, PokemonUtils.getAttackRadius() * 3, 1.0f, fleeSpeed));
        goalAdder.accept(pokemonEntity, 4, new PokemonPanicGoal(pokemonEntity, fleeSpeed));
         */
    }

    public static double getFightOrFlightCoefficient(PokemonEntity pokemonEntity) {
        if (!CobblemonFightOrFlight.commonConfig().do_pokemon_attack) {
            return FOFAggressionCalculator.getPeacefulValue();
        }

        Pokemon pokemon = pokemonEntity.getPokemon();
        String speciesName = pokemon.getSpecies().getName().toLowerCase();
        Set<String> pokemonAspects = pokemon.getAspects();
        double height = pokemonEntity.position().y;

        if (SpeciesNeverAggro(speciesName) || SpeciesAlwaysFlee(speciesName)) {
            return FOFAggressionCalculator.getPeacefulValue();
        }

        if (SpeciesAlwaysAggro(speciesName) || AspectsAlwaysAggro(pokemonAspects) || BelowAlwaysAggro(height)) {
            return FOFAggressionCalculator.getAggressiveValue();
        }

        List<String> peacefulBiomeList = Arrays.stream(CobblemonFightOrFlight.commonConfig().peaceful_biome).toList();
        List<String> neutralBiomeList = Arrays.stream(CobblemonFightOrFlight.commonConfig().neutral_biome).toList();
        List<String> aggressiveBiomeList = Arrays.stream(CobblemonFightOrFlight.commonConfig().aggressive_biome).toList();
        String biomeName = pokemonEntity.level().getBiome(pokemonEntity.blockPosition()).getRegisteredName();
        if (!peacefulBiomeList.isEmpty() && peacefulBiomeList.contains(biomeName)) {
            return FOFAggressionCalculator.getPeacefulValue();
        }
        if (!neutralBiomeList.isEmpty() && neutralBiomeList.contains(biomeName)) {
            return FOFAggressionCalculator.getNeutralValue();
        }
        if (!aggressiveBiomeList.isEmpty() && aggressiveBiomeList.contains(biomeName)) {
            return FOFAggressionCalculator.getAggressiveValue();
        }

        return FOFAggressionCalculator.calc(pokemonEntity);
    }

    public static boolean BelowAlwaysAggro(double height) {
        return height < CobblemonFightOrFlight.commonConfig().always_aggro_below;
    }

    public static boolean AspectsAlwaysAggro(Set<String> pokemonAspects) {
        // Retrieve the list of always aggressive features from the config
        String[] alwaysAggroFeatures = CobblemonFightOrFlight.commonConfig().always_aggro_aspects;

        // Convert the array to a Set for faster lookup
        Set<String> aggroFeatureSet = new HashSet<>(Arrays.asList(alwaysAggroFeatures));

        // Check if any of the pokemon aspects are in the aggroFeatureSet
        for (String aspect : pokemonAspects) {
            if (aggroFeatureSet.contains(aspect)) {
                return true;
            }
        }
        return false;
    }

    public static boolean SpeciesAlwaysAggro(String speciesName) {
        for (String aggroSpecies : CobblemonFightOrFlight.commonConfig().always_aggro) {
            if (aggroSpecies.equals(speciesName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean SpeciesNeverAggro(String speciesName) {
        for (String passiveSpecies : CobblemonFightOrFlight.commonConfig().never_aggro) {
            if (passiveSpecies.equals(speciesName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean SpeciesAlwaysFlee(String speciesName) {
        return Arrays.stream(commonConfig().always_flee).toList().contains(speciesName);
    }

    public static float AUTO_AGGRO_THRESHOLD() {
        return commonConfig().aggressive_threshold;
    }

    public static void fromConfigToMoveData() {
        for (String moveName : moveConfig().taunting_moves) {
            MiscMoveData data = new MiscMoveData("self", "on_use", 1f, false, "taunt");
            //CobblemonFightOrFlight.LOGGER.info(moveName);
            FOFUtils.registerMoveData(moveName, data);
        }
    }

    public static void PokemonEmoteAngry(Mob mob) {
        double particleSpeed = Math.random();
        double particleAngle = Math.random() * 2 * Math.PI;
        double particleXSpeed = Math.cos(particleAngle) * particleSpeed;
        double particleYSpeed = Math.sin(particleAngle) * particleSpeed;

        if (mob.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                    mob.position().x, mob.getBoundingBox().maxY, mob.position().z,
                    1,
                    particleXSpeed, 0.5d, particleYSpeed,
                    1.0f);
        } else {
            mob.level().addParticle(ParticleTypes.ANGRY_VILLAGER,
                    mob.position().x, mob.getBoundingBox().maxY, mob.position().z,
                    particleXSpeed, 0.5d, particleYSpeed);
        }
    }
}