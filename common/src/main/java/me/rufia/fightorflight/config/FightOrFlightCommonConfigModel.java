package me.rufia.fightorflight.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "fightorflight")
public class FightOrFlightCommonConfigModel implements ConfigData {

    @ConfigEntry.Category("Wild Pokemon Aggression")
    @Comment("Do more aggressive Pokemon fight back when provoked?")
    public boolean do_pokemon_attack = true;
    @Comment("Do especially aggressive Pokemon attack unprovoked?")
    public boolean do_pokemon_attack_unprovoked = false;
    @Comment("If the aggressive pokemon will only attack unprovoked in the dark area.(similar to the spider in Minecraft,do_pokemon_attack_unprovoked needs to be set to true")
    public boolean light_dependent_unprovoked_attack = true;
    @Comment("If failed captures can be counted as provocation?")
    public boolean failed_capture_counted_as_provocation = true;
    @Comment("Do aggro Pokemon attack their targets even if they're in the middle of a battles?")
    public boolean do_pokemon_attack_in_battle = false;
    @Comment("If the aggressive pokemon can be caught by Poke Ball.")
    public boolean aggressive_pokemon_catchable = false;
    @Comment("The minimum level a Pokemon needs to be to fight back when provoked.")
    public int minimum_attack_level = 5;
    @Comment("The minimum level a Pokemon needs to be to attack unprovoked.")
    public int minimum_attack_unprovoked_level = 10;
    @Comment("If a wild Pokemon's aggro is above this value, it will attack the player proactively")
    public float aggressive_threshold = 100f;
    @Comment("If a wild Pokemon's aggro is above this value and lower than the aggressive_threshold, it will fight back when attacked,if the Pokemon's aggro is lower than this, it will just run away.")
    public float neutral_threshold = 20f;
    @Comment("The value of the aggression if the Pokemon's level reaches 100.")
    public float aggression_level_base_value = 40f;
    @Comment("The multiplier used to calculate the wild Pokemon's aggression,lower value can make the high level Pokemon less aggressive. Editing the base value is enough, This is kept for the old config files. This still works, though")
    public float aggression_level_multiplier = 1.0f;
    @Comment("The value of the aggression if [(Atk + Sp. Atk)-(Def + Sp. Def)]/level = 1. The Pokemon level won't be affected by the multiplier above.")
    public float aggression_atk_def_dif_base_value = 30f;
    @Comment("Are Dark types more aggressive at or below light level 7 and less aggressive at or above light level 12?")
    public boolean dark_light_level_aggro = true;
    @Comment("Are Ghost types more aggressive at or below light level 7 and less aggressive at or above light level 12?")
    public boolean ghost_light_level_aggro = true;
    @Comment("The value of the aggression if the conditions above are met")
    public float aggression_light_level_base_value = 30f;
    @Comment("The value of the aggression influenced by the Pokemon's nature.")
    public float aggression_nature_base_value = 40f;
    @Comment("The value of the aggression gained if a Pokemon is intimidated")
    public float aggression_intimidation_base_value = -30f;
    @Comment("Pokemon below this map height (y) will always be aggressive.")
    public double always_aggro_below = -128;
    @Comment("Pokemon stops running away if the hp is not full.")
    public boolean stop_running_after_hurt = true;
    @Comment("Wild pokemon will be slow down if the hp is not full.")
    public boolean slow_down_after_hurt = false;
    @Comment("Pokemon with these natures are slightly more aggressive.")
    public String[] aggressive_nature = {"sassy", "hardy", "bold", "impish", "hasty"};
    @Comment("The aggression multiplier for natures above.")
    public float aggressive_nature_multiplier = 1;
    @Comment("Pokemon with these natures are much more aggressive.")
    public String[] more_aggressive_nature = {"brave", "rash", "adamant", "naughty"};
    @Comment("The aggression multiplier for natures above.")
    public float more_aggressive_nature_multiplier = 2f;
    @Comment("Pokemon with these natures are slightly more peaceful.")
    public String[] peaceful_nature = {"relaxed", "lax", "quiet", "bashful", "calm"};
    @Comment("The aggression multiplier for natures above.")
    public float peaceful_nature_multiplier = -1;
    @Comment("Pokemon with these natures are much more peaceful.")
    public String[] more_peaceful_nature = {"docile", "timid", "gentle", "careful"};
    @Comment("The aggression multiplier for natures above.")
    public float more_peaceful_nature_multiplier = -2f;
    @Comment("If the behavior data will work?(Still W.I.P, not avialable yet)")
    public boolean enable_datapack_driven_behavior = true;
    @Comment("Forms that will always be aggressive")
    public String[] always_aggro_aspects = {
            "alolan"
    };
    @Comment("Pokemon that will always be aggressive")
    public String[] always_aggro = {
            "mankey",
            "primeape"
    };
    @Comment("Pokemon that will never be aggressive, priority over always aggresive species/forms")
    public String[] never_aggro = {
            "slowpoke",
            "pyukumuku"
    };
    @Comment("Pokemon that will be aggressive only when provoked(WIP)")
    public String[] provoke_only_aggro = {

    };
    @Comment("Pokemon that will always flee away from the player, priority over always aggresive species/forms")
    public String[] always_flee = {
            "wimpod"
    };
    @Comment("Abilities that will reduce wild pokemon's aggro")
    public String[] aggro_reducing_abilities = {
            "intimidate",
            "unnerve",
            "pressure"
    };
    @Comment("Abilities that works like Mold Breaker(unused)")
    public String[] mold_breaker_like_ablilities = {
            "moldbreaker",
            "turboblaze",
            "teravolt"
    };
    @Comment("Whitelist for all pokemon.(won't influence the Poke Staff)")
    public String[] all_pokemon_targeting_whitelist = {
            "minecraft:villager"
    };
    @Comment("Whitelist for wild pokemon.")
    public String[] wild_pokemon_targeting_whitelist = {

    };
    @Comment("Whitelist for player owned pokemon.(won't influence the Poke Staff)")
    public String[] player_owned_pokemon_targeting_whitelist = {

    };
    @Comment("Allow the Pokemon to use the teleport move to flee if the Pokemon had learnt it?")
    public boolean allow_teleport_to_flee = true;

