package me.rufia.fightorflight.entity;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.categories.DamageCategories;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.data.movedata.MoveData;
import me.rufia.fightorflight.entity.projectile.AbstractPokemonProjectile;
import me.rufia.fightorflight.entity.projectile.PokemonArrow;
import me.rufia.fightorflight.entity.projectile.PokemonBullet;
import me.rufia.fightorflight.entity.projectile.PokemonTracingBullet;
import me.rufia.fightorflight.utils.PokemonMultipliers;
import me.rufia.fightorflight.utils.PokemonUtils;
import me.rufia.fightorflight.utils.TypeEffectiveness;
import me.rufia.fightorflight.utils.explosion.FOFExplosion;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.List;
import java.util.*;

public class PokemonAttackEffect {
    public static SimpleParticleType getParticleFromType(String name) {
        return switch (name) {
            case "fire" -> ParticleTypes.FLAME;
            case "ice" -> ParticleTypes.SNOWFLAKE;
            case "poison" -> ParticleTypes.MYCELIUM;
            case "psychic" -> ParticleTypes.PORTAL;
            case "fairy" -> ParticleTypes.CHERRY_LEAVES;
            case "fighting", "ground", "rock" -> ParticleTypes.POOF;
            case "steel" -> ParticleTypes.WAX_OFF;
            case "ghost" -> ParticleTypes.SOUL;
            case "dark" -> ParticleTypes.SMOKE;
            case "electric" -> ParticleTypes.ELECTRIC_SPARK;
            case "bug" -> ParticleTypes.SPORE_BLOSSOM_AIR;
            case "grass" -> ParticleTypes.COMPOSTER;
            case "dragon" -> ParticleTypes.DRAGON_BREATH;
            case "flying" -> ParticleTypes.SWEEP_ATTACK;
            case "water" -> ParticleTypes.SPLASH;
            case "normal" -> ParticleTypes.CRIT;
            default -> ParticleTypes.CRIT;
        };
    }

    public static Color getColorFromType(String typeName) {
        return switch (typeName) {
            case "fire" -> new Color(230, 40, 41);
            case "ice" -> new Color(63, 216, 255);
            case "poison" -> new Color(145, 65, 203);
            case "psychic" -> new Color(239, 65, 121);
            case "fairy" -> new Color(239, 112, 239);
            case "fighting" -> new Color(255, 128, 0);
            case "steel" -> new Color(96, 161, 184);
            case "ghost" -> new Color(112, 65, 112);
            case "dark" -> new Color(80, 65, 63);
            case "ground" -> new Color(145, 81, 33);
            case "rock" -> new Color(175, 169, 129);
            case "electric" -> new Color(250, 192, 0);
            case "bug" -> new Color(145, 161, 25);
            case "grass" -> new Color(63, 161, 41);
            case "dragon" -> new Color(80, 96, 225);
            case "flying" -> new Color(129, 185, 239);
            case "water" -> new Color(41, 128, 239);
            case "normal" -> new Color(159, 161, 159);
            default -> new Color(68, 104, 94);
        };
    }

    public static SimpleParticleType getParticleFromType(ElementalType type) {
        return getParticleFromType(type.getName());
    }

    public static Color getColorFromType(ElementalType type) {
        return getColorFromType(type.getName());
    }

    public static Color getColorFromType(Pokemon pokemon) {
        return getColorFromType(pokemon.getPrimaryType());
    }

    public static float calculatePokemonDamage(PokemonEntity pokemonEntity, Entity target, boolean isSpecial) {
        return calculatePokemonDamage(pokemonEntity, target, isSpecial, (float) CobblemonFightOrFlight.moveConfig().base_power, null);
    }

