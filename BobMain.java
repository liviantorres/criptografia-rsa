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
        KeyPair keyPair = null;
        String plainMessage = "";
        byte[] signature = null;

        int selectedOption = 0;
        System.out.println("\n\n");

        while (selectedOption != 6) {
            // Exibe o menu
            System.out.println("------ MENU do BOB ------");
            System.out.println("1. Gerar par de chaves");
            System.out.println("2. Enviar chave pública para Alice");
            System.out.println("3. Gerar mensagem");
            System.out.println("4. Assinar mensagem com chave privada");
            System.out.println("5. Enviar mensagem assinada para Alice");
            System.out.println("6. Ler mensagem cifrada");
            System.out.println("7. Sair");
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
                    System.out.println("\nVocê escolheu a Opção 2 - Enviar chave pública para Alice");     
                    
                    if(keyPair == null){
                        System.out.println("Para enviar uma chave pública primeiro você precisará criar um par de chaves.");
                    } else{
                        sendPublicKeyToAlice(keyPair.getPublic());
                    }

                    break;

                case 3:
                    System.out.println("\nVocê escolheu a Opção 3 - Gerar mensagem");
                    System.out.print("Digite a mensagem: ");
                    plainMessage = scanner.nextLine();  
                    break;

                case 4:
                    System.out.println("\nVocê escolheu a Opção 4 - Assinar mensagem com chave privada");

                    if(keyPair == null){
                        System.out.println("Para assinar a mensagem com a chave privada primeiro você precisará criar um par de chaves.");
                    } else{
                        signature = signMessage(plainMessage, keyPair.getPrivate());
                    }

                    break;
                    
                case 5:
                    System.out.println("\nVocê escolheu a Opção 5 - Enviar mensagem assinada para Alice");
                    sendSignedMessageToAlice(plainMessage, signature);
                    break;
                case 6:
                        System.out.println("\nVocê escolheu a Opção 6 - Ler mensagem cifrada");

                        if (keyPair != null) {
                            readEncryptedMessageFromAliceWithoutSignature(keyPair.getPrivate());
                        } else {
                            System.out.println("Para ler a mensagem cifrada, você precisará gerar um par de chaves.");
                        }
                    break;

                case 7:
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
        long start = System.currentTimeMillis();
        System.out.println("Gerando hash de assinatura");

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainMessage.getBytes(StandardCharsets.UTF_8));
        byte[] signature = privateSignature.sign();
        
        System.out.println("Mensagem assinada.");

        long end = System.currentTimeMillis();
        System.out.println("Hash de assinatura gerado em " + (end - start) + "ms");
        return signature;
    }

    public static byte[] encryptMessageToSendToAlice(String plainMessage, PublicKey alicePublicKey) throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("Cifrando mensagem para envio");

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, alicePublicKey);
        var encryptedMessage = encryptCipher.doFinal(plainMessage.getBytes());

        long end = System.currentTimeMillis();
        System.out.println("Mensagem cifrada em " + (end - start) + "ms");
        return encryptedMessage;
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
    public static void readEncryptedMessageFromAliceWithoutSignature(PrivateKey bobPrivateKey) throws Exception {
        byte[] encryptedMessage = Serializer.getSerializedObject("bob/aliceEncryptedMessage.ser");
    
        if (encryptedMessage != null) {
            long start = System.currentTimeMillis();
            System.out.println("Decifrando a mensagem...");
    
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, bobPrivateKey);
            String decipheredMessage = new String(decryptCipher.doFinal(encryptedMessage), StandardCharsets.UTF_8);
    
            long end = System.currentTimeMillis();
            System.out.println("Mensagem decifrada em " + (end - start) + "ms");
    
            
            System.out.println("Texto claro: " + decipheredMessage);
        } else {
            System.out.println("Nenhuma mensagem cifrada foi recebida de Alice.");
        }
    }
}
