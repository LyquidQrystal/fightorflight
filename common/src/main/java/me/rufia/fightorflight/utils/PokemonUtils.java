package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.categories.DamageCategories;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.client.animation.PlayPosableAnimationPacket;
import com.cobblemon.mod.common.net.messages.client.effect.RunPosableMoLangPacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState;
import com.cobblemon.mod.common.pokemon.evolution.progress.UseMoveEvolutionProgress;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.data.movedata.MoveData;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class PokemonUtils {
    public static boolean shouldMelee(PokemonEntity pokemonEntity) {
        return ((PokemonInterface) pokemonEntity).getAttackMode() == 1;
    }

    public static boolean shouldShoot(PokemonEntity pokemonEntity) {
        return ((PokemonInterface) pokemonEntity).getAttackMode() == 2;
    }

    public static boolean shouldFightTarget(PokemonEntity pokemonEntity) {
        if (pokemonEntity.getPokemon().getLevel() < CobblemonFightOrFlight.commonConfig().minimum_attack_level) {
            return false;
        }

        LivingEntity owner = pokemonEntity.getOwner();
        if (owner != null) {
            if (!CobblemonFightOrFlight.commonConfig().do_pokemon_defend_owner || pokemonEntity.getTarget() == null || pokemonEntity.getTarget() == owner || pokemonEntity.getPokemon().getState() instanceof ShoulderedState) {
                return false;
            }

            if (pokemonEntity.getTarget() instanceof PokemonEntity targetPokemon) {
                LivingEntity targetOwner = targetPokemon.getOwner();
                if (targetOwner != null) {
                    if (targetOwner == owner) {
                        return false;
                    }
                    if (!CobblemonFightOrFlight.commonConfig().do_player_pokemon_attack_other_player_pokemon) {
                        return false;
                    }
                }
            }
            if (pokemonEntity.getTarget() instanceof Player) {
                if (!CobblemonFightOrFlight.commonConfig().do_player_pokemon_attack_other_players) {
                    return false;
                }
            }

        } else {
            if (pokemonEntity.getTarget() != null) {
                if (CobblemonFightOrFlight.getFightOrFlightCoefficient(pokemonEntity) <= 0) {
                    return false;
                }

                LivingEntity targetEntity = pokemonEntity.getTarget();
                if (pokemonEntity.distanceToSqr(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ()) > 400) {
                    return false;
                }
            }
        }
        return !pokemonEntity.isBusy();
    }

    public static Set<MoveTemplate> getAllLearnableMoveTemplates(Pokemon pokemon) {
        Set<MoveTemplate> moves = new HashSet<>();
        for (var move : pokemon.getBenchedMoves()) {
            moves.add(move.getMoveTemplate());
        }
        moves.addAll(pokemon.getForm().getMoves().getLevelUpMovesUpTo(100));
        moves.addAll(pokemon.getForm().getMoves().getEvolutionMoves());
        moves.addAll(pokemon.getForm().getMoves().getTmMoves());
        moves.addAll(pokemon.getForm().getMoves().getTutorMoves());
        moves.addAll(pokemon.getForm().getMoves().getEggMoves());
        //Might be a huge load for a big server?
        return moves;
    }

    public static boolean hasType(PokemonEntity pokemonEntity, ElementalType type) {
        return hasType(pokemonEntity.getPokemon(), type);
    }

    public static boolean hasType(Pokemon pokemon, ElementalType type) {
        ElementalType type1 = pokemon.getPrimaryType();
        if (type1 != type) {
            ElementalType type2 = pokemon.getSecondaryType();
            if (type2 != null) {
                return type2 == type;
            }
        }
        return true;
    }

    public static Move getMove(PokemonEntity pokemonEntity) {
        if (pokemonEntity == null) {
            CobblemonFightOrFlight.LOGGER.info("PokemonEntity is null");//This will be shown if the projectile hits the target and the pokemon is recalled
            return null;
        }
        Move firstMove = pokemonEntity.getPokemon().getMoveSet().get(0);
        String moveName = "";
        if (!((PokemonInterface) pokemonEntity).getCurrentMove().isEmpty()) {
            moveName = ((PokemonInterface) pokemonEntity).getCurrentMove();
        } else if (firstMove != null) {
            moveName = firstMove.getName();
        }

        Move move = null;
        boolean flag = false;
        if (!moveName.isEmpty()) {
            for (MoveTemplate m : getAllLearnableMoveTemplates(pokemonEntity.getPokemon())) {
                move = m.create();
                if (m.getName().equals(moveName)) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            move = pokemonEntity.getPokemon().getMoveSet().get(0);
        }

        if (move == null) {
            if (!pokemonEntity.level().isClientSide) {
                CobblemonFightOrFlight.LOGGER.warn("Can't get the move/Trying to return a null move. Move name:{}", moveName);//Will appear in the log when you send a pokemon out for a short period of time in the client environment, so I remove it from the client environment.
            }
        }
        return move;
    }

    @Deprecated
    public static Move getMove(PokemonEntity pokemonEntity, boolean getSpecial) {
        Move move = getMove(pokemonEntity);
        if (move == null) {
            return null;
        }
        boolean isSpecial = move.getDamageCategory() == DamageCategories.INSTANCE.getSPECIAL();
        boolean isPhysical = move.getDamageCategory() == DamageCategories.INSTANCE.getPHYSICAL();

        if ((isSpecial && getSpecial) || (isPhysical && !getSpecial)) {
            ((PokemonInterface) pokemonEntity).setCurrentMove(move);
            return move;
        }
        return null;
    }

    public static boolean isMeleeAttackMove(Move move) {
        if (move == null) {
            return false;
        }
        String moveName = move.getName();
        boolean isSpecial = isSpecialMove(move);
        boolean isPhysical = isPhysicalMove(move);
        boolean b1 = isPhysical && !(Arrays.stream(CobblemonFightOrFlight.moveConfig().single_bullet_moves).toList().contains(moveName) || Arrays.stream(CobblemonFightOrFlight.moveConfig().physical_single_arrow_moves).toList().contains(moveName));
        boolean b2 = isSpecial && (Arrays.stream(CobblemonFightOrFlight.moveConfig().special_contact_moves).toList().contains(moveName));
        return b1 || b2;
    }

    public static boolean isRangeAttackMove(Move move) {
        if (move == null) {
            return false;
        }
        String moveName = move.getName();
        boolean isSpecial = isSpecialMove(move);
        boolean isPhysical = isPhysicalMove(move);
        boolean b1 = isPhysical && (Arrays.stream(CobblemonFightOrFlight.moveConfig().single_bullet_moves).toList().contains(moveName) || Arrays.stream(CobblemonFightOrFlight.moveConfig().physical_single_arrow_moves).toList().contains(moveName));
        boolean b2 = isSpecial && !(Arrays.stream(CobblemonFightOrFlight.moveConfig().special_contact_moves).toList().contains(moveName));
        return b1 || b2;
    }

    public static Move getMeleeMove(PokemonEntity pokemonEntity) {
        Move move = getMove(pokemonEntity);
        if (move == null) {
            return null;
        }

        if (isMeleeAttackMove(move)) {
            return move;
        }
        return null;
    }

    public static Move getRangeAttackMove(PokemonEntity pokemonEntity) {
        Move move = getMove(pokemonEntity);
        if (move == null) {
            return null;
        }
        if (isRangeAttackMove(move)) {
            return move;
        }
        return null;
    }

    public static Move getStatusMove(PokemonEntity pokemonEntity) {
        Move move = getMove(pokemonEntity);
        if (move == null) {
            return null;
        }
        boolean b = DamageCategories.INSTANCE.getSTATUS().equals(move.getDamageCategory());
        boolean b1 = Arrays.stream(CobblemonFightOrFlight.moveConfig().self_targeting_status_move).toList().contains(move.getName());
        boolean b2 = MoveData.moveData.containsKey(move.getName());
        if (b && (b1 || b2)) {
            ((PokemonInterface) pokemonEntity).setCurrentMove(move);
            return move;
        }
        return null;
    }

    public static boolean findMoveEffectData(String effect) {
        var l = MoveData.moveData.get(effect);
        if (l != null) {
            for (MoveData data : l) {
                if (Objects.equals(data.getName(), effect)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean isSpecialMove(Move move) {
        return Objects.equals(move.getDamageCategory(), DamageCategories.INSTANCE.getSPECIAL());
    }

    public static boolean isPhysicalMove(Move move) {
        return Objects.equals(move.getDamageCategory(), DamageCategories.INSTANCE.getPHYSICAL());
    }

    public static boolean isStatusMove(Move move) {
        return Objects.equals(move.getDamageCategory(), DamageCategories.INSTANCE.getSTATUS());
    }

    public static void makeParticle(int particleAmount, Entity entity, SimpleParticleType particleType) {
        Level level = entity.level();
        if (particleAmount > 0) {
            double d = 0;
            double e = 0;
            double f = 0;
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(particleType, entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), particleAmount, d, e, f, 1f);
            } else {
                for (int j = 0; j < particleAmount; ++j) {
                    level.addParticle(particleType, entity.getRandomX(0.5), entity.getRandomY(), entity.getRandomZ(0.5), d, e, f);
                }
            }
        }
    }

    public static void setHurtByPlayer(PokemonEntity pokemonEntity, Entity target) {
        Entity owner = pokemonEntity.getOwner();
        if (owner instanceof Player player) {
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.setLastHurtByPlayer(player);
                //CobblemonFightOrFlight.LOGGER.info("Hurt by player's cobblemon");
            }
        }
    }

    public static boolean canTaunt(PokemonEntity pokemonEntity) {
        if (!CobblemonFightOrFlight.moveConfig().taunt_moves_needed) {
            return true;
        }
        boolean result = false;
        var moveSet = pokemonEntity.getPokemon().getMoveSet();
        for (Move move : moveSet) {
            if (Arrays.stream(CobblemonFightOrFlight.moveConfig().taunting_moves).toList().contains(move.getName())) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static boolean isExplosiveMove(String moveName) {
        return Arrays.stream(CobblemonFightOrFlight.moveConfig().explosive_moves).toList().contains(moveName);
    }

    public static void createSonicBoomParticle(PokemonEntity pokemonEntity, LivingEntity target) {
        if (target == null) {
            return;
        }
        float height = pokemonEntity.getEyeHeight();
        Vec3 vec1 = pokemonEntity.position().add(0, height, 0);
        Vec3 vec2 = target.getEyePosition().subtract(vec1);
        Vec3 vec3 = vec2.normalize();
        for (int i = 1; i < Mth.floor(vec2.length()) + 1; ++i) {
            Vec3 vec4 = vec1.add(vec3.scale(i));
            Level level = target.level();
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, vec4.x, vec4.y, vec4.z, 1, 0, 0, 0, 0);
            }
        }
    }


    public static void sendAnimationPacket(PokemonEntity pokemonEntity, String mode) {
        if (!pokemonEntity.level().isClientSide) {
            var pkt = new PlayPosableAnimationPacket(pokemonEntity.getId(), Set.of(mode), List.of());
            pokemonEntity.level().getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(pokemonEntity.position(), 64.0, 64.0, 64.0), (livingEntity) -> true).forEach((pkt::sendToPlayer));
        }
    }

    public static void updateMoveEvolutionProgress(Pokemon pokemon, MoveTemplate move) {
        if (UseMoveEvolutionProgress.Companion.supports(pokemon, move) && CobblemonFightOrFlight.commonConfig().can_progress_use_move_evoluiton) {
            UseMoveEvolutionProgress progress = pokemon.getEvolutionProxy().current().progressFirstOrCreate(evolutionProgress -> {
                        if (evolutionProgress instanceof UseMoveEvolutionProgress umep) {
                            return umep.currentProgress().getMove().equals(move);
                        }
                        return false;
                    }
                    , UseMoveEvolutionProgress::new);
            progress.updateProgress(new UseMoveEvolutionProgress.Progress(move, progress.currentProgress().getAmount() + 1));
        }
    }

    public static boolean shouldRetreat(PokemonEntity pokemonEntity) {
        //TODO Eject Button
        return pokemonEntity.getOwner() != null && pokemonEntity.getHealth() < pokemonEntity.getMaxHealth() * 0.5 && Arrays.stream(CobblemonFightOrFlight.moveConfig().emergency_exit_like_abilities).toList().contains(pokemonEntity.getPokemon().getAbility().getName());
    }

    public static void makeCobblemonParticle(Entity entity, String particleName) {
        if (entity != null) {
            var packet = new RunPosableMoLangPacket(entity.getId(), Set.of(String.format("q.particle('cobblemon:%s', 'target')", particleName)));
            packet.sendToPlayersAround(entity.getX(), entity.getY(), entity.getZ(), 50, entity.level().dimension(), (serverPlayer) -> false);
        }
        //todo I still need to find a way to update the locator or the particle can't be spawned at the target's location.
    }

    public static ItemStack getHeldItem(PokemonEntity pokemonEntity) {
        if (pokemonEntity == null) {
            return null;
        }
        return getHeldItem(pokemonEntity.getPokemon());
    }

    public static ItemStack getHeldItem(Pokemon pokemon) {
        if (pokemon == null) {
            return null;
        }
        return pokemon.heldItem();
    }

    public static boolean isUsingNewHealthMechanic() {
        return CobblemonFightOrFlight.commonConfig().shouldOverrideUpdateMaxHealth;
    }

    public static String getNatureName(PokemonEntity pokemonEntity) {
        return getNatureName(pokemonEntity.getPokemon());
    }

    public static String getNatureName(Pokemon pokemon) {
        return pokemon.getNature().getDisplayName().toLowerCase().replace("cobblemon.nature.", "");
    }

    public static int getMaxHealth(PokemonEntity pokemonEntity) {
        return getMaxHealth(pokemonEntity.getPokemon());
    }

    public static int getHPStat(Pokemon pokemon) {
        return pokemon.getMaxHealth();
    }

    public static int getMaxHealth(Pokemon pokemon) {
        int hpStat = getHPStat(pokemon);
        int minStat = CobblemonFightOrFlight.commonConfig().min_HP_required_stat;
        int midStat = CobblemonFightOrFlight.commonConfig().mid_HP_required_stat;
        int maxStat = CobblemonFightOrFlight.commonConfig().max_HP_required_stat;
        int stat = Mth.clamp(hpStat, minStat, maxStat);
        int minHealth = CobblemonFightOrFlight.commonConfig().min_HP;
        int midHealth = CobblemonFightOrFlight.commonConfig().mid_HP;
        int maxHealth = CobblemonFightOrFlight.commonConfig().max_HP;
        int health = minHealth;
        health = Math.round(
                stat < midStat ?
                        Mth.lerp((float) (stat - minStat) / (midStat - minStat), minHealth, midHealth) :
                        Mth.lerp((float) (stat - midStat) / (maxStat - midStat), midHealth, maxHealth));
        return health;//The return value is a mathematical integer,but some calculation needs a float.
    }

    public static void entityHpToPokemonHp(PokemonEntity pokemonEntity, float amount, boolean isHealing) {
        Pokemon pokemon = pokemonEntity.getPokemon();
        if (pokemon.getCurrentHealth() == 0 || pokemonEntity.isBattling() || pokemonEntity.getOwner() == null && !CobblemonFightOrFlight.commonConfig().enable_health_sync_for_wild_pokemon) {
            return;
        }
        float ratio = amount / getMaxHealth(pokemonEntity);
        int val = pokemon.getCurrentHealth() + (int) Math.floor(ratio * getHPStat(pokemon)) * (isHealing ? 1 : -1);
        pokemon.setCurrentHealth(val);
    }

    public static void taunt(PokemonEntity pokemonEntity) {
        if (pokemonEntity.getOwner() instanceof Player player) {
            var entities = pokemonEntity.level().getEntitiesOfClass(Mob.class, pokemonEntity.getBoundingBox().inflate(10, 2, 10), (mob -> mob.getTarget() == player));
            for (Mob mob : entities) {
                mob.setTarget(pokemonEntity);
            }
        }
    }

    public static boolean isSheerForce(PokemonEntity pokemonEntity) {
        return abilityIs(pokemonEntity, "sheerforce");
    }

    public static boolean abilityIs(PokemonEntity pokemonEntity, String abilityName) {
        return pokemonEntity.getPokemon().getAbility().getName().equals(abilityName);
    }

    public static boolean canActivateSheerForce(PokemonEntity pokemonEntity) {
        if (pokemonEntity != null && isSheerForce(pokemonEntity)) {
            Move move = getMove(pokemonEntity);
            if (move != null && MoveData.moveData.containsKey(move.getName())) {
                for (MoveData data : MoveData.moveData.get(move.getName())) {
                    if (data.canActivateSheerForce()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static PokeStaffComponent.CMDMODE getCommandMode(PokemonEntity pokemon) {
        try {
            return PokeStaffComponent.CMDMODE.valueOf(((PokemonInterface) (Object) pokemon).getCommand());
        } catch (IllegalArgumentException e) {
            return PokeStaffComponent.CMDMODE.NOCMD;
        }
    }

    public static boolean WildPokemonCanPerformUnprovokedAttack(PokemonEntity pokemonEntity) {//It doesn't include the aggro check.
        return pokemonEntity != null && CobblemonFightOrFlight.commonConfig().do_pokemon_attack_unprovoked && pokemonEntity.getPokemon().getLevel() >= CobblemonFightOrFlight.commonConfig().minimum_attack_unprovoked_level && !pokemonEntity.getPokemon().isPlayerOwned();
    }

    public static String getCommandData(PokemonEntity pokemonEntity) {
        return ((PokemonInterface) pokemonEntity).getCommandData();
    }


    public static boolean moveCommandAvailable(PokemonEntity pokemonEntity) {
        return pokemonEntity.getOwner() != null && PokeStaffComponent.CMDMODE.MOVE == getCommandMode(pokemonEntity);
    }

    public static boolean moveAttackCommandAvailable(PokemonEntity pokemonEntity) {
        return pokemonEntity.getOwner() != null && PokeStaffComponent.CMDMODE.MOVE_ATTACK == getCommandMode(pokemonEntity);
    }

    public static boolean stayCommandAvailable(PokemonEntity pokemonEntity) {
        return pokemonEntity.getOwner() != null && PokeStaffComponent.CMDMODE.STAY == getCommandMode(pokemonEntity);
    }

    public static boolean attackPositionAvailable(PokemonEntity pokemonEntity) {
        return pokemonEntity.getOwner() != null && PokeStaffComponent.CMDMODE.ATTACK_POSITION == getCommandMode(pokemonEntity);
    }

    public static boolean shouldDisableFollowOwner(PokemonEntity pokemon) {
        if (pokemon.getOwner() == null) {
            return false;
        }
        PokeStaffComponent.CMDMODE cmd = getCommandMode(pokemon);
        switch (cmd) {
            case ATTACK, ATTACK_POSITION, MOVE_ATTACK, STAY, MOVE -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public static void clearCommand(PokemonEntity pokemonEntity) {
        ((PokemonInterface) pokemonEntity).setCommand(PokeStaffComponent.CMDMODE.NOCMD.name());
        ((PokemonInterface) pokemonEntity).setCommandData("");
    }

    public static void finishMoving(PokemonEntity pokemonEntity) {
        if (CobblemonFightOrFlight.commonConfig().stay_after_move_command) {
            if (moveCommandAvailable(pokemonEntity)) {
                ((PokemonInterface) pokemonEntity).setCommand(PokeStaffComponent.CMDMODE.STAY.name());
                pokemonEntity.getNavigation().stop();
            }
        } else {
            clearCommand(pokemonEntity);
        }
    }

    public static void pokemonEntityApproachPos(PokemonEntity pokemonEntity, BlockPos pos, double speedModifier) {
        if (pos != BlockPos.ZERO && pokemonEntity.getNavigation().isDone()) {
            pokemonEntity.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), speedModifier);
        }
    }

    public static float getAttackRadius() {
        return 16.0f;
    }

    public static boolean shouldStopRunningAfterHurt(PokemonEntity pokemonEntity) {
        if (CobblemonFightOrFlight.commonConfig().stop_running_after_hurt) {
            return pokemonEntity.getHealth() < pokemonEntity.getMaxHealth();
        }
        return false;
    }

    public static boolean pokemonTryForceEncounter(PokemonEntity attackingPokemon, Entity hurtTarget) {
        if (attackingPokemon == null || hurtTarget == null) {
            return false;
        }
        if (hurtTarget instanceof PokemonEntity defendingPokemon) {
            if (attackingPokemon.getPokemon().isPlayerOwned()) {
                if (defendingPokemon.getPokemon().isPlayerOwned()) {
                    return CobblemonFightOrFlight.commonConfig().force_player_battle_on_pokemon_hurt && pokemonForceEncounterPvP(attackingPokemon, defendingPokemon);
                } else {
                    return pokemonForceEncounterPvE(attackingPokemon, defendingPokemon);
                }

            } else if (defendingPokemon.getPokemon().isPlayerOwned() && CobblemonFightOrFlight.commonConfig().force_wild_battle_on_pokemon_hurt) {
                return pokemonForceEncounterPvE(defendingPokemon, attackingPokemon);
            }
        }
        return false;
    }

    public static boolean pokemonForceEncounterPvP(PokemonEntity playerPokemon, PokemonEntity opponentPokemon) {
        if (playerPokemon.getOwner() instanceof ServerPlayer serverPlayer
                && opponentPokemon.getOwner() instanceof ServerPlayer serverOpponent) {

            if (serverPlayer == serverOpponent // I don't see why this should ever happen, but probably best to account for it
                    || !canBattlePlayer(serverPlayer)
                    || !canBattlePlayer(serverOpponent)) {
                return false;
            }

            BattleBuilder.INSTANCE.pvp1v1(serverPlayer,
                    serverOpponent,
                    null,
                    null,
                    BattleFormat.Companion.getGEN_9_SINGLES(),
                    false,
                    false);
        }
        return false;
    }

    public static boolean pokemonForceEncounterPvE(PokemonEntity playerPokemon, PokemonEntity wildPokemon) {
        if (playerPokemon.getOwner() instanceof ServerPlayer serverPlayer) {

            if (!canBattlePlayer(serverPlayer)) {
                return false;
            }

            BattleBuilder.INSTANCE.pve(serverPlayer,
                    wildPokemon,
                    playerPokemon.getPokemon().getUuid(),
                    BattleFormat.Companion.getGEN_9_SINGLES(),
                    false,
                    false,
                    Cobblemon.config.getDefaultFleeDistance(),
                    Cobblemon.INSTANCE.getStorage().getParty(serverPlayer));
        }
        return false;
    }

    public static boolean canBattlePlayer(ServerPlayer serverPlayer) {
        boolean playerHasAlivePokemon = false;
        for (Pokemon pokemon : Cobblemon.INSTANCE.getStorage().getParty(serverPlayer)) {
            if (!pokemon.isFainted()) {
                playerHasAlivePokemon = true;
                break;
            }
        }

        return BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(serverPlayer) == null
                && playerHasAlivePokemon
                && serverPlayer.isAlive();
    }

    public static boolean canAttackTargetProactively(LivingEntity livingEntity) {
        if (livingEntity == null) {
            return false;
        }
        boolean isEnemy = livingEntity instanceof Enemy;

        return isEnemy;
    }

    public static boolean shouldCheckPokeStaff() {
        return CobblemonFightOrFlight.commonConfig().should_check_poke_staff;//TODO replace it with the config.
    }
}