    public static float calculatePokemonDamage(PokemonEntity pokemonEntity, Entity target, boolean isSpecial, float movePower, ElementalType type) {
        int attack = isSpecial ? pokemonEntity.getPokemon().getSpecialAttack() : pokemonEntity.getPokemon().getAttack();
        int maxStat = isSpecial ? CobblemonFightOrFlight.commonConfig().maximum_special_attack_stat : CobblemonFightOrFlight.commonConfig().maximum_attack_stat;
        boolean isUsingRangeAttack = PokemonUtils.shouldShoot(pokemonEntity);
        boolean isUsingMeleeAttack = PokemonUtils.shouldMelee(pokemonEntity);//Status moves might not be any of them in the future?
        PokemonMultipliers multipliers = new PokemonMultipliers(pokemonEntity);
        float attackModifier = CobblemonFightOrFlight.commonConfig().max_bonus_from_stat * Mth.sqrt((float) Math.min(attack, maxStat) / maxStat);
        float moveModifier = movePower / 40 * CobblemonFightOrFlight.moveConfig().move_power_multiplier;
        float minDmg = isSpecial ? multipliers.getMinimumRangeAttackDamage() : multipliers.getMinimumAttackDamage();
        float maxDmg = isSpecial ? multipliers.getMaximumRangeAttackDamage() : multipliers.getMaximumAttackDamage();
        float sheerForceMultiplier = PokemonUtils.canActivateSheerForce(pokemonEntity) ? 1.3f : 1.0f;
        float multiplier = extraDamageFromEntityFeature(pokemonEntity, target, type) * getHeldItemDmgMultiplier(pokemonEntity, target) * sheerForceMultiplier * multipliers.getPlayerOwnedDamageMultiplier(isUsingRangeAttack, isUsingMeleeAttack);
        float mobEffectBoost = getMobEffectBoost(pokemonEntity);
        //CobblemonFightOrFlight.LOGGER.info(Float.toString(multiplier));
        PokemonInterface pokemonInterface = ((PokemonInterface) pokemonEntity);
        if (pokemonInterface.usingBeam() || pokemonInterface.usingSound() || pokemonInterface.usingMagic()) {
            multiplier *= CobblemonFightOrFlight.moveConfig().indirect_attack_move_power_multiplier;
        }
        float value = Math.min(Math.max(multiplier * (moveModifier * attackModifier + mobEffectBoost), minDmg), maxDmg);
        //CobblemonFightOrFlight.LOGGER.info("value:{} minDmg:{} maxDmg:{} attack:{} attackModifier:{} moveModifier:{} multiplier:{}", value, minDmg, maxDmg, attack, attackModifier, moveModifier, multiplier);
        return value;
    }

    public static float calculatePokemonDamage(PokemonEntity pokemonEntity, Entity target, Move move) {
        //TODO Special effect for Photon Geyser
        if (move == null) {
            CobblemonFightOrFlight.LOGGER.info("Null move detected");
            return CobblemonFightOrFlight.commonConfig().minimum_ranged_attack_damage;
        }
        if (PokemonUtils.isStatusMove(move)) {
            return 0f;
        }
        boolean isSpecial = PokemonUtils.isSpecialMove(move);
        float STAB;
        var primaryType = pokemonEntity.getPokemon().getPrimaryType();
        var secondaryType = pokemonEntity.getPokemon().getSecondaryType();
        if (secondaryType == null) {
            secondaryType = primaryType;
        }
        if (primaryType.equals(move.getType()) || secondaryType.equals(move.getType())) {
            STAB = PokemonUtils.abilityIs(pokemonEntity, "adaptability") ? 2f : 1.5f;
        } else {
            STAB = 1.0f;
        }
        return calculatePokemonDamage(pokemonEntity, target, isSpecial, (float) (move.getPower() * STAB), move.getType());
    }

    protected static float extraDamageFromEntityFeature(PokemonEntity pokemonEntity, Entity target, ElementalType moveType) {
        if (target.level().isClientSide || !(target instanceof LivingEntity livingEntity)) {
            return 1.0f;
        }
        ElementalType type = moveType == null ? pokemonEntity.getPokemon().getPrimaryType() : moveType;
        if (!(livingEntity instanceof PokemonEntity targetPokemon)) {
            if (ElementalTypes.INSTANCE.getWATER().equals(type)) {
                if (livingEntity.isSensitiveToWater()) {
                    return CobblemonFightOrFlight.commonConfig().water_type_super_effective_dmg_multiplier;
                }
            }
            if (ElementalTypes.INSTANCE.getFIRE().equals(type)) {
                if (livingEntity.fireImmune()) {
                    return CobblemonFightOrFlight.commonConfig().fire_type_no_effect_dmg_multiplier;
                }
            }
            if (ElementalTypes.INSTANCE.getICE().equals(type)) {
                if (!livingEntity.canFreeze()) {
                    return CobblemonFightOrFlight.commonConfig().ice_type_no_effect_dmg_multiplier;
                }
                if (livingEntity.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                    return CobblemonFightOrFlight.commonConfig().ice_type_super_effective_dmg_multiplier;
                }
            }
            if (ElementalTypes.INSTANCE.getPOISON().equals(type)) {
                if (livingEntity.getType().is(EntityTypeTags.UNDEAD)) {

                    return CobblemonFightOrFlight.commonConfig().poison_type_no_effect_dmg_multiplier;
                }
            }
        } else {
            //TODO type effectiveness here
            return TypeEffectiveness.getTypeEffectiveness(pokemonEntity, targetPokemon);
        }
        return 1.0f;
    }

    public static int getMobEffectBoost(PokemonEntity pokemonEntity) {
        int strengthLevel = 0;
        int weaknessLevel = 0;
        if (pokemonEntity.hasEffect(MobEffects.DAMAGE_BOOST)) {
            var strengthEffect = pokemonEntity.getEffect(MobEffects.DAMAGE_BOOST);
            if (strengthEffect != null) {
                strengthLevel = strengthEffect.getAmplifier() + 1;
            }
        }
        if (pokemonEntity.hasEffect(MobEffects.WEAKNESS)) {
            var weaknessEffect = pokemonEntity.getEffect(MobEffects.WEAKNESS);
            if (weaknessEffect != null) {
                weaknessLevel = weaknessEffect.getAmplifier() + 1;
            }
        }
        return strengthLevel * 3 - weaknessLevel * 4;
    }

