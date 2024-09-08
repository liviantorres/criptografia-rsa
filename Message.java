import java.io.Serializable;

public class Message implements Serializable {
    byte[] signature;
    byte[] cipherText;
}
