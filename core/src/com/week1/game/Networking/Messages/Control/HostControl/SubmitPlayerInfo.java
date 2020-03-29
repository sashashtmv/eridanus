package com.week1.game.Networking.Messages.Control.HostControl;

import com.badlogic.gdx.Gdx;
import com.week1.game.Model.PlayerInfo;
import com.week1.game.Networking.Messages.MessageType;
import com.week1.game.Networking.NetworkObjects.Host;

import java.net.InetAddress;

public class SubmitPlayerInfo extends HostControlMessage {

    private final static MessageType MESSAGE_TYPE = MessageType.SUBMITPLAYERINFO;
    private PlayerInfo info;

    public SubmitPlayerInfo(PlayerInfo info) {
        super(-1, MESSAGE_TYPE);
        this.info = info;
    }

    @Override
    public void updateHost(Host h, InetAddress addr, int port) {
        h.setPlayerInfo(addr, info);
        Gdx.app.debug("pjb3", "SubmitNameColor is running on host");
    }
}
