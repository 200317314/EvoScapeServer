package core.connection;


import core.managers.AccountManager;
import core.utils.Utils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public class Server extends WebSocketServer {
    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        ClientConnection clientConnection = new ClientConnection(webSocket);

        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        clientConnection.setPrivateKey(pair.getPrivate());

        ConnectionManager.addConnection(clientConnection);

        Message message = new Message("connection:public_key", "{" + "publicKey:'" + Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()) + "'}");
        webSocket.send(Utils.getGson().toJson(message));
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        removeConnection(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("Message: " + s);
        Message message = null;
        EncodedMessage encodedMessage = Utils.getGson().fromJson(s, EncodedMessage.class);

        if (Objects.nonNull(encodedMessage)) {
            if (Objects.nonNull(ConnectionManager.getSecretKey(webSocket))) {
                System.out.println("Decrypting AES");
                message = Utils.getGson().fromJson(Utils.decryptAES(encodedMessage.getEncoded(), ConnectionManager.getSecretKey(webSocket)), Message.class);
            } else {
                System.out.println("Decrypting RSA");
                message = Utils.getGson().fromJson(Utils.decryptRSA(encodedMessage.getEncoded(), ConnectionManager.getPrivateKey(webSocket)), Message.class);
            }
        }

        if (Objects.nonNull(message)) {
            System.out.println("Decrypted message: " + message.getModule() + "|" + message.getType() + " : " + message.getPayload());
            switch (message.getModule()) {
                case "connection":
                    ConnectionManager.handle(message, webSocket);
                    break;
                case "account":
                    AccountManager.handle(message, webSocket);
                    break;
            }
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server is running...");
        setConnectionLostTimeout(10);
    }
}
