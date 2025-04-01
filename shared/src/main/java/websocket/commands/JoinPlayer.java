package websocket.commands;

public class JoinPlayer extends UserGameCommand {

    private final String color; // You might use an enum instead of String

    public JoinPlayer(String authToken, Integer gameID, String color) {
        super(CommandType.JOIN_PLAYER, authToken, gameID);
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
