package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.logging.LogUtils;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PokemonAvoidGoal extends Goal {
    protected final PathfinderMob mob;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    @Nullable
    protected LivingEntity toAvoid;
    protected final float maxDist;
    @Nullable
    protected Path path;
    protected final PathNavigation pathNav;
    private final TargetingConditions avoidEntityTargeting;

    public PokemonAvoidGoal(PathfinderMob mob, float maxDist, float walkSpeedModifier, float sprintSpeedModifier) {
        this.mob = mob;
        this.maxDist = maxDist;
        this.walkSpeedModifier = walkSpeedModifier;
        this.sprintSpeedModifier = sprintSpeedModifier;
        this.pathNav = this.mob.getNavigation();
        this.avoidEntityTargeting = TargetingConditions.forCombat().range((double) maxDist);

    }

    public boolean canUse() {
        PokemonEntity pokemonEntity = (PokemonEntity) this.mob;
        if (pokemonEntity.getPokemon().isPlayerOwned()) {
            return false;
        }
        if (pokemonEntity.isBusy()) {
            return false;
        }
        String species = pokemonEntity.getPokemon().getSpecies().getName().toLowerCase();
        if (PokemonUtils.shouldStopRunningAfterHurt(pokemonEntity)) {
            if (pokemonEntity.getMaxHealth() != pokemonEntity.getHealth()) {
                return false;
            }
        }
        if (CobblemonFightOrFlight.SpeciesAlwaysFlee(species)) {
            //These pokemon won't run away from creative mode player,I thought I had to switch it on manually so I spent an hour debugging...
            this.toAvoid = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(Player.class, this.mob.getBoundingBox().inflate((double) this.maxDist, 3.0, (double) this.maxDist), (livingEntity) -> true), this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
        } else {
            if (this.mob.getTarget() != null) {
                if (CobblemonFightOrFlight.getFightOrFlightCoefficient(pokemonEntity) > CobblemonFightOrFlight.commonConfig().neutral_threshold) {
                    return false;
                }

                if (this.mob.getTarget().distanceToSqr(this.mob) < maxDist) {
                    toAvoid = this.mob.getTarget();
                }
            }
        }


        if (this.toAvoid == null) {
            return false;
        } else {
            Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
            if (vec3 == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.toAvoid.distanceToSqr(this.mob)) {

                return false;
            } else {
                this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
                return this.path != null;
            }
        }
    }


    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }

    public void start() {
        PokemonEntity pokemonEntity = (PokemonEntity) this.mob;
        boolean has_teleport = false;
        List<Move> moves = pokemonEntity.getPokemon().getMoveSet().getMoves();
        for (Move move : moves) {
            if (move.getName().equals("teleport")) {
                has_teleport = true;
                //LogUtils.getLogger().info("This pokemon got teleport to avoid you");
                break;
            }
        }

        if (CobblemonFightOrFlight.commonConfig().allow_teleport_to_flee && has_teleport && path != null) {
            for (int i = 0; i < 5; ++i) {
                this.mob.level().addParticle(ParticleTypes.PORTAL, this.mob.getRandomX(0.5), this.mob.getRandomY(), this.mob.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
            this.mob.teleportTo(this.path.getEndNode().x, this.path.getEndNode().y + 0.2, this.path.getEndNode().z);
            this.path = null;
        } else {
            this.pathNav.moveTo(this.path, this.walkSpeedModifier);
        }
    }

    public void stop() {
        this.toAvoid = null;
    }

    public void tick() {
        PokemonEntity pokemonEntity = (PokemonEntity) this.mob;
        if (pokemonEntity.distanceToSqr(this.toAvoid) < (maxDist * 0.5)) {
            pokemonEntity.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
        } else {
            pokemonEntity.getNavigation().setSpeedModifier(this.walkSpeedModifier);
        }

    }
}