package core.managers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import core.connection.ConnectionManager;
import core.connection.Message;
import core.database.MongoConnection;
import core.models.account.Account;
import core.models.payload.Login;
import core.utils.Utils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.java_websocket.WebSocket;

import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static core.connection.ConnectionManager.getSecretKey;

public class AccountManager {
    public static void handle(Message message, WebSocket socket) {
        switch (message.getHandler()) {
            case "register":
                register(message, socket);
                break;
            case "retrieve":
                retrieve(message, socket);
                break;
            case "login":
                login(message, socket);
                break;
            case "logout":
                logout(message, socket);
                break;
        }
    }

    public static void login(Message message, WebSocket socket) {
        Login loginPayload = Utils.getGson().fromJson(message.getPayload(), Login.class);

        if (Objects.nonNull(loginPayload)) {
            Account account = findAccount(loginPayload.getEmail());

            if (Objects.nonNull(account)) {
                if (BCrypt.verifyer().verify(loginPayload.getPassword().toCharArray(), account.getPassword().toCharArray()).verified) {
                    ConnectionManager.getClientConnection(socket).setAccountId(account.getId());

                    message.setType("account:retrieve");
                    message.setPayload(Utils.getGson().toJson(account));
                    message.sendMessage(socket);

                    message.setType("account:login_success");
                    message.setPayload("{}");
                    message.sendMessage(socket);
                } else {
                    message.setType("account:login_failed");
                    message.setPayload("{" + "}");
                    message.sendMessage(socket);
                }
            } else {
                message.setType("account:login_failed");
                message.setPayload("{" + "}");
                message.sendMessage(socket);
            }
        } else {
            message.setType("account:login_failed");
            message.setPayload("{" + "}");
            message.sendMessage(socket);
        }
    }

    public static void logout(Message message, WebSocket socket) {
        ConnectionManager.getClientConnection(socket).setAccountId("");

        message.setType("account:logout_success");
        message.setPayload("{" + "}");
        message.sendMessage(socket);
    }

    public static void register(Message message, WebSocket socket) {
        Account account = Utils.getGson().fromJson(message.getPayload(), Account.class);

        if (Objects.nonNull(account)) {
            if (!emailExists(account.getEmail())) {
                String uniqueId = new ObjectId().toString();

                account.setPassword(BCrypt.withDefaults().hashToString(12, account.getPassword().toCharArray()));
                account.setPasswordre(account.getPassword());
                account.setId(uniqueId);

                Document accountDocument = new Document("_id", uniqueId);
                accountDocument.put("data", Utils.getGson().toJson(account));
                MongoConnection.getDatabase().getCollection("accounts").insertOne(accountDocument);

                message.setType("account:register_success");
                message.setPayload("{" + "}");
                message.sendMessage(socket);
            } else {
                message.setType("account:register_failed");
                message.setPayload("{" + "}");
                message.sendMessage(socket);
            }
        }
    }

    public static void retrieve(Message message, WebSocket socket) {
        Account account = null;
        Document accountDocument = MongoConnection.getDatabase().getCollection("accounts")
                .find(eq("_id", ConnectionManager.getClientConnection(socket).getAccountId())).first();

        if (accountDocument != null) {
            account = Utils.getGson().fromJson(accountDocument.getString("data"), Account.class);
        }

        message.setType("account:retrieve");
        message.setPayload(Utils.getGson().toJson(account));
        message.sendMessage(socket);
    }

    private static boolean emailExists(String email) {
        for (Document d : MongoConnection.getDatabase().getCollection("accounts").find()) {
            if (Objects.nonNull(d)) {
                if (d.getString("data").contains(email)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static Account findAccount(String email) {
        Account account = null;

        for (Document d : MongoConnection.getDatabase().getCollection("accounts").find()) {
            if (Objects.nonNull(d)) {
                if (d.getString("data").contains(email)) {
                    account = Utils.getGson().fromJson(d.getString("data"), Account.class);
                }
            }
        }

        return account;
    }
}
