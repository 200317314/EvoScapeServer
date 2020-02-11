package core.connection;

import org.java_websocket.WebSocket;

import javax.crypto.SecretKey;
import java.security.PrivateKey;

public class ClientConnection {
    private WebSocket webSocket;
    private PrivateKey privateKey;
    private SecretKey secretKey;

    public ClientConnection(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}
