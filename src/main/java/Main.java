import core.connection.Server;
import core.database.MongoConnection;

public class Main {
    public static void main(String[] args) {
        MongoConnection.init();

        Server server = new Server(42069);
        server.start();
    }
}
