package me.rufia.fightorflight.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "fightorflight_visual_effect")
public class FightOrFlightVisualEffectConfigModel implements ConfigData {
    @Comment("Moves that spawn angry_villager particle around the pokemon")
    public final String[] self_angry_moves = {
            "partingshot",
            "lashout",
            "ragingfury",
            "thrash",
            "outrage",
            "temperflare"
    };
    @Comment("Moves that spawn soul_fire_flame particle around the pokemon's target")
    public final String[] target_soul_fire_moves = {
            "willowisp",
            "infernalparade",
            "bitterblade"
    };
    @Comment("Moves that spawn soul particle around the pokemon's target")
    public final String[] target_soul_moves = {
            "nightshade",
            "astralbarrage"
    };
    @Comment("Slicing moves")
    public final String[] slicing_moves = {
            "cut", "razorleaf", "slash", "furycutter", "aircutter", "aerialace", "leafblade", "nightslash",
            "airslash", "x-scissor", "psychocut", "crosspoison", "sacredsword", "razorshell", "secretsword",
            "solarblade", "behemothblade", "stoneaxe", "ceaselessedge", "populationbomb", "kowtowcleave",
            "psyblade", "bitterblade", "aquacutter", "mightycleave", "tachyoncutter", "spacialrend"
    };
    @Comment("If you want to enable the move indicator?")
    public final boolean enable_move_indicator = true;
    @Comment("Set the size of the move indicator")
    public final float move_indicator_size = 0.5f;
    @Comment("Set the horizontal position of the move indicator.(0-1,0 is the left side, 1 is the right side)")
    public final float move_indicator_x_relative = 0.9f;
    @Comment("Set the horizontal position of the move indicator.(0-1,0 is the top, 1 is the bottom)")
    public final float move_indicator_y_relative = 0.9f;
}
