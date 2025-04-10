package websocket.commands;

public class Connect extends UserGameCommand {

    private final String color; // You might use an enum instead of String

    public Connect(String authToken, Integer gameID, String color) {
        super(CommandType.CONNECT, authToken, gameID);
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
