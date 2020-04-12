package core.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    private static MongoClient mongoClient;

    public static void init() {
        mongoClient = MongoClients.create("mongodb://7804364:5hfo4jsb!@realmlands.com/?authSource=admin");
    }

    public static MongoDatabase getDatabase() {
        return mongoClient.getDatabase("evoscape");
    }
}
