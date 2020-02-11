package core.connection;

public class EncodedMessage {
    private byte[] encoded;

    public EncodedMessage(byte[] encoded) {
        this.encoded = encoded;
    }

    public byte[] getEncoded() {
        return encoded;
    }

    public void setEncoded(byte[] encoded) {
        this.encoded = encoded;
    }
}