    @ConfigEntry.Category("Player Pokemon Defence")
    @Comment("Do player Pokemon defend their owners when they attack or are attacked by other mobs?")
    public boolean do_pokemon_defend_owner = true;
    @Comment("Do player Pokemon defend their owners proactively? (follows the same rules as Iron Golems!!!)")
    public boolean do_pokemon_defend_proactive = true;
    @Comment("0 zero means follow the same rule as Iron Golem. 1 means ignores neutral mobs that are not aiming at the player.")
    public int pokemon_proactive_level = 1;
    @Comment("Do player Pokemon defend their owners from Creepers proactively?(Iron Golems won't attack Creeper proactively.")
    public boolean do_pokemon_defend_creeper_proactive = false;
    @Comment("If the player's cat pokemon will attack the creeper proactively(LivelierPokemon required, this one is not functioning anymore currently) ")
    public boolean do_cat_pokemon_defend_creeper_proactive = true;
    @Comment("Can player Pokemon target other players?")
    public boolean do_player_pokemon_attack_other_players = false;
    @Comment("Can player Pokemon target other player's Pokemon?")
    public boolean do_player_pokemon_attack_other_player_pokemon = false;
    @Comment("Will the wild pokemon cries for several times when angered,set to false so the pokemon will only cry one time when it's angered")
    public boolean multiple_cries = true;
    @Comment("Tick(1/20s by default) needed for the pokemon to cry again(it will only work when the multiple_cries is set to true)")
    public int time_to_cry_again = 100;
    @ConfigEntry.Category("Pokemon yield")
    @Comment("How much experience a pokemon can get by killing a pokemon without a battle? Set to 0 for no experience outside standard pokemon battles.")
    public float experience_multiplier = 0.5f;
    @Comment("Your pokemon can gain EV points by killing a pokemon without a battle?")
    public boolean can_gain_ev = true;
    @Comment("If the Pokemon can evolve by using the move out of a Pokemon Battle? For example Primeape can use Rage Fist 20x to evolve without a traditional Pokemon Battle.")
    public boolean can_progress_use_move_evoluiton = true;
    @ConfigEntry.Category("Pokemon Damage and Effects")
    @Comment("Pokemons should be immune to suffocation damage? (cant die to wall damage, like when sand drops into them)")
    public boolean suffocation_immunity = true;
    @Comment("Should player-owned pokemons be immune to damage from all players?")
    public boolean pvp_immunity = true;
    @Comment("Should player-owned pokemons be immune to damage from mobs on the same team?")
    public boolean friendly_fire_immunity_team = true;
    @Comment("Should player-owned pokemons be immune to damage from the owner (friendly fire)?")
    public boolean friendly_fire_immunity_owner = true;
    @Comment("The maximum damage bonus can get if it reached the maximum stat(the physical and the special moves both share one value currently)")
    public float max_bonus_from_stat = 4.0f;
    @Comment("The minimum damage wild pokemon could do on hit")
    public float minimum_attack_damage = 1.0f;
    @Comment("The maximum damage wild pokemon could do on hit")
    public float maximum_attack_damage = 50.0f;
    @Comment("Minimum damage multiplier player-owned pokemon would do")
    public float minimum_attack_damage_player = 1.2f;
    @Comment("Damage multiplier player-owned pokemon would do")
    public float attack_damage_player = 1.2f;
    @Comment("Maximum damage multiplier player-owned pokemon would do")
    public float maximum_attack_damage_player = 1.2f;
    @Comment("Attack stat required to reach the maximum damage,the default value is calculated with 50 level, 130 stat, 252 EVs, IVs of 31, and a helpful nature.")
    public int maximum_attack_stat = 200;
    @Comment("The movement speed multiplier of a pokemon if the Speed stat of this Pokemon is 0.")
    public float minimum_movement_speed = 1.3f;
    @Comment("The movement speed multiplier of a pokemon if the Speed stat of this Pokemon reaches the value in the config.")
    public float maximum_movement_speed = 2.0f;
    @Comment("The speed stat required for a pokemon to reach the highest fleeing and pursuing speed.The default value(548) is the max speed stat of a lvl.100 Regieleki with a beneficial nature.")
    public int speed_stat_limit = 548;
    @Comment("Use the classic type effect after hitting the enemy.(Levitate for the psychic type move,regeneration for grass type move)")
    public boolean activate_type_effect = false;
    @Comment("Use the move effect after hitting the enemy(Inferno will always burn the target, Flamethrower has a 10% chance to burn the target.)")
    public boolean activate_move_effect = true;
    @Comment("The maximum damage reduction wild pokemon can get from its defense/special defense(uses the highest one)")
    public float max_damage_reduction_multiplier = 0.4f;
    @Comment("The multiplier for player-owned Pokemon for maximum damage reduction")
    public float max_damage_reduction_multiplier_player = 1.2f;
    @Comment("The highest defense stat needed to get the highest damage reduction.")
    public int defense_stat_limit = 161;
    @Comment("When a player owned Pokemon hurts or is hurt by a wild pokemon, should a pokemon battle be started?")
    public boolean force_wild_battle_on_pokemon_hurt = false;
    @Comment("When a player owned Pokemon hurts or is hurt by another player's pokemon, should a pokemon battle be started?")
    public boolean force_player_battle_on_pokemon_hurt = false;
    @Comment("When a player is hurt by a wild pokemon, should a pokemon battle be started?")
    public boolean force_wild_battle_on_player_hurt = false;
    @Comment("When a player is hurt by another player's pokemon, should a pokemon battle be started?")
    public boolean force_player_battle_on_player_hurt = false;
    @Comment("If this is set to true, only the players of different teams can start a battle")
    public boolean force_player_battle_check_team = true;
    @ConfigEntry.Category("Pokemon Ranged Attack")
    @Comment("If wild pokemon can use the ranged attack.")
    public boolean wild_pokemon_ranged_attack = false;
    @Comment("The minimum time between pokemons ranged attacks. In seconds.")
    public float minimum_ranged_attack_interval = 1.0f;
    @Comment("The maximum time between pokemons ranged attacks. In seconds.")
    public float maximum_ranged_attack_interval = 3.0f;
    @Comment("The minimum time between pokemons melee attacks. In seconds.")
    public float minimum_melee_attack_interval = 1.0f;
    @Comment("The maximum time between pokemons melee attacks. In seconds.")
    public float maximum_melee_attack_interval = 3.0f;
    @Comment("Minimum damage wild pokemon would do with ranged attacks")
    public float minimum_ranged_attack_damage = 1.0f;
    @Comment("Maximum damage wild pokemon would do with ranged attacks")
    public float maximum_ranged_attack_damage = 50.0f;
    @Comment("Minimum damage multiplier player-owned pokemon would do with ranged attacks")
    public float minimum_ranged_attack_damage_player = 1.2f;
    @Comment("Damage multiplier player-owned pokemon would do with ranged attacks")
    public float ranged_attack_damage_player = 1.2f;
    @Comment("Maximum damage multiplier player-owned pokemon would do with ranged attacks")
    public float maximum_ranged_attack_damage_player = 1.2f;
    @Comment("Special attack stat required to reach the maximum damage,the default value is calculated with 50 level, 130 stat, 252 EVs, IVs of 31, and a helpful nature.")
    public int maximum_special_attack_stat = 200;
    @ConfigEntry.Category("Pokemon Damage Multiplier(misc),These modifiers doesn't stack currently")
    @Comment("If the pokemon type effectiveness will be activated outside standard Pokemon battle")
    public boolean type_effectiveness_between_pokemon = true;
    @Comment("Multiplier for super effective damage against the other Pokemon")
    public float super_effective_multiplier = 2f;
    @Comment("Multiplier for not very effective damage against the other Pokemon")
    public float not_very_effective_multiplier = 0.5f;
    @Comment("Multiplier for no effect damage against the other Pokemon")
    public float no_effect_multiplier = 0.1f;
    @Comment("Water type damage will be more effective on mobs like Blaze,Enderman,etc.")
    public float water_type_super_effective_dmg_multiplier = 2.0f;
    @Comment("Fire type damage will be not very effective against fire immune entity.(set to 0 if you want a complete immune)")
    public float fire_type_no_effect_dmg_multiplier = 0.1f;
    @Comment("Ice type damage will be not very effective against entity that can't be frozen.(set to 0 if you want a complete immune)")
    public float ice_type_no_effect_dmg_multiplier = 0.1f;
    @Comment("Ice type damage will be more effective against entity that has a weaker resistant to the frost damage.")
    public float ice_type_super_effective_dmg_multiplier = 2.0f;
    @Comment("Poison type damage will be not very effective against undead mobs.(set to 0 if you want a complete immune)")
    public float poison_type_no_effect_dmg_multiplier = 0.1f;
    @ConfigEntry.Category("Health Calculation & Synchronization")
    @Comment("If you want to use FOF health mechanic. A different curve that was used in FOF previously. The following configurations needs this one to work.")
    public boolean shouldOverrideHealthMechanic = true;
    @Comment("If you want to use the FOF style hp calculation.")
    public boolean use_fof_style_hp_calculation = true;
    @Comment("If health sync will work on the wild pokemon, set to false will heal them on standard pokemon battle start if they were damaged before.")
    public boolean enable_health_sync_for_wild_pokemon = false;
    @Comment("The minimum hp of a pokemon outside standard pokemon battle,shedinja is set to 1.0 and can't be changed.")
    public int min_HP = 8;
    @Comment("The medium hp value of a pokemon outside standard pokemon battle,the medium value is designed to allow you to better tweak the growth of HP value for the entity")
    public int mid_HP = 40;
    @Comment("The maximum hp of a pokemon outside standard pokemon battle.")
    public int max_HP = 100;
    @Comment("Pokemon HP stat above this value will increase its HP outside standard pokemon battle.")
    public int min_HP_required_stat = 20;
    @Comment("HP stat needed to get medium HP outside standard pokemon battle.")
    public int mid_HP_required_stat = 160;
    @Comment("HP stat needed to get maximum HP outside standard pokemon battle. The max hp of a Blissey is 714.")
    public int max_HP_required_stat = 500;
    @ConfigEntry.Category("Poke Staff")
    @Comment("If Poke Staff can be used.")
    public boolean can_use_poke_staff = true;
    @Comment("The Pokemon stays still after finishing the move command.")
    public boolean stay_after_move_command = true;
    @Comment("If the player need to hold a Poke Staff to use the command keybind")
    public boolean should_check_poke_staff = true;
    @ConfigEntry.Category("Held item")
    @Comment("If the pokemon can use held item(This config has the highest priority)")
    public boolean can_use_held_item = true;
    @Comment("If the pokemon can use damage-increasing held item")
    public boolean can_use_held_item_damage_influencing = true;
    @Comment("If the pokemon can use hp-restoring/damage-inflicting held item(Life Orb not included)")
    public boolean can_use_held_item_hp_influencing = true;
    @Comment("If the player's pokemon can hurt ender dragon")
    public boolean player_pokemon_can_hurt_ender_dragon = true;
    @Comment("If the wild poekmon can hurt ender dragon")
    public boolean wild_pokemon_can_hurt_ender_dragon = false;
    @Comment("If you want to use the FOF style melee attack?")
    public boolean use_fof_style_melee = true;
    @Comment("If you want to use range attack from FOF")
    public boolean use_range_attack = true;
    @Comment("If you want to set willDefendOwner to true in Pokemon's combat behaviour of Cobblemon, should be useful for solving the problem that player's Pokemon added by the datapacks can't attack.")
    public boolean force_enable_defend_owner = true;
    @Comment("If you want to set willDefendSelf to true in Pokemon's combat behaviour of Cobblemon, should be useful for solving the problem that wild Pokemon added by the datapacks can't attack.")
    public boolean force_enable_defend_self = true;
}
