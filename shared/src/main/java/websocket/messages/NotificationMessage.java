package websocket.messages;

import model.GameData;

public class NotificationMessage extends ServerMessage {
    public String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
