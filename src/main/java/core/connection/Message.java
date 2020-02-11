package core.connection;

public class Message {
    private String type, payload;

    public Message(String type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getModule() {
        return type.split(":")[0];
    }

    public String getHandler() {
        return type.split(":")[1];
    }
}
