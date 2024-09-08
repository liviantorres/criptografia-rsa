import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Scanner;

public class EvaMain {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        KeyPair keyPair = null;
        String plainMessage = "";
        byte[] signature = null;

        int selectedOption = 0;
        System.out.println("\n\n");

        while (selectedOption != 4) {
            // Exibe o menu
            System.out.println("------ MENU da Eva ------");
            System.out.println("1. Gerar par de chaves");
            System.out.println("2. Gerar mensagem");
            System.out.println("3. Enviar mensagem para Alice se passando por Bob");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");

            // Lê a escolha do usuário
            selectedOption = scanner.nextInt();
            scanner.nextLine();  

            // Executa a ação com base na escolha
            switch (selectedOption) {
                case 1:
                    System.out.println("\nVocê escolheu a Opção 1 - Gerar par de chaves");
                    keyPair = generateNewKeyPair(); 
                
                    break;

                case 2:
                    System.out.println("\nVocê escolheu a Opção 2 - Gerar mensagem");     
                    System.out.print("Digite a mensagem: ");
                    plainMessage = scanner.nextLine();  

                    break;

                case 3:
                    System.out.println("\nVocê escolheu a Opção 3 - Enviar mensagem para Alice se passando por Bob");
                    signature = signMessage(plainMessage, keyPair.getPrivate());
                    sendSignedMessageToAlice(plainMessage, signature);
                    break;

                case 4:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida, tente novamente.");
            }

            System.out.println("\n\n\n\n\n");
        }

        scanner.close();
    }

    // Gera um novo par de chaves RSA
    public static KeyPair generateNewKeyPair() throws Exception {
        KeyPair pair = KeyGenerator.generateKeyPair();
        return pair;
    }

    public static void sendPublicKeyToAlice(PublicKey publicKey) {
        Serializer.saveObject("alice/bobPublicKey.ser", publicKey);
        System.out.println("Chave pública enviada para Alice.");
    }

    public static byte[] signMessage(String plainMessage, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainMessage.getBytes(StandardCharsets.UTF_8));
        byte[] signature = privateSignature.sign();
        
        System.out.println("Mensagem assinada.");
        return signature;
    }

    public static byte[] encryptMessageToSendToAlice(String plainMessage, PublicKey alicePublicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, alicePublicKey);
        return encryptCipher.doFinal(plainMessage.getBytes());
    }

    public static void sendSignedMessageToAlice(String plainMessage, byte[] signature) throws Exception{
        if (signature != null) {
            System.out.println("Enviando mensagem assinada para Alice\n");
            PublicKey alicePublicKey = Serializer.getSerializedObject("bob/alicePublicKey.ser");
            byte[] cipherText = encryptMessageToSendToAlice(plainMessage, alicePublicKey);
            
            System.out.println("Texto claro: " + plainMessage);
            System.out.println("Texto Cifrado: " + new String(cipherText));
            System.out.println("Assinatura: " + new String(signature));

            Message message = new Message();
            message.cipherText = cipherText;
            message.signature = signature;     

            Serializer.saveObject("alice/aliceMessages.ser", message); 
        } else {
            System.out.println("Mensagem não assinada, assine a mensagem antes de enviá-la");
        }
    }
}