    public static float getHeldItemDmgMultiplier(PokemonEntity pokemonEntity, Entity target) {
        //Variables that might be needed.
        ItemStack heldItem = PokemonUtils.getHeldItem(pokemonEntity);
        Move move = PokemonUtils.getMove(pokemonEntity);
        ElementalType type = null;
        if (move != null) {
            type = move.getType();
        }

        if (heldItem.is(CobblemonItems.LIFE_ORB)) {
            return 1.3f;//Do you really like 5324/4096(1.2998046875)?
        }
        if (move != null) {
            if (DamageCategories.INSTANCE.getPHYSICAL().equals(move.getDamageCategory())) {
                if (heldItem.is(CobblemonItems.MUSCLE_BAND)) {
                    return 1.1f;
                } else if (heldItem.is(CobblemonItems.CHOICE_BAND)) {
                    return 1.5f;
                }
            } else if (DamageCategories.INSTANCE.getSPECIAL().equals(move.getDamageCategory())) {
                if (heldItem.is(CobblemonItems.WISE_GLASSES)) {
                    return 1.1f;
                } else if (heldItem.is(CobblemonItems.CHOICE_SPECS)) {
                    return 1.5f;
                }
            }
        }
        //Type-enhancing item.
        if (type != null) {
            float typeEnhancingMultiplier = 1.2f;
            switch (type.getName()) {
                case "fire":
                    if (heldItem.is(CobblemonItems.CHARCOAL)) return typeEnhancingMultiplier;
                    break;
                case "ice":
                    if (heldItem.is(CobblemonItems.NEVER_MELT_ICE)) return typeEnhancingMultiplier;
                    break;
                case "poison":
                    if (heldItem.is(CobblemonItems.POISON_BARB)) return typeEnhancingMultiplier;
                    break;
                case "psychic":
                    if (heldItem.is(CobblemonItems.TWISTED_SPOON)) return typeEnhancingMultiplier;
                    break;
                case "fairy":
                    if (heldItem.is(CobblemonItems.FAIRY_FEATHER)) return typeEnhancingMultiplier;
                    break;
                case "fighting":
                    if (heldItem.is(CobblemonItems.BLACK_BELT)) return typeEnhancingMultiplier;
                    break;
                case "steel":
                    if (heldItem.is(CobblemonItems.METAL_COAT)) return typeEnhancingMultiplier;
                    break;
                case "ghost":
                    if (heldItem.is(CobblemonItems.SPELL_TAG)) return typeEnhancingMultiplier;
                    break;
                case "dark":
                    if (heldItem.is(CobblemonItems.BLACK_GLASSES)) return typeEnhancingMultiplier;
                    break;
                case "ground":
                    if (heldItem.is(CobblemonItems.SOFT_SAND)) return typeEnhancingMultiplier;
                    break;
                case "rock":
                    if (heldItem.is(CobblemonItems.HARD_STONE)) return typeEnhancingMultiplier;
                    break;
                case "electric":
                    if (heldItem.is(CobblemonItems.MAGNET)) return typeEnhancingMultiplier;
                    break;
                case "bug":
                    if (heldItem.is(CobblemonItems.SILVER_POWDER)) return typeEnhancingMultiplier;
                    break;
                case "grass":
                    if (heldItem.is(CobblemonItems.MIRACLE_SEED)) return typeEnhancingMultiplier;
                    break;
                case "dragon":
                    if (heldItem.is(CobblemonItems.DRAGON_FANG)) return typeEnhancingMultiplier;
                    break;
                case "flying":
                    if (heldItem.is(CobblemonItems.SHARP_BEAK)) return typeEnhancingMultiplier;
                    break;
                case "water":
                    if (heldItem.is(CobblemonItems.MYSTIC_WATER)) return typeEnhancingMultiplier;
                    break;
                case "normal":
                    if (heldItem.is(CobblemonItems.SILK_SCARF)) return typeEnhancingMultiplier;
                    break;
                default:
                    break;
            }
        }
        return 1.0f;
    }

    public static boolean canChangeMove(PokemonEntity pokemonEntity) {
        ItemStack itemStack = PokemonUtils.getHeldItem(pokemonEntity);
        return !itemStack.is(CobblemonItems.CHOICE_BAND) && !itemStack.is(CobblemonItems.CHOICE_SCARF) && !itemStack.is(CobblemonItems.CHOICE_SPECS);
    }

