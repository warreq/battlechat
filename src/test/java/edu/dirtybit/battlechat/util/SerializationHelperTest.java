package edu.dirtybit.battlechat.util;

import com.google.gson.Gson;
import edu.dirtybit.battlechat.BattleShipConfiguration;
import edu.dirtybit.battlechat.model.BattleChatStatus;
import edu.dirtybit.battlechat.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class SerializationHelperTest {

    @Test
    public void SerializationHelper_DeserializeChat_BodyShouldBeString() {
        Gson gson = new Gson();
        GameMessage<String> msg = new GameMessage<>(GameMessageType.CHAT, UUID.randomUUID(), "hello");
        String json = gson.toJson(msg, GameMessage.class);

        GameMessage chat = SerializationHelper.deserializeMessage(json);
        assertEquals(chat.getBody().getClass(), String.class);
    }

    @Test
    public void SerializationHelper_DeserializeConfiguration_BodyShouldBeBattleShipConfguration() {
        Gson gson = new Gson();
        BattleShipConfiguration c = new BattleShipConfiguration();
        GameMessage<BattleShipConfiguration> msg = new GameMessage<>(GameMessageType.CONFIGURATION, UUID.randomUUID(), c);
        String json = gson.toJson(msg, GameMessage.class);

        GameMessage config = SerializationHelper.deserializeMessage(json);
        assertEquals(config.getBody().getClass(), BattleShipConfiguration.class);
    }

    @Test
    public void SerializationHelper_DeserializePlacement_BodyShouldBeFleet() {
        Gson gson = new Gson();
        Fleet f = Fleet.fromConfig(new BattleShipConfiguration());
        GameMessage<Fleet> msg = new GameMessage<>(GameMessageType.PLACEMENT, UUID.randomUUID(), f);
        String json = gson.toJson(msg, GameMessage.class);

        GameMessage config = SerializationHelper.deserializeMessage(json);
        assertEquals(config.getBody().getClass(), Fleet.class);
    }

    @Test
    public void SerializationHelper_DeserializeStatus_BodyShouldBeBattleChatStatus() {
        Gson gson = new Gson();
        BattleChatStatus s = new BattleChatStatus(BattleChatStatus.Phase.YOU_WIN, 1);
        s.setGamePhase(BattleChatStatus.Phase.NOT_STARTED);
        s.setSecondsToPhaseChange(30);
        GameMessage<BattleChatStatus> msg = new GameMessage<>(GameMessageType.STATUS, UUID.randomUUID(), s);
        String json = gson.toJson(msg, GameMessage.class);

        GameMessage status = SerializationHelper.deserializeMessage(json);
        assertEquals(status.getBody().getClass(), BattleChatStatus.class);
    }

    @Test
    public void SerializationHelper_DeserializeFire_BodyShouldBeCoordinateArray() {
        Gson gson = new Gson();
        ArrayList<Coordinate> c = new ArrayList<>();
        c.add(new Coordinate(0, 1, 1));
        GameMessage<ArrayList<Coordinate>> msg = new GameMessage<>(GameMessageType.FIRE, UUID.randomUUID(), c);
        String json = gson.toJson(msg, GameMessage.class);

        GameMessage<ArrayList<Coordinate>> coordinate = SerializationHelper.deserializeMessage(json);
        assertEquals(coordinate.getBody().getClass(), ArrayList.class);
        assertEquals(coordinate.getBody().get(0).getClass(), Coordinate.class);
    }
}