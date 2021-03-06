package edu.dirtybit.battlechat;

import edu.dirtybit.battlechat.model.Player;
import edu.dirtybit.battlechat.model.GameMessage;
import edu.dirtybit.battlechat.model.GameMessageType;

import java.util.*;
import java.util.List;

public abstract class Session implements Runnable {
    private GameConfiguration config;
    private UUID id;
    private List<Player> players;
    private Set<SessionListener> subscribers;
    protected SessionStatus status;

    public Session(GameConfiguration config, Player player) {
       this.config = config;
       this.id = UUID.randomUUID();
       this.players = new ArrayList<>();
       this.players.add(player);
       this.subscribers = new HashSet<>();
       this.status = SessionStatus.ENQUEUED;
    }

    public void subscribe(SessionListener subscriber) {
        this.subscribers.add(subscriber);
    }

    public SessionStatus getStatus() {
        return this.status;
    }

    protected void setStatus(SessionStatus status) {
        this.status = status;
    }

    public UUID getId() {
        return this.id;
    }

    public UUID getInitiator() {
        return this.players.get(0).getId();
    }

    public UUID enqueuePlayer(Player player) {
        this.players.add(player);
        if (shouldStart()) {
            this.status = SessionStatus.IN_PROGRESS;
            this.players.forEach(p -> {
                int pi = this.players.indexOf(p);
                this.players.stream().filter(o -> this.players.indexOf(o) != pi).forEach(r ->
                        this.notifySubscribers(new GameMessage<>(GameMessageType.EVENT, p.getId(), String.format("%s has joined.", r.getGivenName()))));
            });
            Thread t = new Thread(this);
            t.start();

        }
        return player.getId();
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public int getIndexOfPlayerById(UUID id) {
        for(int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public Player getPlayerByName(String name) {
       for(Player p : this.players) {
           if(p.getGivenName().equals(name)) {
               return p;
           }
        }
        return null;
    }

    public Player getPlayerById(UUID id) {
       for(Player p : this.players) {
           if(p.getId().equals(id)) {
               return p;
           }
        }
        return null;
    }

    public Set<SessionListener> getSubscribers() {
        return this.subscribers;
    }

    public GameConfiguration getConfig() {
        return this.config;
    }

    public void notifySubscribers(GameMessage msg) {
       this.subscribers.forEach(x -> x.notifySubscriber(this, msg));
    }

    public abstract void handleMessage(GameMessage msg);

    public abstract boolean shouldStart();
}

