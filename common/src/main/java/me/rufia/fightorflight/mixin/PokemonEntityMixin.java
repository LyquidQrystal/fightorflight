package me.rufia.fightorflight.mixin;


import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.experience.SidemodExperienceSource;
import com.cobblemon.mod.common.api.pokemon.stats.BattleEvSource;
import com.cobblemon.mod.common.api.pokemon.stats.EvSource;
import com.cobblemon.mod.common.api.pokemon.stats.SidemodEvSource;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.data.movedata.MoveData;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.entity.ai.sensors.FOFSensors;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.utils.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
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
public abstract class PokemonEntityMixin extends Mob implements PokemonInterface {
    @Shadow(remap = false)
    public abstract void cry();

    @Shadow(remap = false)
    public abstract Pokemon getPokemon();

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
    @Unique
    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET;
    @Unique
    private static final EntityDataAccessor<Integer> DATA_ID_CAPTURED_BY;
    @Unique
    private static final EntityDataAccessor<Integer> ATTACK_MODE;
    @Unique
    private static final EntityDataAccessor<Integer> ATTACK_TIME;
    @Unique
    private static final EntityDataAccessor<Integer> MAX_ATTACK_TIME;
    @Unique
    private static final EntityDataAccessor<String> MOVE;
    @Unique
    private static final EntityDataAccessor<Integer> CRY_CD;
    @Unique
    private static final EntityDataAccessor<String> COMMAND;
    @Unique
    private static final EntityDataAccessor<String> COMMAND_DATA;
    @Unique
    private static final EntityDataAccessor<BlockPos> TARGET_BLOCK_POS;

    @Unique
    private static final EntityDataAccessor<Integer> MOVE_DURATION;

    @Unique
    private final List<FOFMove> MOVES_FOF = new ArrayList<>();//This should only be accessed in the server side!

