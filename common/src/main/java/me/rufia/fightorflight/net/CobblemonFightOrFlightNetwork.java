package me.rufia.fightorflight.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import me.rufia.fightorflight.net.handler.FOFStartBattlePokemonHandler;
import me.rufia.fightorflight.net.handler.FOFStartBattleRequestHandler;
import me.rufia.fightorflight.net.handler.SendCommandHandler;
import me.rufia.fightorflight.net.handler.SendMoveSlotHandler;
import me.rufia.fightorflight.net.packet.FOFStartBattlePokemonPacket;
import me.rufia.fightorflight.net.packet.FOFStartBattleRequestPacket;
import me.rufia.fightorflight.net.packet.SendCommandPacket;
import me.rufia.fightorflight.net.packet.SendMoveSlotPacket;

public class CobblemonFightOrFlightNetwork {
    public static void registerC2S() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SendCommandPacket.TYPE, SendCommandPacket.STREAM_CODEC, ((packet, context) -> {
            SendCommandHandler handler = new SendCommandHandler();
            handler.handle(packet, context);
        }));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SendMoveSlotPacket.TYPE, SendMoveSlotPacket.STREAM_CODEC, ((packet, context) -> {
            SendMoveSlotHandler handler = new SendMoveSlotHandler();
            handler.handle(packet, context);
        }));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, FOFStartBattlePokemonPacket.TYPE, FOFStartBattlePokemonPacket.STREAM_CODEC, (packet, context) -> {
            FOFStartBattlePokemonHandler handler = new FOFStartBattlePokemonHandler();
            handler.handle(packet, context);
        });
    }

    public static void registerS2C() {
        if( Platform.getEnvironment() ==Env.CLIENT){
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, FOFStartBattleRequestPacket.TYPE, FOFStartBattleRequestPacket.STREAM_CODEC, (packet, context) -> {
                FOFStartBattleRequestHandler handler = new FOFStartBattleRequestHandler();
                handler.handle(packet, context);
            });}
        else {
            NetworkManager.registerS2CPayloadType(FOFStartBattleRequestPacket.TYPE, FOFStartBattleRequestPacket.STREAM_CODEC);
        }
    }

    public static void init() {
        registerC2S();
        registerS2C();
    }
}