    protected static void calculateTypeEffect(PokemonEntity pokemonEntity, Entity hurtTarget, String typeName, int pkmLevel) {
        if (PokemonUtils.isSheerForce(pokemonEntity)) {
            return;
        }
        if (hurtTarget instanceof
                LivingEntity livingHurtTarget) {
            int effectStrength = Math.max(pkmLevel / 10, 1);

            switch (typeName) {
                case "fire":
                    livingHurtTarget.setRemainingFireTicks(effectStrength * 20);
                    break;
                case "ice":
                    livingHurtTarget.setTicksFrozen(livingHurtTarget.getTicksFrozen() + effectStrength * 30);
                    break;
                case "poison":
                    livingHurtTarget.addEffect(new MobEffectInstance(MobEffects.POISON, effectStrength * 20, 0), pokemonEntity);
                    break;
                case "psychic":
                    livingHurtTarget.addEffect(new MobEffectInstance(MobEffects.LEVITATION, effectStrength * 20, 0), pokemonEntity);
                    break;
                case "fairy":
                case "fighting":
                case "steel":
                    livingHurtTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, effectStrength * 20, 0), pokemonEntity);
                    break;
                case "ghost":
                case "dark":
                    livingHurtTarget.addEffect(new MobEffectInstance(MobEffects.DARKNESS, effectStrength * 25, 0), pokemonEntity);
                    break;
                case "ground":
                case "rock":
                    livingHurtTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, effectStrength * 25, 0), pokemonEntity);
                    break;
                case "electric":
                    livingHurtTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, effectStrength * 25, 0), pokemonEntity);
                    break;
                case "bug":
                    livingHurtTarget.addEffect(new MobEffectInstance(MobEffects.HUNGER, effectStrength * 25, 0), pokemonEntity);
                    break;
                case "grass":
                    pokemonEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, effectStrength * 40, 0), pokemonEntity);
                    break;
                case "water":
                    livingHurtTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (effectStrength + 2) * 25, 0), pokemonEntity);
                    break;
                case "dragon", "flying":
                default:
                    break;
            }
        }
    }

    public static void applyTypeEffect(PokemonEntity pokemonEntity, Entity hurtTarget, String typeName) {
        if (pokemonEntity == null) {
            return;
        }
        Pokemon pokemon = pokemonEntity.getPokemon();
        int pkmLevel = pokemon.getLevel();

        calculateTypeEffect(pokemonEntity, hurtTarget, typeName, pkmLevel);
    }

    public static void applyTypeEffect(PokemonEntity pokemonEntity, Entity hurtTarget) {
        if (pokemonEntity == null) {
            return;
        }
        Pokemon pokemon = pokemonEntity.getPokemon();
        int pkmLevel = pokemon.getLevel();
        String primaryType = pokemon.getPrimaryType().getName();
        calculateTypeEffect(pokemonEntity, hurtTarget, primaryType, pkmLevel);
    }

    public static void applyOnHitVisualEffect(PokemonEntity pokemonEntity, Entity hurtTarget, Move move) {
        if (move == null) {
            return;
        }
        String moveName = move.getName();
        int particleAmount = 4;
        boolean b1 = Arrays.stream(CobblemonFightOrFlight.visualEffectConfig().self_angry_moves).toList().contains(moveName);
        boolean b2 = Arrays.stream(CobblemonFightOrFlight.visualEffectConfig().target_soul_fire_moves).toList().contains(moveName);
        boolean b3 = Arrays.stream(CobblemonFightOrFlight.visualEffectConfig().target_soul_moves).toList().contains(moveName);
        boolean b4 = Arrays.stream(CobblemonFightOrFlight.visualEffectConfig().slicing_moves).toList().contains(moveName);
        boolean b5 = Arrays.stream(CobblemonFightOrFlight.moveConfig().magic_attack_moves).toList().contains(moveName);
        if (b1) {
            PokemonUtils.makeParticle(particleAmount, pokemonEntity, ParticleTypes.ANGRY_VILLAGER);
        }
        if (b2) {
            PokemonUtils.makeParticle(particleAmount, hurtTarget, ParticleTypes.SOUL_FIRE_FLAME);
        }
        if (b3) {
            PokemonUtils.makeParticle(particleAmount, hurtTarget, ParticleTypes.SOUL);
        }
        if (b4) {
            PokemonUtils.makeParticle(particleAmount, hurtTarget, ParticleTypes.SWEEP_ATTACK);
        }
        if (b5) {
            makeMagicAttackParticle(pokemonEntity, hurtTarget);
        }
        if (hurtTarget instanceof PokemonEntity targetPokemon) {
            float typeEffectivenessMultiplier = TypeEffectiveness.getTypeEffectiveness(pokemonEntity, targetPokemon);
            if (typeEffectivenessMultiplier >= 1.3f) {
                //Filter/Solid Rock will reduce the damage,1.3 is to avoid the inaccuracy of float.
                PokemonUtils.makeParticle(particleAmount, hurtTarget, ParticleTypes.WAX_ON);
            } else if (typeEffectivenessMultiplier < 1f) {
                PokemonUtils.makeParticle(particleAmount, hurtTarget, ParticleTypes.SCRAPE);
            }
        }
    }

    public static void makeMagicAttackParticle(PokemonEntity pokemonEntity, Entity target) {
        int particleAmount = 8;
        Move move = PokemonUtils.getRangeAttackMove(pokemonEntity);
        if (move == null) {
            return;
        }
        makeTypeEffectParticle(particleAmount, pokemonEntity, move.getType().getName());
        makeTypeEffectParticle(particleAmount, target, move.getType().getName());
    }

    public static void makeTypeEffectParticle(int particleAmount, Entity entity, String typeName) {
        if (typeName == null) {
            return;
        }
        PokemonUtils.makeParticle(particleAmount, entity, getParticleFromType(typeName));
    }

    public static void applyOnUseEffect(PokemonEntity pokemonEntity, LivingEntity hurtTarget, Move move) {
        Level level = hurtTarget.level();
        if (move == null || level.isClientSide) {
            return;
        }
        if (CobblemonFightOrFlight.commonConfig().activate_move_effect) {
            if (MoveData.moveData.containsKey(move.getName())) {
                for (MoveData data : MoveData.moveData.get(move.getName())) {
                    if (data.isOnUse()) {
                        data.invoke(pokemonEntity, hurtTarget);
                    }
                }
            }
        }
    }

    public static void applyPostEffect(PokemonEntity pokemonEntity, LivingEntity hurtTarget, Move move, boolean targetIsHurt) {
        Level level = hurtTarget.level();
        if (move == null || level.isClientSide) {
            return;
        }
        //These effects might stack, so a chain of ifs might be needed.
        boolean b1 = Arrays.stream(CobblemonFightOrFlight.moveConfig().switch_moves).toList().contains(move.getName());
        boolean b2 = Arrays.stream(CobblemonFightOrFlight.moveConfig().explosive_moves).toList().contains(move.getName());
        boolean b3 = Arrays.stream(CobblemonFightOrFlight.moveConfig().recoil_moves_allHP).toList().contains(move.getName());
        boolean b4 = Arrays.stream(CobblemonFightOrFlight.moveConfig().hp_draining_moves_50).toList().contains(move.getName());
        boolean b5 = Arrays.stream(CobblemonFightOrFlight.moveConfig().hp_draining_moves_75).toList().contains(move.getName());
        boolean b6 = pokemonEntity.getPokemon().heldItem().is(CobblemonItems.LIFE_ORB);
        if (b1) {
            pokemonRecallWithAnimation(pokemonEntity);
        }
        if (b2) {
            pokemonExplode(pokemonEntity, level);
        }
        if (b3) {
            pokemonRecoilSelf(pokemonEntity, 1.0f);
        }

        if (b4 || b5) {
            float dmg = calculatePokemonDamage(pokemonEntity, hurtTarget, move);
            boolean hasBigRoot = pokemonEntity.getPokemon().heldItem().is(CobblemonItems.BIG_ROOT);
            float percent = (b4 ? 0.5f : 0.75f) * (hasBigRoot ? 1.3f : 1.0f);
            pokemonEntity.heal(dmg * percent);
        }

        if (b6) {
            var abilityName = pokemonEntity.getPokemon().getAbility().getName();
            if (!(abilityName.equals("sheerforce") || abilityName.equals("magicguard"))) {
                pokemonRecoilSelf(pokemonEntity, 0.1f);
            }
        }

        if (CobblemonFightOrFlight.commonConfig().activate_type_effect) {
            applyTypeEffect(pokemonEntity, hurtTarget, move.getType().getName());
        }

        if (CobblemonFightOrFlight.commonConfig().activate_move_effect) {
            if (MoveData.moveData.containsKey(move.getName())) {
                for (MoveData data : MoveData.moveData.get(move.getName())) {
                    if (data.isOnHit() && targetIsHurt) {
                        data.invoke(pokemonEntity, hurtTarget);
                    }
                }
            }
        }
    }

    public static void pokemonRecallWithAnimation(PokemonEntity pokemonEntity) {
        if (pokemonEntity.getOwner() != null) {
            pokemonEntity.recallWithAnimation();
        }
    }

    protected static void addProjectileEntity(PokemonEntity pokemonEntity, LivingEntity target, AbstractPokemonProjectile projectile, Move move) {
        projectile.setElementalType(move.getType().getName());
        projectile.setDamage(calculatePokemonDamage(pokemonEntity, target, move));
        ((LivingEntity) pokemonEntity).level().addFreshEntity(projectile);
    }

    protected static void addProjectileEntity(PokemonEntity pokemonEntity, LivingEntity target, AbstractPokemonProjectile projectile) {
        projectile.setElementalType(pokemonEntity.getPokemon().getPrimaryType().getName());
        projectile.setDamage(calculatePokemonDamage(pokemonEntity, target, true));
        pokemonEntity.level().addFreshEntity(projectile);
    }

    protected static void shootProjectileEntity(PokemonEntity pokemonEntity, LivingEntity target, AbstractPokemonProjectile projectile) {
        double d = target.getX() - pokemonEntity.getX();
        double e = target.getY(0.5) - projectile.getY();
        double f = target.getZ() - pokemonEntity.getZ();
        float velocity = 1.6f;
        projectile.accurateShoot(d, e, f, velocity, 0.1f);
    }

    public static void pokemonPerformRangedAttack(PokemonEntity pokemonEntity, LivingEntity target) {
        if (pokemonEntity == null || target == null) {
            return;
        }
        Move move = PokemonUtils.getRangeAttackMove(pokemonEntity);
        AbstractPokemonProjectile bullet;
        PokemonUtils.sendAnimationPacket(pokemonEntity, "special");
        LivingEntity livingEntity = pokemonEntity;
        if (move != null) {
            String moveName = move.getName();
            //CobblemonFightOrFlight.LOGGER.info(moveName);
            Random rand = new Random();
            boolean b1 = Arrays.stream(CobblemonFightOrFlight.moveConfig().single_bullet_moves).toList().contains(moveName);
            boolean b2 = Arrays.stream(CobblemonFightOrFlight.moveConfig().multiple_bullet_moves).toList().contains(moveName);
            boolean b3 = Arrays.stream(CobblemonFightOrFlight.moveConfig().single_tracing_bullet_moves).toList().contains(moveName);
            boolean b4 = Arrays.stream(CobblemonFightOrFlight.moveConfig().multiple_tracing_bullet_moves).toList().contains(moveName);
            boolean b5 = Arrays.stream(CobblemonFightOrFlight.moveConfig().single_beam_moves).toList().contains(moveName);
            boolean b6 = PokemonUtils.isExplosiveMove(moveName);
            boolean b7 = Arrays.stream(CobblemonFightOrFlight.moveConfig().sound_based_moves).toList().contains(moveName);
            boolean b8 = Arrays.stream(CobblemonFightOrFlight.moveConfig().magic_attack_moves).toList().contains(moveName);
            if (b3 || b4) {
                for (int i = 0; i < (b3 ? 1 : rand.nextInt(3) + 1); ++i) {
                    bullet = new PokemonTracingBullet(livingEntity.level(), pokemonEntity, target, livingEntity.getDirection().getAxis());
                    addProjectileEntity(pokemonEntity, target, bullet, move);
                }
            } else if (b1 || b2) {
                for (int i = 0; i < (b1 ? 1 : rand.nextInt(3) + 1); ++i) {
                    bullet = new PokemonBullet(livingEntity.level(), pokemonEntity, target);
                    shootProjectileEntity(pokemonEntity, target, bullet);
                    addProjectileEntity(pokemonEntity, target, bullet, move);
                }
            } else if (b5 || b7 || b8) {
                if (!PokemonUtils.pokemonTryForceEncounter(pokemonEntity, target)) {
                    boolean success = target.hurt(pokemonEntity.damageSources().mobAttack(pokemonEntity), calculatePokemonDamage(pokemonEntity, target, move));
                    PokemonUtils.setHurtByPlayer(pokemonEntity, target);
                    applyOnHitVisualEffect(pokemonEntity, target, move);
                    applyPostEffect(pokemonEntity, target, move, success);
                }
                //applyTypeEffect(pokemonEntity, target);
            } else if (b6) {
                //Should not be processed here.
            } else {
                bullet = new PokemonArrow(livingEntity.level(), pokemonEntity, target);
                shootProjectileEntity(pokemonEntity, target, bullet);
                addProjectileEntity(pokemonEntity, target, bullet, move);
            }
            if (CobblemonFightOrFlight.commonConfig().activate_move_effect) {
                applyOnUseEffect(pokemonEntity, livingEntity, move);
            }
        } else {
            bullet = new PokemonArrow(livingEntity.level(), pokemonEntity, target);
            shootProjectileEntity(pokemonEntity, target, bullet);
            addProjectileEntity(pokemonEntity, target, bullet);
        }
    }

    public static void pokemonExplode(PokemonEntity entity, Level level) {
        if (!level.isClientSide) {
            FOFExplosion explosion = FOFExplosion.createExplosion(entity, entity, entity.getX(), entity.getY(), entity.getZ(), true, false);
            if (explosion != null) {
                explosion.explode();
                explosion.finalizeExplosion();
            } else {
                CobblemonFightOrFlight.LOGGER.warn("Failed to create the explosion");
            }
        }
    }

    public static void dealAoEDamage(PokemonEntity pokemonEntity, Entity centerEntity, boolean shouldHurtAlly, boolean decreaseOverDistance) {
        if (pokemonEntity == null) {
            return;
        }
        Move move = PokemonUtils.getMove(pokemonEntity);
        if (move == null) {
            CobblemonFightOrFlight.LOGGER.warn("No move for aoe.");
            return;
        }
        double radius = getAoERadius(pokemonEntity, move);
        //CobblemonFightOrFlight.LOGGER.info(Double.toString(radius));
        List<LivingEntity> list = centerEntity.level().getEntitiesOfClass(LivingEntity.class, centerEntity.getBoundingBox().inflate(radius - centerEntity.getBbWidth() / 2));
        Iterator<LivingEntity> it = list.iterator();
        while (true) {
            LivingEntity livingEntity;
            do {
                if (!it.hasNext()) {
                    return;
                }
                livingEntity = it.next();
            } while (centerEntity.distanceToSqr(livingEntity) > 25.0);
            if (livingEntity == pokemonEntity || !(shouldHurtAlly && shouldHurtAllyMob(pokemonEntity, livingEntity))) {
                continue;
            }
            float dmgMultiplier;
            if (decreaseOverDistance) {
                float distance = centerEntity.distanceTo(livingEntity);
                if (distance < CobblemonFightOrFlight.moveConfig().min_AoE_radius) {
                    dmgMultiplier = 1.0f;
                } else {
                    //TODO unfinished
                    dmgMultiplier = CobblemonFightOrFlight.moveConfig().min_AoE_damage_multiplier;//Will be replaced when I have enough free time
                }

            } else {
                dmgMultiplier = CobblemonFightOrFlight.moveConfig().min_AoE_damage_multiplier;
            }

            //CobblemonFightOrFlight.LOGGER.info(livingEntity.getDisplayName().getString());
            boolean bl = livingEntity.hurt(centerEntity.damageSources().mobAttack(pokemonEntity), calculatePokemonDamage(pokemonEntity, livingEntity, move) * dmgMultiplier);
            if (bl) {
                //applyTypeEffect(pokemonEntity, livingEntity);
                PokemonUtils.setHurtByPlayer(pokemonEntity, livingEntity);
                applyOnHitVisualEffect(pokemonEntity, livingEntity, move);
                makeTypeEffectParticle(10, livingEntity, move.getType().getName());
            }
        }
    }

    public static void dealAoEDamage(PokemonEntity pokemonEntity, Entity centerEntity, boolean shouldHurtAlly) {
        if (pokemonEntity != null) {
            Move move = PokemonUtils.getMove(pokemonEntity);
            if (move != null) {
                dealAoEDamage(pokemonEntity, centerEntity, shouldHurtAlly, true);
            } else {
                CobblemonFightOrFlight.LOGGER.warn("Failed to get move for aoe damage");
            }
        }
    }

    public static void pokemonRecoilSelf(PokemonEntity pokemonEntity, float percent) {
        Pokemon pokemon = pokemonEntity.getPokemon();
        float curHealth = pokemonEntity.getHealth();
        float maxHealth = pokemonEntity.getMaxHealth();
        float health = curHealth - maxHealth * percent;
        if (health > 0) {
            pokemonEntity.setHealth(curHealth);
        } else {
            pokemonEntity.setHealth(0);
        }
        PokemonUtils.entityHpToPokemonHp(pokemonEntity, maxHealth * percent, false);
        if (pokemonEntity.getHealth() == 0f) {
            pokemon.setCurrentHealth(0);
        }
    }

    public static float getAoERadius(PokemonEntity entity, Move move) {
        Pokemon pokemon = entity.getPokemon();
        boolean isSpecial = move.getDamageCategory().equals(DamageCategories.INSTANCE.getSPECIAL());
        int stat = isSpecial ? pokemon.getSpecialAttack() : pokemon.getAttack();
        int requiredStat = isSpecial ? CobblemonFightOrFlight.commonConfig().maximum_special_attack_stat : CobblemonFightOrFlight.commonConfig().maximum_attack_stat;
        return Math.min(Mth.lerp(((float) stat) / requiredStat, CobblemonFightOrFlight.moveConfig().min_AoE_radius, CobblemonFightOrFlight.moveConfig().max_AoE_radius), CobblemonFightOrFlight.moveConfig().max_AoE_radius);
    }

    public static int calculateAttackTime(PokemonEntity pokemonEntity, double distance) {
        if (pokemonEntity == null) {
            return -1;
        }
        boolean isMelee = PokemonUtils.shouldMelee(pokemonEntity);
        float attackSpeedModifier = Math.max(0.1f, 1 - pokemonEntity.getSpeed() / CobblemonFightOrFlight.commonConfig().speed_stat_limit);
        float f = (isMelee ? 0.2f : (float) Math.sqrt(distance) / PokemonUtils.getAttackRadius()) * attackSpeedModifier;
        if (isMelee) {
            return Mth.floor(20 * Mth.lerp(f, CobblemonFightOrFlight.commonConfig().minimum_melee_attack_interval, CobblemonFightOrFlight.commonConfig().maximum_melee_attack_interval));
        } else {
            return Mth.floor(20 * Mth.lerp(f, CobblemonFightOrFlight.commonConfig().minimum_ranged_attack_interval, CobblemonFightOrFlight.commonConfig().maximum_ranged_attack_interval));
        }
    }

    public static void refreshAttackTime(PokemonEntity pokemonEntity, int attackTime) {
        ((PokemonInterface) pokemonEntity).setAttackTime(attackTime);
        ((PokemonInterface) pokemonEntity).setMaxAttackTime(attackTime);
    }

    public static void resetAttackTime(PokemonEntity pokemonEntity, double distance) {
        int attackTime = calculateAttackTime(pokemonEntity, distance);
        refreshAttackTime(pokemonEntity, attackTime);
    }

    public static boolean pokemonAttack(PokemonEntity pokemonEntity, Entity hurtTarget) {
        Pokemon pokemon = pokemonEntity.getPokemon();
        float hurtDamage;
        float hurtKnockback = 1f;
        Move move = PokemonUtils.getMeleeMove(pokemonEntity);
        //CobblemonFightOrFlight.LOGGER.info("Trying to use melee attack");
        if (move != null) {
            //CobblemonFightOrFlight.LOGGER.info(move.getName());
            boolean b1 = PokemonUtils.isExplosiveMove(move.getName());
            if (b1) {
                hurtDamage = 0f;
            } else {
                hurtDamage = calculatePokemonDamage(pokemonEntity, hurtTarget, move);
            }
            if (hurtTarget instanceof LivingEntity livingEntity) {
                //applyTypeEffect(pokemonEntity, hurtTarget, move.getType().getName());
                makeTypeEffectParticle(10, livingEntity, move.getType().getName());
                PokemonUtils.updateMoveEvolutionProgress(pokemon, move.getTemplate());
                if (CobblemonFightOrFlight.commonConfig().activate_move_effect) {
                    applyOnUseEffect(pokemonEntity, livingEntity, move);
                }
            }
        } else {
            if (CobblemonFightOrFlight.commonConfig().activate_type_effect) {
                applyTypeEffect(pokemonEntity, hurtTarget);
            }
            makeTypeEffectParticle(6, hurtTarget, pokemonEntity.getPokemon().getPrimaryType().getName());
            hurtDamage = calculatePokemonDamage(pokemonEntity, hurtTarget, false);
        }
        applyOnHitVisualEffect(pokemonEntity, hurtTarget, move);
        PokemonUtils.setHurtByPlayer(pokemonEntity, hurtTarget);

        boolean flag = hurtTarget.hurt(pokemonEntity.level().damageSources().mobAttack(pokemonEntity), hurtDamage);
        if (flag) {
            if (hurtTarget instanceof LivingEntity livingEntity) {
                if (CobblemonFightOrFlight.commonConfig().activate_type_effect) {
                    pokemonEntity.setDeltaMovement(pokemonEntity.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }
                if (CobblemonFightOrFlight.commonConfig().activate_move_effect) {
                    applyPostEffect(pokemonEntity, livingEntity, move, true);
                }
                livingEntity.knockback(CobblemonFightOrFlight.commonConfig().activate_type_effect ? hurtKnockback * 0.5F : 0.5F, Mth.sin(pokemonEntity.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(pokemonEntity.getYRot() * ((float) Math.PI / 180F)));
            }

            pokemonEntity.setLastHurtMob(hurtTarget);
        }
        return flag;
    }

    public static boolean shouldHurtAllyMob(PokemonEntity pokemonEntity, LivingEntity target) {
        if (pokemonEntity == null || target == null) {
            return true;
        }
        if (pokemonEntity.getOwner() instanceof Player owner) {
            if (CobblemonFightOrFlight.commonConfig().pvp_immunity) {
                return !(target instanceof Player);
            }
            if (CobblemonFightOrFlight.commonConfig().friendly_fire_immunity_team) {
                return !Objects.equals(owner.getTeam(), target.getTeam());
            }
            if (CobblemonFightOrFlight.commonConfig().friendly_fire_immunity_owner) {
                return !owner.equals(target);
            }
        }
        return true;
    }

    public static boolean shouldBeHurtByAllyMob(PokemonEntity pokemonEntity, LivingEntity attacker) {
        if (pokemonEntity == null || attacker == null) {
            return true;
        }
        if (pokemonEntity.getOwner() instanceof Player owner) {
            if (CobblemonFightOrFlight.commonConfig().pvp_immunity) {
                return !(attacker instanceof Player);
            }
            if (CobblemonFightOrFlight.commonConfig().friendly_fire_immunity_team) {
                return !Objects.equals(owner.getTeam(), attacker.getTeam());
            }
            if (CobblemonFightOrFlight.commonConfig().friendly_fire_immunity_owner) {
                return !owner.equals(attacker);
            }
        }
        return true;
    }
}
