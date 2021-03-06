package edu.dirtybit.battlechat;

import edu.dirtybit.battlechat.model.GameMessage;
import edu.dirtybit.battlechat.model.GameMessageType;
import edu.dirtybit.battlechat.model.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.HashSet;
import java.util.Set;

public class SessionManager implements SessionListener, LobbyListener {
    private ConcurrentMap<UUID, UUID> playerToSession;
    private ConcurrentMap<UUID, Session> sessions;
    private ConcurrentLinkedQueue<Session> queue;
    private BattleShipConfiguration defaultConfig = new BattleShipConfiguration();

    public SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
        this.playerToSession = new ConcurrentHashMap<>();
        this.queue = new ConcurrentLinkedQueue<>();
        Lobby.INSTANCE.subscribe(this);

        Runnable keepAlive = () -> {
            while(true) {
                sessions.values().forEach(s -> {
                    if (s.getStatus() == SessionStatus.ENQUEUED) {
                        s.getPlayers().forEach(p -> notifySubscriber(s, new GameMessage<>(GameMessageType.KEEPALIVE, p.getId(), "Waiting to be matched...")));
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(keepAlive).start();
    }

    public Session getSessionContainingPlayer(UUID player) {
        UUID lookup = this.playerToSession.get(player);
        if(lookup == null) {
            return null;
        } else {
            return this.sessions.get(lookup);
        }
    }

    public Session getSession(UUID session) {
        return this.sessions.get(session);
    }

    public UUID enterQueue(Player player) throws Exception {
        if(!this.queue.isEmpty()) {
            return addToExisting(this.queue.peek(), player);
        } else {
            return addToNewGame(player);
        }
    }

    public void notifySubscriber(Session obj, GameMessage message) {
        Lobby.INSTANCE.message(message);

        switch (obj.getStatus()) {
            case COMPLETED:
                while(this.playerToSession.values().remove(obj.getId()));
                this.sessions.remove(obj.getId());
                break;
            case IN_PROGRESS:
                this.queue.remove(obj);
                break;
        }
    }

    @Override
    public void receiveMessage(GameMessage message) {
        Session s = this.getSessionContainingPlayer(message.getId());
        if(message.getMessageType() == GameMessageType.CHAT) {
            if (s != null) {
                String sender = s.getPlayerById(message.getId()).getGivenName();
                s.getPlayers().forEach(p -> Lobby.INSTANCE.message(new GameMessage(GameMessageType.CHAT,
                        p.getId(), String.format("%s: %s \n", sender, message.getBody()))));
            }
        } else {
            s.handleMessage(message);
        }
    }

    private UUID addToNewGame(Player player) throws Exception {
       Set<SessionListener> listeners = new HashSet<>();
       Session newGame = SessionFactory.INSTANCE.createSession(defaultConfig, player);
       newGame.subscribe(this);
       this.sessions.put(newGame.getId(), newGame);
       this.playerToSession.put(newGame.getInitiator(), newGame.getId());
       this.queue.add(newGame);
       return newGame.getInitiator();
    }

    private UUID addToExisting(Session session, Player player) {
       this.playerToSession.put(player.getId(), session.getId());
       return session.enqueuePlayer(player);
    }
}
