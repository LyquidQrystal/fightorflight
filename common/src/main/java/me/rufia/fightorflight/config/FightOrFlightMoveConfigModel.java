package me.rufia.fightorflight.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "fightorflight_moves")
public class FightOrFlightMoveConfigModel implements ConfigData {
    @ConfigEntry.Category("Special attack moves")
    @Comment("The multiplier of the move power in calculating damage(The final damage can't be higher than the value in the config)")
    public float move_power_multiplier = 1.0f;
    @Comment("The multiplier for the moves that don't make contacts or shoot projectiles when calculating damage(The final damage can't be higher than the value in the config.These moves are hard to avoid in an open area so the damage should be slightly lower than the others)")
    public float indirect_attack_move_power_multiplier = 0.8f;
    @Comment("If a pokemon doesn't have the correct moves to use,the base power will be used to calculate the damage.")
    public int base_power = 60;
    @Comment("The minimum radius of the AoE moves")
    public float min_AoE_radius = 1.5f;
    @Comment("The maximum radius of the AoE moves")
    public float max_AoE_radius = 3.0f;
    @Comment("The AoE damage will be lower to the target away from the center,this value sets the lowest damage multiplier of the AOE damage")
    public float min_AoE_damage_multiplier = 0.6f;
    @Comment("The radius of the status moves(unused)")
    public float status_move_radius = 8.0f;
    @Comment("Taunting moves are needed to taunt the aggressive wild pokemon")
    public boolean taunt_moves_needed = true;
    @Comment("Wild pokemon can taunt your pokemon")
    public boolean wild_pokemon_taunt = false;
    @Comment("Special moves that makes contact,these moves will let the Pokemon melee while using the Special Attack stat to calculate the damage.")
    public String[] special_contact_moves = {
            "electrodrift",
            "infestation",
            "drainingkiss",
            "grassknot",
            "wringout",
            "trumpcard",
            "petaldance"
    };
    @Comment("Physical moves that uses arrow,these moves will let the Pokemon shoot arrow while using the Attack stat to calculate the damage.")
    public String[] physical_single_arrow_moves = {
            "dragondarts",
            "iceshard",
            "iciclecrash",
            "iciclespear",
            "gunkshot",
            "scaleshot",
            "spikecannon",
            "pinmissile",
            "thousandarrows"
    };
    @Comment("Moves that shoots multiple bullet")
    public String[] multiple_bullet_moves = {
            "bulletseed"
    };
    @Comment("Moves that shoots single bullet.Physical moves still shoots the bullet and uses the Attack stat to calculate the damage.")
    public String[] single_bullet_moves = {
            "electroball",
            "focusblast",
            "weatherball",
            "pyroball",
            "acidspray",
            "sludgebomb",
            "mudbomb",
            "pollenpuff",
            "shadowball",
            "searingshot",
            "octazooka",
            "energyball",
            "zapcannon",
            "mistball",
            "syrupbomb",
            "armorcannon",
            "seedbomb",
            "magnetbomb",
            "powergem",
            "rockwrecker",

            "sludge",
            "technoblast",
            "fusionflare"
    };
    @Comment("Moves that shoots multiple tracing bullet")
    public String[] multiple_tracing_bullet_moves = {
            "dracometeor",
            "ancientpower",
            "infernalparade"
    };
    @Comment("Moves that shoots single tracing bullet")
    public String[] single_tracing_bullet_moves = {
            "aurasphere"
    };
    @Comment("Moves that shoots single beam or pulse")
    public String[] single_beam_moves = {
            "signalbeam",
            "chargebeam",
            "icebeam",
            "eternabeam",
            "solarbeam",
            "moongeistbeam",
            "aurorabeam",

            "waterpulse",
            "darkpulse",
            "dragonpulse",
            "terrainpulse",
            "healpulse",

            "freezingglare",

            "watergun",
            "hydropump",
            "prismaticlaser",
            "lusterpurge",
            "electroshot",
            "photongeyser",
            "flashcannon",

            "flamethrower",
            "mysticalfire",
            "oblivionwing",
            "snipeshot"
    };
    @Comment("Moves that hurt a target without any projectiles or blast.(The AOE moves will be implemented in another way)")
    public String[] magic_attack_moves = {
            "absorb",
            "gigadrain",
            "megadrain",
            "confusion",
            "psychic",
            "freezedry",
            "earthpower",
            "nightdaze",
            "iceburn",
            "luminacrash",
            "extrasensory",
            "hex",
            "seedflare",
            "psyshock",
            "psystrike",
            "psychoboost"
    };
    @Comment("Moves that hurts the entity(including the allies) around the pokemon")
    public String[] self_centered_aoe_moves = {
            "earthquake",
            "surf",
            "lavaplume",
            "discharge",
            "sludgewave",
            "magnitude",
            "bulldoze",
            "petalblizzard",
            "corrosivegas"
    };
    @Comment("Moves that drains HP(50% of the damage dealt)")
    public String[] hp_draining_moves_50 = {
            "absorb",
            "bitterblade",
            "bouncybubble",
            "drainpunch",
            "gigadrain",
            "hornleech",
            "leechlife",
            "megadrain",
            "matchagotcha",
            "paraboliccharge"
    };
    @Comment("Moves that drains HP(75% of the damage dealt)")
    public String[] hp_draining_moves_75 = {
            "drainingkiss",
            "oblivionwing"
    };
    @Comment("Moves that ignores abilities(unused)")
    public String[] mold_breaker_like_moves = {
            "sunsteelstrike",
            "moongeistbeam",
            "photongeyser"
    };
    @Comment("Moves that start an explode")
    public String[] explosive_moves = {
            "selfdestruct",
            "explosion",
            "mindblown",
            "mistyexplosion"
    };
    @Comment("Moves that use sound to attack")
    public String[] sound_based_moves = {
            "snore",
            "uproar",
            "hypervoice",
            "bugbuzz",
            "chatter",
            "round",
            "echoedvoice",
            "relicsong",
            "snarl",
            "disarmingvoice",
            "boomburst",
            "sparklingaria",
            "clangingscales",
            "clangoroussoulblaze",
            "overdrive",
            "eeriespell",
            "torchsong",
            "alluringvoice",
            "psychicnoise",
            "roaroftime"
    };
    @Comment("Moves that can switch your pokemon")
    public String[] switch_moves = {
            "teleport",
            "batonpass",
            "uturn",
            "partingshot",
            "voltswitch",
            "flipturn"
    };
    @Comment("Abilities that forces your Pokemon to switch when it is below 50% HP")
    public String[] emergency_exit_like_abilities = {
            "emergencyexit",
            "wimpout"
    };
    @Comment()
    public String[] recoil_moves_allHP = {
            "selfdestruct",
            "explosion",
            "mindblown",
            "mistyexplosion"
    };
    //TODO
    @ConfigEntry.Category("Status moves(WIP)")
    @Comment("Enable the Pokemon to use the spikes.")
    public boolean enable_spikes = true;
    @Comment("The base damage of Spikes and Stealth Rock")
    public float spike_basic_damage = 3f;
    @Comment("Moves that taunt other pokemon")
    public String[] taunting_moves = {
            "taunt",
            "followme",
            "ragepowder",
            "torment"
    };
    @Comment("Moves that burns the pokemon(unused)")
    public String[] burn_status_move = {
            "willowisp"
    };
    @Comment("Status moves that are used on the user.")
    public String[] self_targeting_status_move = {
            "bulkup",
            "meditate",
            "coil",
            "calmmind",
            "takeheart",

            "charge",
            "defensecurl",
            "harden",
            "withdraw",

            "slackoff",
            "synthesis",
            "milkdrink",
            "healorder",
            "recover",
            "morningsun",
            "moonlight",
            "softboiled",
            "roost",

            "geomancy",

            "tidyup",
            "dragondance",

            "cosmicpower",
            "defendorder",

            "barrier",
            "irondefense",
            "shelter",
            "acidarmor",
            "amnesia",

            "rockpolish",
            "agility",
            "autotomize",

            "shiftgear",

            "cottonguard",

            "shellsmash",

            "victorydance",
            "quiverdance",

            "honeclaws",
            "sharpen",
            "workup",
            "growth",

            "swordsdance",
            "nastyplot",
            "tailglow"
    };
    @ConfigEntry.Category("Pokemon Griefing")
    @Comment("Do Pokemon grief(only explosions currently)?")
    public boolean pokemon_griefing = false;
    @Comment("Fire type attack(only explosions currently) should burn the ground")
    public boolean should_create_fire = false;
}
