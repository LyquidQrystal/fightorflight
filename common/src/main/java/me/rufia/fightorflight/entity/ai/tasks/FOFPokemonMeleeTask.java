package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Arrays;

public class FOFPokemonMeleeTask {
    public static OneShot<LivingEntity> create(int cooldownBetweenAttacks) {
        return BehaviorBuilder.create(context ->
                context.group(
                        context.registered(MemoryModuleType.LOOK_TARGET),
                        context.present(MemoryModuleType.ATTACK_TARGET),
                        context.absent(MemoryModuleType.ATTACK_COOLING_DOWN),
                        context.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                ).apply(context, (lookTargetAccessor, attackTargetAccessor, attackCooldownAccessor, visibleMobsAccessor) -> ((serverLevel, livingEntity, l) -> {
                    LivingEntity target = context.get(attackTargetAccessor);
                    if (livingEntity instanceof PokemonEntity pokemonEntity) {
                        if (PokemonUtils.shouldMelee(pokemonEntity)) {
                            if (canPerformAttack(pokemonEntity, target)) {
                                lookTargetAccessor.set(new EntityTracker(target, true));
                                FOFPokemonAttackTask.resetAttackTime(pokemonEntity, 0);
                                pokemonEntity.swing(InteractionHand.MAIN_HAND);
                                pokemonDoHurtTarget(pokemonEntity, target);
                                pokemonEntity.setTarget(target);
                                ((PokemonInterface) pokemonEntity).setAttackTime(cooldownBetweenAttacks);
                                attackCooldownAccessor.setWithExpiry(true, cooldownBetweenAttacks);
                                return true;
                            }
                        }
                    }
                    return false;
                }
                )));
    }

    protected static boolean canPerformAttack(PokemonEntity pokemonEntity, LivingEntity entity) {
        return FOFPokemonAttackTask.getAttackTime(pokemonEntity) == 0 && pokemonEntity.isWithinMeleeAttackRange(entity) && pokemonEntity.getSensing().hasLineOfSight(entity);
    }

    protected static boolean pokemonDoHurtTarget(PokemonEntity pokemonEntity, LivingEntity hurtTarget) {
        if (!CobblemonFightOrFlight.commonConfig().do_pokemon_attack_in_battle) {
            if (FOFPokemonAttackTask.isTargetInBattle(pokemonEntity)) {
                return false;
            }
        }
        //CobblemonFightOrFlight.LOGGER.info("Trying to use melee attack");
        if (!PokemonUtils.pokemonTryForceEncounter(pokemonEntity, hurtTarget)) {
            Move move = PokemonUtils.getMove(pokemonEntity);
            if (move != null) {
                if (Arrays.stream(CobblemonFightOrFlight.moveConfig().self_centered_aoe_moves).toList().contains(move.getName())) {
                    PokemonAttackEffect.dealAoEDamage(pokemonEntity, pokemonEntity, true, PokemonUtils.isMeleeAttackMove(move));
                    if (PokemonUtils.isPhysicalMove(move)) {
                        PokemonUtils.sendAnimationPacket(pokemonEntity, "physical");
                    } else {
                        PokemonUtils.sendAnimationPacket(pokemonEntity, "special");
                    }
                    return true;
                }
            }
            PokemonUtils.sendAnimationPacket(pokemonEntity, "physical");
            return PokemonAttackEffect.pokemonAttack(pokemonEntity, hurtTarget);
        }

        return false;
    }
}
