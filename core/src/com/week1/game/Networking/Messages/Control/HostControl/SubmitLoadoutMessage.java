package com.week1.game.Networking.Messages.Control.HostControl;

import com.week1.game.Model.TowerLite;
import com.week1.game.Networking.Messages.Control.ClientControl.ReadyToStartMessage;
import com.week1.game.Networking.Messages.MessageFormatter;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Host;
import com.week1.game.Networking.NetworkObjects.Player;

import java.net.InetAddress;
import java.util.List;

/**
 * This is the message that sends a players chosen loadout to the host.
 */
public class SubmitLoadoutMessage extends HostControlMessage {

    private final static MessageType MESSAGE_TYPE = MessageType.SUBMITLOADOUT;

    private List<TowerLite> details;

    public SubmitLoadoutMessage(int playerID, List<TowerLite> details){
        super(playerID, MESSAGE_TYPE);
        this.details = details;
    }

    @Override
    public void updateHost(Host h, InetAddress addr, int port) {
//        Gdx.app.debug("pjb3 -  SendLoadoutMessage", "Adding the tower details for the player " + playerID);
        h.towerDetails.put(playerID, details);

        // See if there are any more towerDetails missing. If there are, nor more action needed.
        // TODO maybe make a message that does out everytime someone sends it in if we have a nice UI.
        if (h.registry.size() != h.towerDetails.size()) {
            return;
        }

        // Tell all clients that all players are ready to go if they want to do any rendering showing people are ready
        for (Player p : h.registry.values()) {
            h.sendMessage(MessageFormatter.packageMessage(new ReadyToStartMessage(-1)), p);
        }
    }

    @Override
    public String toString() {
        return "SendChosenTowersMessage.";
    }
}
