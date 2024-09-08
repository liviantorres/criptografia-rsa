import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KeyGenerator {
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException{
        long start = System.currentTimeMillis();
        System.out.println("Ininciando geração de par de chaves");

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        long end = System.currentTimeMillis();
        System.out.println("Novo par de chaves gerado com sucesso.");
        System.out.println("Geração de par de chaves finalizada em " + (end - start) + "ms");

        return pair;
    }
}