    static {
        DATA_ID_ATTACK_TARGET = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.INT);
        DATA_ID_CAPTURED_BY = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.INT);
        ATTACK_TIME = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.INT);
        MAX_ATTACK_TIME = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.INT);
        MOVE = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.STRING);
        CRY_CD = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.INT);
        COMMAND = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.STRING);
        COMMAND_DATA = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.STRING);
        TARGET_BLOCK_POS = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.BLOCK_POS);
        ATTACK_MODE = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.INT);//0 means the pokemon can't attack, 1 for melee, 2 for range attack.
        MOVE_DURATION = SynchedEntityData.defineId(PokemonEntityMixin.class, EntityDataSerializers.INT);
    }

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
                Entity entity = this.level().getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET));
                if (entity instanceof LivingEntity) {
                    fightorflight$clientSideCachedAttackTarget = (LivingEntity) entity;
                    return fightorflight$clientSideCachedAttackTarget;
                }
            }
        }
        return super.getTarget();
    }

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    public void onSyncedDataUpdated(EntityDataAccessor<?> key, CallbackInfo ci) {
        if (DATA_ID_ATTACK_TARGET.equals(key)) {
            this.fightorflight$clientSideCachedAttackTarget = null;
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo callbackInfo) {
        builder.define(DATA_ID_ATTACK_TARGET, 0);
        builder.define(DATA_ID_CAPTURED_BY, 0);
        builder.define(ATTACK_TIME, 0);
        builder.define(MAX_ATTACK_TIME, -1);
        builder.define(MOVE, "");
        builder.define(CRY_CD, 0);
        builder.define(COMMAND, "");
        builder.define(COMMAND_DATA, "");
        builder.define(TARGET_BLOCK_POS, BlockPos.ZERO);
        builder.define(ATTACK_MODE, 0);
        builder.define(MOVE_DURATION, 0);
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    private void writeAdditionalNbt(CompoundTag compoundTag, CallbackInfoReturnable<Boolean> ci) {
        compoundTag.putInt(CRY_CD.toString(), 0);
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void readAdditionalNbt(CompoundTag compoundTag, CallbackInfo ci) {
        entityData.set(CRY_CD, compoundTag.getInt(CRY_CD.toString()));
    }

    @ModifyVariable(method = "assignNewBrainWithMemoriesAndSensors", at = @At("HEAD"), argsOnly = true, index = 3)
    private Set<SensorType<?>> assignNewBrainWithMemoriesAndSensorsMixin(Set<SensorType<?>> sensors) {
        HashSet<SensorType<?>> hashSet = new HashSet<>(sensors);
        hashSet.add(FOFSensors.POKEMON_HELP_OWNER);
        hashSet.add(FOFSensors.POKEMON_WILD_PROACTIVE);
        return Set.copyOf(hashSet);
    }

    public void setTarget(LivingEntity target) {
        super.setTarget(target);
        if (target != null) {
            this.entityData.set(DATA_ID_ATTACK_TARGET, target.getId());
        }
    }

    @Override
    public int getAttackTime() {
        return entityData.get(ATTACK_TIME);
    }

    @Override
    public void setAttackTime(int val) {
        entityData.set(ATTACK_TIME, val);
    }

    @Override
    public int getMaxAttackTime() {
        return entityData.get(MAX_ATTACK_TIME);
    }

    @Override
    public void setMaxAttackTime(int val) {
        entityData.set(MAX_ATTACK_TIME, val);
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
        entityData.set(MOVE, move.getName());
    }

    @Override
    public String getCurrentMove() {
        return entityData.get(MOVE);
    }

    @Override
    public int getNextCryTime() {
        return this.entityData.get(CRY_CD);
    }

    @Override
    public void setNextCryTime(int time) {
        this.entityData.set(CRY_CD, time);
    }

    @Override
    public void setCommand(String cmd) {
        entityData.set(COMMAND, cmd);
    }

    @Override
    public String getCommand() {
        return entityData.get(COMMAND);
    }

    @Override
    public void setCommandData(String cmdData) {
        entityData.set(COMMAND_DATA, cmdData);
        createTargetBlockPos();
    }

    @Override
    public String getCommandData() {
        return entityData.get(COMMAND_DATA);
    }

    @Override
    public BlockPos getTargetBlockPos() {
        return this.entityData.get(TARGET_BLOCK_POS);
    }

    @Override
    public void setTargetBlockPos(BlockPos blockPos) {
        this.entityData.set(TARGET_BLOCK_POS, blockPos);
    }

    @Override
    public int getCapturedBy() {
        return entityData.get(DATA_ID_CAPTURED_BY);
    }

    @Override
    public void setCapturedBy(int id) {
        entityData.set(DATA_ID_CAPTURED_BY, id);
    }

    @Override
    public int getAttackMode() {
        return entityData.get(ATTACK_MODE);
    }

    @Override
    public void setAttackMode(int attackMode) {
        entityData.set(ATTACK_MODE, attackMode);
    }

    @Override
    public int getMoveDuration() {
        return entityData.get(MOVE_DURATION);
    }

    @Override
    public void setMoveDuration(int duration) {
        entityData.set(MOVE_DURATION, duration);
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
        int specialDef = (int) (pokemon.getSpecialDefence() * (FOFHeldItemManager.canUse(pokemon, CobblemonItems.ASSAULT_VEST) ? 1.3f : 1f));
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
        if (damageSource.is(DamageTypes.MOB_ATTACK) && damageAmount > 0) {
            PokemonEntity pokemonEntity = (PokemonEntity) (Object) this;
            var entity = damageSource.getEntity();
            if (entity instanceof LivingEntity livingEntity) {
                if (FOFHeldItemManager.canUse(pokemonEntity, CobblemonItems.ROCKY_HELMET)) {
                    entity.hurt(damageSources().thorns(pokemonEntity), livingEntity.getMaxHealth() / 6);
                }
                if (PokemonUtils.abilityIs(pokemonEntity, "roughskin") || PokemonUtils.abilityIs(pokemonEntity, "ironbarbs")) {
                    entity.hurt(damageSources().thorns(pokemonEntity), livingEntity.getMaxHealth() / 8);
                }
            }
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void hurtImmune(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (CobblemonFightOrFlight.commonConfig().suffocation_immunity && source.type().equals(damageSources().inWall().type())) {
            cir.setReturnValue(false);
        }
        if (source.getEntity() instanceof LivingEntity livingEntity) {
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
        var targetEntity = PokemonUtils.getTarget((PokemonEntity) (Object) this);
        int nextCryTime = getNextCryTime();
        if (!getPokemon().isPlayerOwned() && targetEntity != null && targetEntity.isAlive()) {
            if (nextCryTime < 1) {
                this.cry();
                if (CobblemonFightOrFlight.commonConfig().multiple_cries) {
                    setNextCryTime(CobblemonFightOrFlight.commonConfig().time_to_cry_again);
                } else {
                    setNextCryTime(-1);
                }
            } else {
                setNextCryTime(nextCryTime - 1);
            }

            if (ticksUntilNewAngerParticle < 1) {
                CobblemonFightOrFlight.PokemonEmoteAngry(this);
                ticksUntilNewAngerParticle = 25;
            } else {
                --ticksUntilNewAngerParticle;
            }
        } else {
            setNextCryTime(0);
            ticksUntilNewAngerParticle = 0;
        }

        int attackTime = getAttackTime();
        if (attackTime > -1) {
            setAttackTime(attackTime - 1);
        }
        if (!level().isClientSide) {
            int t = tickCount % 20;
            int sec = tickCount / 20;
            slowTick(t, sec);
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
            if (move != null && CobblemonFightOrFlight.commonConfig().activate_move_effect && MoveData.moveData.containsKey(move.getName())) {
                for (MoveData data : MoveData.moveData.get(move.getName())) {
                    data.invoke(self, null);
                }
                PokemonUtils.makeParticle(10, self, ParticleTypes.HAPPY_VILLAGER);
                PokemonUtils.sendAnimationPacket(self, "status");
                setAttackTime(300);
                setMaxAttackTime(300);
            }
        }
    }

    @Unique
    private void slowTick(int ticks, int sec) {
        //CobblemonFightOrFlight.LOGGER.info("slowTick...");//It works
        if (ticks == 11) {
            updateAttackMode();
            backendMoveCooldown();
        }
        if (sec % 5 == 0 && ticks == 17) {
            turnBasedHeldItemTrigger();
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
            } else {
                if (!PokemonUtils.abilityIs(pokemonEntity, "magicguard")) {
                    hurt(damageSources().magic(), maxHealth / 8);
                }
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
        boolean hasOwner = pokemonEntity.getOwner() != null;//The pokemon has no trainer.
        boolean moveAvailable = move != null;
        if (hasOwner && moveAvailable) {
            if (PokemonUtils.isMeleeAttackMove(move)) {
                setAttackMode(1);
            } else if (PokemonUtils.isRangeAttackMove(move)) {
                setAttackMode(2);
            } else {
                setAttackMode(0);
            }
            //CobblemonFightOrFlight.LOGGER.info("Current attack mode: {}", getAttackMode());
            setCurrentMove(move);
        } else {
            if (!attackIsHigher && CobblemonFightOrFlight.commonConfig().wild_pokemon_ranged_attack) {
                setAttackMode(2);
            } else {
                setAttackMode(1);
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
            if (tmpMove != null) {
                if (Objects.equals(tmpMove.getName(), oldMoveName)) {
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
            }
            ++index;
        }
        setCurrentMove(move);
    }

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    private void dropAllDeathLootInject(ServerLevel world, DamageSource source, CallbackInfo ci) {
        if (getLastHurtByMob() instanceof PokemonEntity pokemonEntity && pokemonEntity.getOwner() != null) {
            PokemonEntity self = (PokemonEntity) (Object) this;
            pokemonEntity.getPokemon().addExperience(new SidemodExperienceSource(CobblemonFightOrFlight.MODID), FOFExpCalculator.calculate(pokemonEntity.getPokemon(), self.getPokemon()));
            if (CobblemonFightOrFlight.commonConfig().can_gain_ev) {
                var map = FOFEVCalculator.calculate(pokemonEntity.getPokemon(), self.getPokemon());
                for (Map.Entry<Stat, Integer> entry : map.entrySet()) {
                    EvSource evSource = new SidemodEvSource(CobblemonFightOrFlight.MODID, pokemonEntity.getPokemon());
                    pokemonEntity.getPokemon().getEvs().add(entry.getKey(), entry.getValue(), evSource);
                }
            }
        }
    }
}
