### 0.10中间漏的几个我直接写到b站懒得复制过来了。直接看我b站动态吧。关注bilibili LyquidQrystal喵，关注LyquidQrystal谢谢喵
# V0.9.0:
这个版本只支持方块宝可梦1.6和1.6.1！支持方块宝可梦1.7的新版本将在几天后发布。  
Cobblemon 1.7已经发布了，0.9.0将成为最后一个支持方块宝可梦1.6.1且增加了新特性的版本。但是我仍然会修复2026.1.1之前提交的bug。
### 新特性：
* 与电光一闪类似的有先制度的近战技能会在宝可梦与目标有一定距离且宝可梦正在尝试使用这一类技能的时候快速接近目标（其实是传送）
* 部分技能会在目标脚底创造一个风暴。这个风暴会在被创建一段时间后触发。
* 部分技能会在目标脚底创造一个漩涡。它在激活后会伤害范围内的敌人并减缓它们的行动
* 宝可梦的体型（更准确的说是碰撞箱的半径）会影响上述范围技能的作用半径
### 新的config:
* quick_attack_like_move: 机制和电光一闪类似的技能
* delayed_aoe_at_target_position: 通过创建范围伤害区域造成伤害的技能
* delayed_aoe_can_float: 可以在空中使用的AOE技能，如果一个技能被包括在上面但不包括在这个列表，则这些技能在攻击空中的目标时只能在它们下方的地面上使用。与之相反，如果技能在这个列表的话则可以在空中目标的位置上使用。
* delayed_aoe_rise_up_tornado: 暴风类技能。这类技能的碰撞箱的高度高于漩涡类，意味着这类技能可以更有效的攻击空中目标
* delayed_aoe_bounding_whirlpool: 漩涡类技能。伤害目标并减速。
* delayed_aoe_is_instant: 技能效果和伤害只进行一次判定的技能。可以比较漩涡类技能和暴风类技能

### 旧的懒得翻了直接看我b站动态写好的吧。关注bilibili LyquidQrystal喵，关注LyquidQrystal谢谢喵
# V0.8.4:
### New Features:
* Player's Pokemon can hurt the ender dragon now.(Recommended to use with some riding addon.)
### New Config:
* player_pokemon_can_hurt_ender_dragon:If set to true, your pokemon can hurt ender dragon.
* wild_pokemon_can_hurt_ender_dragon:If set to true, wild pokemon can hurt ender dragon.
* aggressive_pokemon_catchable: If set to true, aggressive will be not catchable outside the battle.
* force_wild_battle_on_player_hurt: If set to true, a battle will be started when hurt by a wild Pokemon.
* force_player_battle_on_player_hurt: If set to true, a battle will be started when hurt by a player's Pokemon.
* force_player_battle_check_team: If this is set to true, only the players of different team can start a battle by using the pokemon to attack the player/the player's pokemon.
### Bug Fixes:
* Fixed the bug that force_wild_battle_on_pokemon_hurt is not working correctly
# v0.6.2
This is just a build using the last commit before I was trying to update to 1.21.1. It should help to solve the problem that the forge version is behaving differently from the fabric version. It's been over 10 months so I can't remember the differences very well.
# V0.8.3
#### Attention:
The way we calculate the aggression is changed greatly. If you're enabling Pokemon's proactive attack, the Pokemon might be more/less aggressive than before. I'm sorry for any possible inconvenience I may cause, but it's an important change I have to made to make the config edit easier to understand.
### New Features:
* Implemented Spikes and its variations(Toxic Spikes, Stealth Rock and Sticky Web)
* Player's Pokemon can throw the spikes now.
  * Stone Axe and Ceaseless Edge can create stealth rock/spikes, too.
* Status moves need to be used manually now.(Select them with the Poke Staff and right click/press the hotkey you set again)
* Using the taunt moves(Taunt, Torment, Rage Powder, Follow Me) will force the mobs targeting the owner to attack the Pokemon.
* * Reworked aggression system.
  * It is the sum of 5 values now. Most of them have an editable base value and multiplier to allow you have a deeper customization. The threshold is editable, too. More detailed information is written in the config file.(keywords that you might use when searching through the config file: aggression_level, aggression_atk_def_dif, aggression_light_level, aggression_nature, aggression_intimidation)
