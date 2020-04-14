package core.managers;

import com.mongodb.client.FindIterable;
import core.connection.ConnectionManager;
import core.connection.Message;
import core.database.MongoConnection;
import core.models.service.Service;
import core.utils.Utils;
import org.bson.Document;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ServiceManager {
    public static void handle(Message message, WebSocket socket) {
        switch (message.getHandler()) {
            case "retrieve":
                retrieve(message, socket);
                break;
        }
    }

    private static void retrieve(Message message, WebSocket socket) {
        message.setType("services:retrieve");
        message.setPayload(Utils.getGson().toJson(getServices()));
        message.sendMessage(socket);
    }

    private static List<Service> getServices() {
        List<Service> services = new ArrayList<>();
        FindIterable<Document> serviceDoc = MongoConnection.getDatabase().getCollection("services").find();

        serviceDoc.forEach((Consumer<? super Document>) d -> {
            if (d != null) {
                Service service = Utils.getGson().fromJson(d.toJson(), Service.class);

                if (Objects.nonNull(service)) {
                    services.add(service);
                }
            }
        });

        return services;
    }
}
