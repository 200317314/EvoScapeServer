package core.managers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import core.connection.ConnectionManager;
import core.connection.Message;
import core.database.MongoConnection;
import core.models.account.Account;
import core.models.account.Address;
import core.models.payload.Login;
import core.models.payload.account.Addresses;
import core.models.payload.account.Details;
import core.models.payload.account.Password;
import core.utils.Utils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.java_websocket.WebSocket;

import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

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
            case "updatePassword":
                updatePassword(message, socket);
                break;
            case "updateDetails":
                updateDetails(message, socket);
                break;
            case "addAddress":
                addAddress(message, socket);
                break;
            case "updateCurrentAddress":
                updateCurrentAddress(message, socket);
                break;

        }
    }

    public static void login(Message message, WebSocket socket) {
        Login loginPayload = Utils.getGson().fromJson(message.getPayload(), Login.class);

        if (Objects.nonNull(loginPayload)) {
            Account account = findAccount(loginPayload.getEmail().toLowerCase());

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
        account.setEmail(account.getEmail().toLowerCase());

        if (Objects.nonNull(account)) {
            if (!emailExists(account.getEmail().toLowerCase())) {
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

    private static void updatePassword(Message message, WebSocket socket) {
        Account account = findAccountById(ConnectionManager.getClientConnection(socket).getAccountId());
        Password password = Utils.getGson().fromJson(message.getPayload(), Password.class);

        if (BCrypt.verifyer().verify(password.getCurrentPassword().toCharArray(), account.getPassword().toCharArray()).verified) {
            account.setPassword(BCrypt.withDefaults().hashToString(12, password.getNewPassword().toCharArray()));
            account.saveAccount();

            message.setType("account:updatePassword_success");
        } else {
            message.setType("account:updatePassword_failed");
        }

        message.setPayload("{}");
        message.sendMessage(socket);
    }

    private static void updateDetails(Message message, WebSocket socket) {
        Account account = findAccountById(ConnectionManager.getClientConnection(socket).getAccountId());
        Details details = Utils.getGson().fromJson(message.getPayload(), Details.class);

        if (Objects.nonNull(account)) {
            if (!account.getEmail().toLowerCase().equals(details.getEmail().toLowerCase())) {
                if (!emailExists(details.getEmail()))
                    account.setEmail(details.getEmail());
            }

            account.setName(details.getName());
            account.setPhone(details.getPhone());
            account.saveAccount();

            message.setType("account:updateDetails_success");
        } else {
            message.setType("account:updateDetails_failed");
        }

        message.setPayload("{}");
        message.sendMessage(socket);
    }

    private static void addAddress(Message message, WebSocket socket) {
        Account account = findAccountById(ConnectionManager.getClientConnection(socket).getAccountId());
        Address newAddress = Utils.getGson().fromJson(message.getPayload(), Address.class);

        if (Objects.nonNull(account)) {
            if (Objects.nonNull(account.getCurrentAddress())) {
                account.getPreviousAddresses().add(newAddress);
            } else {
                account.setCurrentAddress(newAddress);
            }
            message.setType("account:addAddress_success");
        } else {
            message.setType("account:addAddress_failed");
        }

        message.setPayload("{}");
        message.sendMessage(socket);
    }

    private static void updateCurrentAddress(Message message, WebSocket socket) {
        Account account = findAccountById(ConnectionManager.getClientConnection(socket).getAccountId());
        Addresses addresses = Utils.getGson().fromJson(message.getPayload(), Addresses.class);

        if (Objects.nonNull(account)) {
            account.setCurrentAddress(addresses.getPreviousAddress());
            for (int i = 0; i < account.getPreviousAddresses().size(); i++) {
                if (account.getPreviousAddresses().get(i).equals(addresses.getPreviousAddress()))
                    account.getPreviousAddresses().add(i, addresses.getCurrentAddress());
            }

            message.setType("account:updateCurrentAddress_success");
        } else {
            message.setType("account:updateCurrentAddress_failed");
        }
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

    private static Account findAccountById(String id) {
        Account account = null;

        for(Document d : MongoConnection.getDatabase().getCollection("accounts").find()) {
            if (Objects.nonNull(d)) {
                if (d.getString("_id").contains(id)) {
                    account = Utils.getGson().fromJson(d.getString("data"), Account.class);
                }
            }
        }

        return account;
    }
}