### Bug Fixes:
* Fixed the bug that the cooldown of unselected moves is unusually fast.
* Fixed the bug that can_use_held_item is not working correctly.
* Fixed the bug that the bullet/ball projectiles didn't explode when hitting mobs.
* Fixed the bug that the color of the bullet/ball projectiles wasn't influenced by the move's type.
* Fixed the bug that friendly fire related configs are not working as expected.
# V0.8.2
### New Features:
* If you installed LivelierPokemon, the cat Pokemon will attack Creeper proactively.(can be disabled in the config, the cat pokemon category is in livelierpokemon-categories.json5 if you installed that mod.)
#### New Config:
* can_use_held_item. If the pokemon can use held item(This config has the highest priority)
* can_use_held_item_damage_influencing. If the pokemon can use damage-increasing held item
* can_use_held_item_hp_influencing. If the pokemon can use hp-restoring/damage-inflicting held item(Life Orb not included)
### Bug Fixes:
* Pokemon's explosion won't accidentally destroy gravestone from Pneumono_'s mod.(It's the only one reported in the issue, might be useful for the other blocks that shouldn't be destroyed by explosion)
# v0.8.1
### New Features:
* Added tooltip for Oran Lucky Egg
* Rocky helmet(Held Item)/Rough Skin(Ability)/Iron Barbs(Ability) hurts the attacker now.
* Whenever the Pokémon holding Shell Bell deals damage with a move, it restores HP equal to 1/8 of the damage dealt now.
* Pokemon holding Assault Vest won't try to use status moves now.
* Pokemon holding Leftovers will be healed slowly.
* Pokemon holding Sticky Barb will be hurt slowly. The holder can't be changed on contact currently.
* Poison type Pokemon holding Black Sludge will be healed slowly. The other Pokemon will be hurt slowly.
### Changes:
* Some damage types is changed to indirect magic to avoid being recognized as making contact with a Pokemon.
### Bug Fixes:
* Fixed the log spam when the player enters the spectator mode and the Pokemon is out.
* Fixed the bug that the projectiles could cause friendly fire.
### Misc:
* More optimization, possibly solving performance issue.

# v0.8.0
### New Features:
* Player's pokemons can use the status move that raises the user's stats to enhance themselves now(The move needs to be selected by the player). 
* Added new config pokemon_proactive_level to stop the pokemon from attacking neutural mobs that are not hostile to the owner.
* Added new config should_check_poke_staff to set if the player needs to hold the Poke Staff to use the command keybinds.
### Bug Fixes:
* The cooldown of the not selected moves can be reduced correctly now.
### Misc:
* Optimized a part of the algorithm, possibly solving performance issues

# v0.7.9
### New features:
* Moves use independent cooldown time now.
* There will be an internal cooldown(10 ticks, 0.5s) when switching the moves and the move's cooldown is lower than 10 ticks.
* The cooldown of moves with a charging turn(Solar Beam) or moves that require recharging(Hyper Beam) will be doubled.
### Bug Fixes:
* Fix the bug that the arrow_projectile might cause a crash
* Fix the bug that the melee attack and range attack can't change fluently.

# v0.7.8
### New features:
* Added configs to enable/disable the move indicator, adjust its position and size.
### Bug Fixes:
* Fixed the log spam on the neoforge side.
* Fixed the bug that range attack won't trigger the battle when the config is enabled.
# v0.7.7
### New features:
- Type effectiveness for Pokemon: 2x damage for super effective moves, 0.5x damage for not very super effective moves and 0.1x damage for no effect. 
- Adaptability can enhance the STAB now.
- Added a new indicator located in the lower right corner of the screen that shows the move's name, type and cooldown.
- New config options: slow_down_after_hurt slows the pokemon after being attacked, an alternative choice of stop_running_after_hurt.(Disabled by default)
- New config options: activate_type_effect use the classical type effect that was used in the original version and before v0.7.5.(Disabled by default)
- New config options: activate_move_effect use the move effect added in v0.7.5, you can enable them together.(Enabled by default)
- New config options: all_pokemon_targeting_whitelist, wild_pokemon_targeting_whitelist and player_owned_pokemon_targeting_whitelist
### Fixes:
- Probably fixed the bug that some pokemon's move can't be recognized if it learns it at a low level.
## v0.7.6
A small update that fixes some small bug before I start working on the other features.
### New Features:
- New config options: A config option to disable failed captures counted as provocation
### Fixes:
- Bug fixes:Fix the bug that health_sync_for_wild_pokemon is disabled when set to true
    - If I just revert it, every player has to edit the config to use their preferred choice. To avoid that, it will be renamed to enable_health_sync_for_wild_pokemon so you won't need to edit the config if you use the default setting.
