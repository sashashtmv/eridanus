package com.week1.game.Networking.Messages;

import com.week1.game.Model.GameState;

public class TestMessage extends GameMessage {
    private final static MessageType MESSAGE_TYPE = MessageType.TEST;

    private int coolValue;
    private String wow;

    public TestMessage(int coolValue, String wow, int playerID) {
        super(playerID, MESSAGE_TYPE);
        this.coolValue = coolValue;
        this.wow = wow;
    }

    @Override
    public boolean process(GameState inputState){
        return true;
    }
    
    @Override
    public String toString() {
        return "TestMessage: " + coolValue + ", " + wow + ", " + playerID;
    }
}
