package me.rufia.fightorflight.net.handler;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSeenEvent;
import com.cobblemon.mod.common.api.text.TextKt;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.ChallengeManager;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.architectury.networking.NetworkManager;
import kotlin.Unit;
import me.rufia.fightorflight.net.NetworkPacketHandler;
import me.rufia.fightorflight.net.packet.FOFStartBattlePokemonPacket;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class FOFStartBattlePokemonHandler implements NetworkPacketHandler<FOFStartBattlePokemonPacket> {

    @Override
    public void handle(FOFStartBattlePokemonPacket packet, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();
        if (player instanceof ServerPlayer serverPlayer) {
            UUID uuid=packet.getPlayerPokemonUUID();
            var pokemon = Cobblemon.INSTANCE.getStorage().getParty(serverPlayer).get(uuid);
            if (pokemon == null) {
                return;
            }

            var entity = player.level().getEntity(packet.getPokemonEntityID());

            if (entity instanceof PokemonEntity pokemonEntity && pokemonEntity.canBattle(player)) {
                BattleBuilder.INSTANCE.pve(serverPlayer, pokemonEntity, pokemon.getUuid()).ifSuccessful((battle) -> flagAsSeen(battle, pokemonEntity)
                ).ifErrored((error) -> {
                    error.sendTo(player, TextKt::red);
                    return Unit.INSTANCE;
                });
            } else if (entity instanceof ServerPlayer challengedTrainer) {
                //This part is written in challenge handler so this is modified and kept, I'm not sure if it will work.
                ChallengeManager.INSTANCE.setLead(serverPlayer, pokemon.getUuid());
                var challenge = new ChallengeManager.SinglesBattleChallenge(serverPlayer, challengedTrainer, pokemon.getUuid(), BattleFormat.Companion.getGEN_9_SINGLES(), 20);
                ChallengeManager.INSTANCE.sendRequest(challenge);
            }
        }
    }

    private Unit flagAsSeen(PokemonBattle battle, PokemonEntity pokemonEntity) {
        var actor = battle.getActor(pokemonEntity.getPokemon().getUuid());
        if (actor instanceof PokemonBattleActor pokemonBattleActor) {
            var battlePokemonList = pokemonBattleActor.getPokemonList();
            BattlePokemon battlePokemon = battlePokemonList.stream().filter(pkm -> pkm.getUuid() == pokemonEntity.getPokemon().getUuid()).findFirst().orElse(null);
            if (battlePokemon == null) {
                return Unit.INSTANCE;
            }
            battle.getPlayerUUIDs().forEach(uuid -> {
                CobblemonEvents.POKEMON_SEEN.post(new PokemonSeenEvent(uuid,battlePokemon.getEffectedPokemon()));
            });
        }
        return Unit.INSTANCE;
    }
}