- Bug fixes:Fix the bug that attack_damage_player is not working.
- Bug fixes:Fix the bug that the tracing projectile is being influenced by the gravity when tracing the target.
## v0.7.5
### New Features:
- New config options: light_dependent_unprovoked_attack: The aggression system will only work in the dark areas if enabled.(Similar to the spiders in Minecraft, disabled by default)
- New config options: do_pokemon_defend_creeper_proactive: Player owned pokemon can attack creeper proactively.(disabled by default.)
- Combat overhaul
    - Remove the type effects(levitate for psychic, weakness for fight,etc.)
    - Give more special effect to different moves(Stat changing/Status related attack moves)
        - Pokemon gain strength after using Power-up Punch, gain weakness and resistance weakened(a new effect added by myself) after using Close Combat,etc.
            - I want to make it easy so the effect level WON'T stack like the core series.
        - Moves that can apply status conditions can apply status effects from Minecraft:
            - Burn -> Weakness & set the entity on fire.
            - Poison -> Poison
            - Badly Poison -> Poison II
            - Freeze & Sleep -> Mining Fatigue II & Slowness III & Increase the frozen time.
            - Paralysis -> Mining Fatigue & Slowness
            - Flinch -> Mining Fatigue & Slowness II
            - Confusion -> Confusion
        - Serene Grace can increase the chance to trigger the additional effect
        - Sheer Force no longer boost all the moves. It works like the core series now.(Some moves are not supported yet. Sparkling Aria can trigger Sheer Force in Pokemon S/V, but it can't be learnt by the Pokemon which has Sheer Force, so I didn't add it.)
### Changes 
- Pokemon on shoulders should stop targeting now.
### Fixes:
- Bug fixes: The tracing projectiles should work correctly now.
- Bug fixes: The explosive projectiles can deal the damage properly now.
- Bug fixes: The invulnerable time should work properly for pokemon entity now;

## v0.6.1
* **Animation Support** Support for animations from cobblemon mod when attacking(These animations are not designed for this mod so it might be weird)
* Wild Pokemon cries correctly when provoked.
* Player's Pokemon can taunt wild Pokemon.
* Added a new hotkey that let your pokemon start a battle with the pokemon that tries to attack you.
* Some abilities(intimidate, unnerve, pressure) can lower the nearby pokemon's aggro.
* The Wimpod line Pokemon will be recalled when taking damage and the health is below 50%.
* Using move outside battle can be used to evolve a Pokemon like Annihilape.
* Pokemon aiming optimization, increasing the accuracy.
* More specific move classification.
* The projectiles of ball and bomb moves can cause a small explosion that don't break the blocks.
* Balance tweaks.
* Bug fixes.
## v0.6.0
- **Lower Pokemon Damage:** I noticed that some players commented on the curseforge page that the pokemon damage was too high ,so I lowered the default value of the maximum damage.
- **Configurable aggresion:** Added a multiplier so that you can multiply the level of the pokemon when calculating its aggresion.
- **Faster Pokemon:** Pokemon with a higher speed stat can run faster.(can be changed in the config)
- **Range attack!:** Added a range attack for pokemon whose Sp.ATK is higher than its ATK.
- - Wild pokemon are not allowed to use the range attack.(can be enabled in the config)
- **Different ways of range attack:** If a pokemon has some special moves,they will shoot different bullet.
- - The moves' type and power will influence the projectile's if the moves is a special move.However, if your pokemon doesn't have these moves, the type of the projectile will be based on the pokemon's primary type and the power will be set to 60(can be changed in the config).
- - You can use the Poke Staff to select the move you want to use, even forcing a special attacker to melee!(use JEI to check the recipe)
- **Special effect for moves**
- - The panicked pokemon can teleport to a nearby position if it learns the teleport move.(can be disabled in the config)
- - Player's pokemon will be recalled automatically when using moves like U-turn and hitting the target(melee)/shooting(range)
- - Explosive moves can cause an explosion.
- **Mobs killed by your pokemon will drop items and experience like it was killed by a tamed wolf.**
- Your pokemon can gain experience and ev by killing pokemon without starting a pokemon battle(needs to be **the last mob** that deals the damage,can be disabled in the config)
- Adds the Oran Lucky Egg(held item) to gain more experience from pokemon killed by your pokemon,right-click your pokemon while sneaking to give the item to the pokemon.(**The Oran Lucky Egg won't give you extra xp from any other ways!**)