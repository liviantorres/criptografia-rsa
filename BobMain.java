import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Scanner;

public class BobMain {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        var bobKeyPair = KeyGenerator.generateKeyPair();

        System.out.println("\n\n\n\n\n");

        String plainMessage = "";
        byte[] signature = null;

        int option = 0;

        while (option != 5) {
            // Exibe o menu
            System.out.println("------ MENU do BOB ------");
            System.out.println("1. Enviar chave pública para Alice");
            System.out.println("2. Gerar mensagem");
            System.out.println("3. Assinar mensagem com chave privada");
            System.out.println("4. Enviar mensagem assinada para Alice");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");

            // Lê a escolha do usuário
            option = scanner.nextInt();
            scanner.nextLine();  

            // Executa a ação com base na escolha
            switch (option) {
                case 1:
                    System.out.println("\nVocê escolheu a Opção 1 - Enviar chave pública para Alice\n");        
                    sendPublicKeyToAlice(bobKeyPair.getPublic());
                    
                    break;

                case 2:
                    System.out.println("\nVocê escolheu a Opção 2 - Gerar mensagem");
                    System.out.print("Digite a mensagem: ");
                    plainMessage = scanner.nextLine();  
                   
                    break;

                case 3:
                    System.out.println("\nVocê escolheu a Opção 3 - Assinar mensagem com chave privada");
                    signature = signMessage(plainMessage, bobKeyPair.getPrivate());
                    System.out.println("Mensagem assinada.");
                    break;
                    
                case 4:
                    System.out.println("\nVocê escolheu a Opção 4 - Enviar mensagem assinada para Alice");

                    if (signature != null) {
                        System.out.println("Enviando mensagem assinada para Alice\n");
                        PublicKey alicePublicKey = Serializer.getSerializedObject("bob/bobFriendPublicKey.ser");
                        byte[] cipherText = encryptMessageToSendToAlice(plainMessage, alicePublicKey);
                        
                        System.out.println("Mensagem limpa: " + plainMessage);
                        System.out.println("Texto Cifrado: " + new String(cipherText));
                        System.out.println("Assinatura: " + new String(signature));

                        Message message = new Message();
                        message.cipherText = cipherText;
                        message.signature = signature;     

                        sendMessageToAlice(message);      
                    } else {
                        System.out.println("Mensagem não assinada, assine a mensagem antes de enviá-la");
                    }
                    
                    break;
                case 5:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }

            System.out.println("\n\n\n\n\n");
        }

       
        scanner.close();
    }

    public static void sendPublicKeyToAlice(PublicKey publicKey) {
        Serializer.saveObject("alice/aliceFriendPublicKey.ser", publicKey);
    }

    public static byte[] signMessage(String plainMessage, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainMessage.getBytes(StandardCharsets.UTF_8));
        return privateSignature.sign();
    }

    public static byte[] encryptMessageToSendToAlice(String plainMessage, PublicKey alicePublicKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, alicePublicKey);
        return encryptCipher.doFinal(plainMessage.getBytes());
    }

    public static void sendMessageToAlice(Message message) {
        Serializer.saveObject("alice/aliceMessages.ser", message);
    }
}
