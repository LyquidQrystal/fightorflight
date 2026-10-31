package me.rufia.fightorflight.mixin;


import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.categories.DamageCategories;
import com.cobblemon.mod.common.api.moves.categories.DamageCategory;
import com.cobblemon.mod.common.api.pokemon.experience.SidemodExperienceSource;
import com.cobblemon.mod.common.api.pokemon.stats.EvSource;
import com.cobblemon.mod.common.api.pokemon.stats.SidemodEvSource;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.data.movedata.MoveData;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.entity.ai.sensors.FOFSensors;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.net.FOFStateSync;
import me.rufia.fightorflight.net.packet.MoveInfoPacket;
import me.rufia.fightorflight.utils.*;
import me.rufia.fightorflight.utils.event.move.FOFMoveEvent;
import me.rufia.fightorflight.utils.handler.MoveEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(PokemonEntity.class)
public abstract class PokemonEntityMixin extends TamableAnimal implements PokemonInterface {
    @Shadow(remap = false)
    public abstract void cry();

    @Shadow(remap = false)
    public abstract Pokemon getPokemon();

    @Final
    @Shadow(remap = false)
    private List<Object> busyLocks;


    @Shadow(remap = false)
    public abstract int getBeamMode();

    @Shadow(remap = false)
    public abstract UUID getBattleId();

    @Unique
    @Nullable
    private LivingEntity fightorflight$clientSideCachedAttackTarget;
    @Unique
    @Nullable
    private LivingEntity ownerLastHurt;
    @Unique
    private int ownerLastHurtTick = 0;
    @Unique
    private int ticksUntilNewAngerParticle = 0;
    // ------------------------------------------------------------------
    // FOF entity state. This used to live in SynchedEntityData, which required injecting into
    // PokemonEntity#defineSynchedData and conflicted with other mods doing the same.
    // The part that the client needs is now pushed by FOFPokemonStatePacket (see FOFStateSync).
    // ------------------------------------------------------------------
    @Unique
    private int fof$attackTargetId = 0;
    @Unique
    private String fof$currentMove = "";
    @Unique
    private int fof$attackTime = 0;
    @Unique
    private int fof$maxAttackTime = -1;
    @Unique
    private int fof$moveDuration = -1;//Activate the move if the value equals to 0, then this value will be reduced to -1.
    @Unique
    private int fof$moveDurationOriginal = -1;
    // Server side only, never sent to the client.
    @Unique
    private int fof$attackMode = 0;//0 means the pokemon can't attack, 1 for melee, 2 for range attack.
    @Unique
    private int fof$cryCd = 0;
    @Unique
    private String fof$command = "";
    @Unique
    private String fof$commandData = "";
    @Unique
    private BlockPos fof$targetBlockPos = BlockPos.ZERO;
    @Unique
    private int fof$capturedBy = 0;

    @Unique
    private boolean fof$stateDirty = false;
    @Unique
    private final Map<UUID, Integer> fof$lastStateSync = new HashMap<>();

    @Unique
    private final List<FOFMove> MOVES_FOF = new ArrayList<>();//This should only be accessed in the server side!
    @Unique
    private final HashSet<Pokemon> HURT_BY_POKEMON_FOF = new HashSet<>();
    @Unique
    private Pokemon LAST_HURT_BY_POKEMON_FOF = null;
    @Unique
    private final MoveEventHandler moveEventHandler = new MoveEventHandler();

