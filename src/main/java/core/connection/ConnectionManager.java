package core.connection;

import core.utils.Utils;
import org.java_websocket.WebSocket;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConnectionManager {
    private static List<ClientConnection> connections = new ArrayList<>();

    public static List<ClientConnection> getConnections() {
        return connections;
    }

    public static void addConnection(ClientConnection clientConnection) {
        connections.add(clientConnection);
    }

    public static PrivateKey getPrivateKey(WebSocket webSocket) {
        return connections.stream().filter(s -> s.getWebSocket() == webSocket).findFirst().get().getPrivateKey();
    }

    public static SecretKey getSecretKey(WebSocket webSocket) {
        return connections.stream().filter(s -> s.getWebSocket() == webSocket).findFirst().get().getSecretKey();
    }

    public static ClientConnection getClientConnection(WebSocket webSocket) {
        return connections.stream().filter(s -> s.getWebSocket() == webSocket).findFirst().get();
    }

    public static void removeConnection(WebSocket webSocket) {
        connections.remove(getClientConnection(webSocket));
    }

    public static void setSecretKey(Message message, WebSocket webSocket) {
        SecretKey secretKey = new SecretKeySpec(Utils.getGson().fromJson(message.getPayload(), byte[].class), "AES");

        if (Objects.nonNull(secretKey)) {
            getClientConnection(webSocket).setSecretKey(secretKey);
            Message msg = new Message("connection:secret_key_success", "{}");

            webSocket.send("{" + "encoded:" + Utils.getGson().toJson(Utils.encryptAES(Utils.getGson().toJson(msg), getSecretKey(webSocket))) + "}");
        }
    }

    public static void handle(Message message, WebSocket webSocket) {
        switch (message.getHandler()) {
            case "secret_key":
                setSecretKey(message, webSocket);
                break;
        }
    }
}