    @Unique
    protected void createTargetBlockPos() {
        String data = this.getCommandData();
        BlockPos blockPos = BlockPos.ZERO;
        if (data.startsWith("POS_")) {
            if (data.equals("POS_SELF")) {
                blockPos = new BlockPos(getBlockX(), getBlockZ(), getBlockZ());
            }
        } else {
            Vec3i vec3i = FOFUtils.stringToVec3i(data);
            if (vec3i != null) {
                blockPos = new BlockPos(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            }
        }
        setTargetBlockPos(blockPos);
    }

    protected PokemonEntityMixin(EntityType<? extends ShoulderRidingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public LivingEntity getTarget() {
        if (this.level().isClientSide) {
            if (fightorflight$clientSideCachedAttackTarget != null) {
                return fightorflight$clientSideCachedAttackTarget;
            } else {
                Entity entity = this.level().getEntity(this.fof$attackTargetId);
                if (entity instanceof LivingEntity) {
                    fightorflight$clientSideCachedAttackTarget = (LivingEntity) entity;
                    return fightorflight$clientSideCachedAttackTarget;
                }
            }
        }
        return super.getTarget();
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    private void writeAdditionalNbt(CompoundTag compoundTag, CallbackInfoReturnable<Boolean> ci) {
        compoundTag.putInt("FOF_CryCd", 0);
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void readAdditionalNbt(CompoundTag compoundTag, CallbackInfo ci) {
        fof$cryCd = compoundTag.getInt("FOF_CryCd");
    }

    @ModifyVariable(method = "assignNewBrainWithMemoriesAndSensors", at = @At("HEAD"), argsOnly = true, index = 3)
    private Set<SensorType<?>> sensorInject(Set<SensorType<?>> sensors) {
        HashSet<SensorType<?>> hashSet = new HashSet<>(sensors);
        hashSet.add(FOFSensors.POKEMON_HELP_OWNER);
        hashSet.add(FOFSensors.POKEMON_WILD_PROACTIVE);
        hashSet.add(FOFSensors.POKEMON_CAUGHT_BY);
        hashSet.add(FOFSensors.POKESTAFF_ATTACK_TARGET);
        hashSet.add(FOFSensors.POKESTAFF_WALK_TARGET);
        hashSet.add(FOFSensors.POKESTAFF_AUTO_BATTLE);
        hashSet.add(SensorType.HURT_BY);
        return Set.copyOf(hashSet);
    }

    @ModifyVariable(method = "assignNewBrainWithMemoriesAndSensors", at = @At("HEAD"), argsOnly = true, index = 2)
    private Set<MemoryModuleType<?>> memoryInject(Set<MemoryModuleType<?>> memoryModuleTypes) {
        var hashSet = new HashSet<>(memoryModuleTypes);
        hashSet.add(MemoryModuleType.AVOID_TARGET);
        return Set.copyOf(hashSet);
    }


    public void setTarget(LivingEntity target) {
        super.setTarget(target);
        setAttackTargetId(target.getId());
    }

    @Override
    public int getAttackTargetId() {
        return fof$attackTargetId;
    }

    @Override
    public void setAttackTargetId(int id) {
        if (fof$attackTargetId != id) {
            fightorflight$clientSideCachedAttackTarget = null;
        }
        fof$attackTargetId = id;
        fof$markStateDirty();
    }

    @Override
    public int getAttackTime() {
        return fof$attackTime;
    }

    @Override
    public void setAttackTime(int val) {
        fof$attackTime = val;
        fof$markStateDirty();
    }

    @Override
    public int getMaxAttackTime() {
        return fof$maxAttackTime;
    }

    @Override
    public void setMaxAttackTime(int val) {
        fof$maxAttackTime = val;
        fof$markStateDirty();
    }

    @Override
    public boolean usingBeam() {
        if (getCurrentMove().isEmpty()) {
            return false;
        }
        return Arrays.stream(CobblemonFightOrFlight.moveConfig().single_beam_moves).toList().contains(getCurrentMove());
    }

    @Override
    public boolean usingSound() {
        if (getCurrentMove().isEmpty()) {
            return false;
        }
        return Arrays.stream(CobblemonFightOrFlight.moveConfig().sound_based_moves).toList().contains(getCurrentMove());
    }

    @Override
    public boolean usingMagic() {
        if (getCurrentMove().isEmpty()) {
            return false;
        }
        return Arrays.stream(CobblemonFightOrFlight.moveConfig().magic_attack_moves).toList().contains(getCurrentMove());
    }

    @Override
    public void setCurrentMove(Move move) {
        setCurrentMove(move.getName());
    }

    @Override
    public String getCurrentMove() {
        return fof$currentMove;
    }

    @Override
    public void setCurrentMove(String name) {
        fof$currentMove = name == null ? "" : name;
        fof$markStateDirty();
    }

    @Override
    public int getNextCryTime() {
        return fof$cryCd;
    }

    @Override
    public void setNextCryTime(int time) {
        fof$cryCd = time;
    }

    @Override
    public void setCommand(String cmd) {
        fof$command = cmd == null ? "" : cmd;
    }

    @Override
    public String getCommand() {
        return fof$command;
    }

    @Override
    public void setCommandData(String cmdData) {
        fof$commandData = cmdData == null ? "" : cmdData;
        createTargetBlockPos();
    }

    @Override
    public String getCommandData() {
        return fof$commandData;
    }

    @Override
    public BlockPos getTargetBlockPos() {
        return fof$targetBlockPos;
    }

    @Override
    public void setTargetBlockPos(BlockPos blockPos) {
        fof$targetBlockPos = blockPos == null ? BlockPos.ZERO : blockPos;
    }

    @Override
    public int getCapturedBy() {
        return fof$capturedBy;
    }

    @Override
    public void setCapturedBy(int id) {
        fof$capturedBy = id;
    }

    @Override
    public int getAttackMode() {
        return fof$attackMode;
    }

    @Override
    public void setAttackMode(int attackMode) {
        fof$attackMode = attackMode;
    }

    @Override
    public int getMoveDuration() {
        return fof$moveDuration;
    }

    @Override
    public void setMoveDuration(int duration) {
        fof$moveDuration = duration;
        fof$markStateDirty();
    }

    @Override
    public int getMoveDurationOriginal() {
        return fof$moveDurationOriginal;
    }

    @Override
    public void setMoveDurationOriginal(int duration) {
        fof$moveDurationOriginal = duration;
        fof$markStateDirty();
    }


    @Override
    public int getOwnerLastHurtTick() {
        return ownerLastHurtTick;
    }

    @Override
    public LivingEntity getOwnerLastHurt() {
        return ownerLastHurt;
    }

    @Override
    public void setOwnerLastHurt(@Nullable LivingEntity livingEntity) {
        ownerLastHurt = livingEntity;
        ownerLastHurtTick = tickCount;
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float hurtDamageTweak(float amount) {
        if (PokemonUtils.shouldRetreat((PokemonEntity) (Object) this)) {
            PokemonAttackEffect.pokemonRecallWithAnimation((PokemonEntity) (Object) this);
            return 0;
        }
        PokemonMultipliers pokemonMultipliers = new PokemonMultipliers((PokemonEntity) (Object) this);
        Pokemon pokemon = getPokemon();
        int specialDef = (int) (pokemon.getSpecialDefence() * (FOFHeldItemManager.canUse((PokemonEntity) (Object) this, CobblemonItems.ASSAULT_VEST) ? 1.3f : 1f));
        float def = Math.max(pokemon.getDefence(), specialDef);
        return amount * (1 - pokemonMultipliers.getMaximumDamageReduction() * Math.min(CobblemonFightOrFlight.commonConfig().max_damage_reduction_multiplier, Mth.lerp(def / CobblemonFightOrFlight.commonConfig().defense_stat_limit, 0, CobblemonFightOrFlight.commonConfig().max_damage_reduction_multiplier)));
    }

    @Override
    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        float prevHealth = getHealth();
        super.actuallyHurt(damageSource, damageAmount);
        if (PokemonUtils.isUsingNewHealthMechanic() && invulnerableTime == 20) {
            float newHealth = getHealth();
            float d = newHealth - prevHealth;
            if (d < 0) {
                PokemonUtils.entityHpToPokemonHp((PokemonEntity) (Object) this, -d, false);
            }
        }
        if (CobblemonFightOrFlight.commonConfig().slow_down_after_hurt) {
            if (!getPokemon().isPlayerOwned()) {
                addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));
            }
        }
        var entity = damageSource.getEntity();
        if (damageAmount > 0 && entity instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof PokemonEntity attackerPokemon) {
                if (attackerPokemon.getPokemon().isPlayerOwned()) {
                    Pokemon pokemon = attackerPokemon.getPokemon();
                    HURT_BY_POKEMON_FOF.add(pokemon);
                    LAST_HURT_BY_POKEMON_FOF = pokemon;
                }
                if (damageSource.is(DamageTypes.MOB_ATTACK)) {
                    PokemonEntity pokemonEntity = (PokemonEntity) (Object) this;
                    if (FOFHeldItemManager.canUse(pokemonEntity, CobblemonItems.ROCKY_HELMET)) {
                        entity.hurt(damageSources().thorns(pokemonEntity), livingEntity.getMaxHealth() / 6);
                    }
                    if (PokemonUtils.abilityIs(pokemonEntity, "roughskin") || PokemonUtils.abilityIs(pokemonEntity, "ironbarbs")) {
                        entity.hurt(damageSources().thorns(pokemonEntity), livingEntity.getMaxHealth() / 8);
                    }
                }
            }

        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (!busyLocks.isEmpty()) {
            return true;
        }
        // Don't let Pokémon be hurt during sendout and recall animations
        if (getBeamMode() != 0) {
            return true;
        }

        if (CobblemonFightOrFlight.commonConfig().suffocation_immunity && damageSource.is(DamageTypes.IN_WALL)) {
            return true;
        }

        if (!CobblemonFightOrFlight.commonConfig().pvp_immunity && getOwnerUUID() != null && (damageSource.getEntity() instanceof ServerPlayer player)) {
            if (getBattleId() != null) {
                CobblemonFightOrFlight.LOGGER.info("Occupied by a battle.");
                return true;
            }
        }
        return super.isInvulnerableTo(damageSource);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void hurtImmune(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof ServerPlayer player) {
                LivingEntity ownerEntity = getOwner();
                PokemonEntity thisEntity = (PokemonEntity) (Object) this;
                if (ownerEntity == null && CobblemonFightOrFlight.commonConfig().force_wild_battle_on_player_attack) {
                    PokemonUtils.pokemonForceEncounterPvE(player, thisEntity);
                    cir.setReturnValue(false);
                } else if (ownerEntity instanceof Player && CobblemonFightOrFlight.commonConfig().force_player_battle_on_player_attack) {
                    PokemonUtils.pokemonForceEncounterPvP(player, thisEntity);
                    cir.setReturnValue(false);
                }
            }
            if (!PokemonAttackEffect.shouldBeHurtByAllyMob(((PokemonEntity) (Object) this), livingEntity)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Override
    public void heal(float healAmount) {
        if (PokemonUtils.isUsingNewHealthMechanic()) {
            PokemonUtils.entityHpToPokemonHp((PokemonEntity) (Object) this, healAmount, true);
        }
        super.heal(healAmount);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (Objects.equals(getCommand(), PokeStaffComponent.CMDMODE.CLEAR.name())) {
            setCommand(PokeStaffComponent.CMDMODE.NOCMD.name());
        }
        PokemonEntity thisEntity = (PokemonEntity) (Object) this;
        var targetEntity = PokemonUtils.getTarget(thisEntity);
        int nextCryTime = getNextCryTime();
        if (!getPokemon().isPlayerOwned() && PokemonUtils.shouldFightTarget(thisEntity) && FOFAggressionCalculator.calc(thisEntity) >= CobblemonFightOrFlight.commonConfig().neutral_threshold) {
            boolean targetAvailable = targetEntity != null && targetEntity.isAlive();
            if (nextCryTime > 0) {
                setNextCryTime(nextCryTime - 1);
            }
            if (nextCryTime == 0) {
                setNextCryTime(20);
                if (targetAvailable) {
                    this.cry();
                    if (CobblemonFightOrFlight.commonConfig().multiple_cries) {
                        setNextCryTime(CobblemonFightOrFlight.commonConfig().time_to_cry_again);
                    } else {
                        setNextCryTime(-1);
                    }
                }
            }

            if (ticksUntilNewAngerParticle > 0) {
                --ticksUntilNewAngerParticle;
            }
            if (ticksUntilNewAngerParticle == 0) {
                ticksUntilNewAngerParticle = 25;
                if (targetAvailable) {
                    CobblemonFightOrFlight.PokemonEmoteAngry(this);
                }
            }
        }
        int moveDuration = getMoveDuration();
        int moveDurationOriginal = getMoveDurationOriginal();
        int attackTime = getAttackTime();
        if (moveDuration > -1) {
            setMoveDuration(moveDuration - 1);
        } else {
            if (attackTime > 0) {
                setAttackTime(attackTime - 1);
            }
        }
        useMoveProcess(moveDuration, moveDurationOriginal);
        if (!level().isClientSide) {
            int t = tickCount % 20;
            int sec = tickCount / 20;
            slowTick(t, sec);
            fof$syncState();
            moveEventHandler.update(tickCount);
        }
    }

    @Override
    public void tryUsingStatusMoves() {
        if (getAttackTime() > 0) {
            return;
        }
        PokemonEntity self = (PokemonEntity) (Object) this;
        if (self.getOwner() instanceof Player && !FOFHeldItemManager.canUse(self, CobblemonItems.ASSAULT_VEST)) {
            Move move = PokemonUtils.getStatusMove(self);
            if (move != null && PokemonAttackEffect.canUseMove(self) && CobblemonFightOrFlight.commonConfig().activate_move_effect && MoveData.moveData.containsKey(move.getName())) {
                PokemonAttackEffect.resetMoveDuration(self, 0);
                //PokemonAttackEffect.applyBeforeUseEffect(self, null, move);
            }
        }
    }

    @Unique
    private void useMoveProcess(int moveDuration, int moveDurationOriginal) {
        if (moveDuration == -1 || moveDurationOriginal == -1) {
            return;
        }
        PokemonEntity self = (PokemonEntity) (Object) this;
        int chargeState = (int) (0.333 * moveDurationOriginal);
        int attackState = (int) (0.667 * moveDurationOriginal);
        int finishAttackState = (int) (0.75 * moveDurationOriginal);
        int passedTime = moveDurationOriginal - moveDuration;
        if (passedTime == chargeState) {
            addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, moveDurationOriginal - chargeState, 2, false, false));
        }
        if (passedTime >= chargeState && passedTime <= attackState) {
            Move move = PokemonUtils.getMove(self);
            if (move != null) {
                PokemonUtils.makeParticle(2, self, PokemonAttackEffect.getParticleFromType(PokemonAttackEffect.modifyElementalTypeByAbility(self, move)));
            }
        }
        if (passedTime >= chargeState && passedTime == attackState - 5) {
            if (((PokemonInterface) self).usingSound()) {
                PokemonUtils.createSonicBoomParticle(self, getTarget());
            }
        }
        if (passedTime == attackState) {
            int attackMode = getAttackMode();
            switch (attackMode) {
                case 0: {
                    Move move = PokemonUtils.getStatusMove(self);
                    if (move != null && PokemonAttackEffect.canUseMove(self) && CobblemonFightOrFlight.commonConfig().activate_move_effect && MoveData.moveData.containsKey(move.getName())) {
                        for (MoveData data : MoveData.moveData.get(move.getName())) {
                            data.invoke(self, null);
                        }
                        PokemonUtils.makeParticle(10, self, ParticleTypes.HAPPY_VILLAGER);
                        PokemonUtils.sendAnimationPacket(self, "status");
                        PokemonAttackEffect.refreshAttackTime(self, 300);
                    }
                    break;
                }
                case 2:
                    PokemonAttackEffect.pokemonPerformRangedAttack(self, getTarget());
                    break;
                default:
                    break;
            }
        }
        if (passedTime == finishAttackState) {
            PokemonUtils.makeParticle(6, self, ParticleTypes.SPIT);
        }
    }

    @Unique
    private void slowTick(int ticks, int sec) {
        if (ticks == 11) {
            updateAttackMode();
            backendMoveCooldown();

            //FOFUtils.checkEntityActivity(this);
        }
        if (sec % 5 == 4 && ticks == 17) {
            turnBasedHeldItemTrigger();
        }
        int switchMoveInterval = CobblemonFightOrFlight.commonConfig().wild_alpha_switch_move_interval;
        if (sec % switchMoveInterval == 0 && ticks == 17) {
            wildRandomMove();
        }
    }

    @Unique
    private void fof$markStateDirty() {
        if (this.level() == null || this.level().isClientSide) {
            return;
        }
        fof$stateDirty = true;
    }

    @Unique
    private void fof$syncState() {
        PokemonEntity self = (PokemonEntity) (Object) this;
        int tick = this.tickCount;
        // Push the changes of the last two ticks to the players that can see this pokemon.
        if (tick % 2 == 0 && fof$stateDirty) {
            List<ServerPlayer> players = FOFStateSync.nearPlayers(self);
            if (!players.isEmpty()) {
                FOFStateSync.sendState(self, players);
                for (ServerPlayer player : players) {
                    fof$lastStateSync.put(player.getUUID(), tick);
                }
            }
            fof$stateDirty = false;
        }
        // Catch up players that started tracking this pokemon after it was spawned (or that lost the packet).
        // The resend happens in the entity tick instead of on a tracking event, because the tracking events fire
        // before the client actually knows the entity, which would make the packet get dropped.
        if (tick >= 20 && tick % 20 == 0) {
            List<ServerPlayer> players = FOFStateSync.nearPlayers(self);
            for (ServerPlayer player : players) {
                Integer lastSent = fof$lastStateSync.get(player.getUUID());
                if (lastSent == null || tick - lastSent > FOFStateSync.RESYNC_INTERVAL) {
                    FOFStateSync.sendState(self, player);
                    fof$lastStateSync.put(player.getUUID(), tick);
                }
            }
            if (!fof$lastStateSync.isEmpty()) {
                Set<UUID> nearby = new HashSet<>();
                for (ServerPlayer player : players) {
                    nearby.add(player.getUUID());
                }
                fof$lastStateSync.keySet().retainAll(nearby);
            }
        }
    }

    @Unique
    private void turnBasedHeldItemTrigger() {
        if (!FOFHeldItemManager.canUseHeldItemHPInfluencing()) {
            return;
        }
        PokemonEntity pokemonEntity = (PokemonEntity) (Object) this;
        Pokemon pokemon = pokemonEntity.getPokemon();
        float maxHealth = pokemonEntity.getMaxHealth();
        if (FOFHeldItemManager.canUse(pokemonEntity, CobblemonItems.LEFTOVERS)) {
            heal(maxHealth / 16);
        } else if (FOFHeldItemManager.canUse(pokemonEntity, CobblemonItems.STICKY_BARB)) {
            if (!PokemonUtils.abilityIs(pokemonEntity, "magicguard")) {
                hurt(damageSources().magic(), maxHealth / 8);
            }
        } else if (FOFHeldItemManager.canUse(pokemonEntity, CobblemonItems.BLACK_SLUDGE)) {
            if (PokemonUtils.hasType(pokemon, ElementalTypes.POISON)) {
                heal(maxHealth / 16);
            } else if (!PokemonUtils.abilityIs(pokemonEntity, "magicguard")) {
                hurt(damageSources().magic(), maxHealth / 8);
            }
        }
    }

    @Unique
    private void backendMoveCooldown() {
        //CobblemonFightOrFlight.LOGGER.info("{}-{} {}:backend move running cooldown", tickCount, lastActivatedTick, getPokemon().getSpecies().getName());
        String moveName = getCurrentMove();
        for (FOFMove move : MOVES_FOF) {
            if (!Objects.equals(move.getName(), moveName)) {
                int remainingTime = move.getRemainingCooldown();
                if (remainingTime > 25) {
                    move.setRemainingCooldown(remainingTime - 20);
                }
            }
        }
    }

    @Unique
    private void updateAttackMode() {
        PokemonEntity pokemonEntity = (PokemonEntity) (Object) this;
        Pokemon pokemon = pokemonEntity.getPokemon();
        Move move = PokemonUtils.getMove(pokemonEntity);
        boolean attackIsHigher = pokemon.getAttack() > pokemon.getSpecialAttack();//The default setting.
        boolean hasOwner = pokemonEntity.getOwner() != null;//The pokemon has a trainer.
        boolean moveAvailable = move != null;
        boolean wildCanUseRange = !hasOwner && !attackIsHigher && (CobblemonFightOrFlight.commonConfig().wild_pokemon_ranged_attack || CobblemonFightOrFlight.commonConfig().wild_alpha_ranged_attack && PokemonUtils.isAlpha(pokemonEntity));
        if (moveAvailable) {
            if (hasOwner) {
                if (PokemonUtils.isMeleeAttackMove(move)) {
                    setAttackMode(1);
                } else if (PokemonUtils.isRangeAttackMove(move)) {
                    setAttackMode(2);
                } else {
                    setAttackMode(0);
                }
            } else {
                if (PokemonAttackEffect.canUseMove(pokemonEntity)) {
                    if (wildCanUseRange && PokemonUtils.isRangeAttackMove(move)) {
                        setAttackMode(2);
                    } else {
                        setAttackMode(1);
                    }
                } else {
                    if (wildCanUseRange) {
                        setAttackMode(2);
                    } else {
                        setAttackMode(1);
                    }
                }
            }
            //CobblemonFightOrFlight.LOGGER.info("Current attack mode: {}", getAttackMode());
            setCurrentMove(move);
        } else {
            if (wildCanUseRange) {
                setAttackMode(2);
            } else {
                setAttackMode(1);
            }
        }
    }

    @Unique
    private void wildRandomMove() {
        if (getOwner() != null) {
            return;
        }
        PokemonEntity pokemonEntity = (PokemonEntity) (Object) this;
        if (!busyLocks.isEmpty()) {
            return;
        }
        if (getTarget() == null) {
            return;
        }
        if (CobblemonFightOrFlight.commonConfig().wild_alpha_switch_move && PokemonUtils.isAlpha(pokemonEntity) && PokemonAttackEffect.canUseMove(pokemonEntity)) {
            //CobblemonFightOrFlight.LOGGER.info("Trying to switch move.");
            Pokemon pokemon = getPokemon();
            int attackStatGap = pokemon.getSpecialAttack() - pokemon.getAttack();
            List<DamageCategory> allowedCategories = new ArrayList<>();
            if (Mth.abs(attackStatGap) <= 30 || !CobblemonFightOrFlight.commonConfig().wild_alpha_switch_move_stat_check) {
                allowedCategories.add(DamageCategories.INSTANCE.getPHYSICAL());
                allowedCategories.add(DamageCategories.INSTANCE.getSPECIAL());
            } else if (attackStatGap > 0) {
                allowedCategories.add(DamageCategories.INSTANCE.getSPECIAL());
            } else {
                allowedCategories.add(DamageCategories.INSTANCE.getPHYSICAL());
            }
            //TODO unfinished
            var moves = pokemon.getMoveSet();
            List<Move> attackMoves = new ArrayList<>();
            for (Move move : moves) {
                if (allowedCategories.contains(move.getDamageCategory())) {
                    attackMoves.add(move);
                }
            }
            if (attackMoves.isEmpty()) {
                return;
            }
            int randIndex = random.nextInt(attackMoves.size());
            if (PokemonAttackEffect.canChangeMove(pokemonEntity, null)) {
                Move oldMove = PokemonUtils.getMove(pokemonEntity);
                Move newMove = attackMoves.get(randIndex);
                if (oldMove == null || (newMove != null && !Objects.equals(oldMove.getName(), newMove.getName()))) {
                    switchMove(newMove);
                    PokemonUtils.makeParticle(6, this, ParticleTypes.ENCHANTED_HIT);
                }
            }
        }
    }

    @Override
    public void refreshMovesList() {
        if (level().isClientSide) {
            return;
        }
        MOVES_FOF.clear();
        Pokemon pokemon = getPokemon();
        for (Move move : pokemon.getMoveSet()) {
            MOVES_FOF.add(new FOFMove(move.getName(), 0, 0));
        }
    }

    @Override
    public void switchMove(Move move) {
        if (level().isClientSide || move == null) {
            return;
        }
        if (MOVES_FOF.isEmpty()) {
            refreshMovesList();
        }
        String oldMoveName = getCurrentMove();
        int index = 0;
        while (index < 4) {
            FOFMove tmpMove = MOVES_FOF.get(index);
            if (tmpMove != null && Objects.equals(tmpMove.getName(), oldMoveName)) {
                tmpMove.setRemainingCooldown(getAttackTime());
                tmpMove.setOriginalCooldown(getMaxAttackTime());
                for (int i = 0; i < 4; ++i) {
                    FOFMove m = MOVES_FOF.get(i);
                    if (m != null && Objects.equals(m.getName(), move.getName())) {
                        if (m.getRemainingCooldown() < 10) {
                            setAttackTime(10);
                            setMaxAttackTime(10);
                        } else if (m.getRemainingCooldown() > 10) {
                            setAttackTime(m.getRemainingCooldown());
                            setMaxAttackTime(m.getOriginalCooldown());
                        }
                        break;
                    }
                }
                break;
            }
            ++index;
        }
        if (CobblemonFightOrFlight.visualEffectConfig().show_switch_move_msg_server) {
            var playerList = level().getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(position(), 16, 16, 16));
            for (ServerPlayer player : playerList) {
                PokemonEntity pokemonEntity = (PokemonEntity) (Object) this;
                MoveInfoPacket packet = new MoveInfoPacket(getId(), move.getName(), PokemonAttackEffect.getFactionCode(pokemonEntity, player));
                NetworkManager.sendToPlayer(player, packet);
            }
        }
        setCurrentMove(move);
    }

    @Unique
    private void shareYield(PokemonEntity self) {
        int pokemonCount = HURT_BY_POKEMON_FOF.size();
        int faintedPokemonCount = 0;
        for (Pokemon pokemon : HURT_BY_POKEMON_FOF) {
            if (pokemon.isFainted()) {
                faintedPokemonCount++;
            }
        }
        for (Pokemon pokemon : HURT_BY_POKEMON_FOF) {
            if (pokemon.isFainted()) {
                continue;
            }
            pokemon.addExperience(new SidemodExperienceSource(CobblemonFightOrFlight.MODID), FOFExpCalculator.calculate(pokemon, self.getPokemon(), pokemonCount - faintedPokemonCount));
            if (CobblemonFightOrFlight.commonConfig().can_gain_ev) {
                var map = FOFEVCalculator.calculate(pokemon, self.getPokemon());
                for (Map.Entry<Stat, Integer> entry : map.entrySet()) {
                    EvSource evSource = new SidemodEvSource(CobblemonFightOrFlight.MODID, pokemon);
                    pokemon.getEvs().add(entry.getKey(), entry.getValue(), evSource);
                }
            }
        }
    }

    @Unique
    private void lastTakeYield(PokemonEntity self) {
        Pokemon pokemon = LAST_HURT_BY_POKEMON_FOF;
        pokemon.addExperience(new SidemodExperienceSource(CobblemonFightOrFlight.MODID), FOFExpCalculator.calculate(pokemon, self.getPokemon()));
        if (CobblemonFightOrFlight.commonConfig().can_gain_ev) {
            var map = FOFEVCalculator.calculate(pokemon, self.getPokemon());
            for (Map.Entry<Stat, Integer> entry : map.entrySet()) {
                EvSource evSource = new SidemodEvSource(CobblemonFightOrFlight.MODID, pokemon);
                pokemon.getEvs().add(entry.getKey(), entry.getValue(), evSource);
            }
        }
    }

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    private void dropAllDeathLootInject(ServerLevel world, DamageSource source, CallbackInfo ci) {
        if (HURT_BY_POKEMON_FOF.isEmpty()) {
            return;
        }
        PokemonEntity self = (PokemonEntity) (Object) this;
        if (CobblemonFightOrFlight.commonConfig().pokemon_share_yield) {
            shareYield(self);
        } else {
            lastTakeYield(self);
        }
    }

    @Override
    public void addMoveEvent(FOFMoveEvent event) {
        moveEventHandler.addEvent(event);
    }
}
