import javax.crypto.Cipher;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Scanner;

public class AliceMain {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        KeyPair keyPair = null;
        String plainMessage = "";

        int option = 0;
        System.out.println("\n\n");

        while (option != 7) {
            // Exibe o menu
            System.out.println("------ MENU da Alice ------");
            System.out.println("1. Gerar novo par de chaves");
            System.out.println("2. Enviar chave pública para Bob");
            System.out.println("3. Verificar a assinatura da mensagem de Bob");
            System.out.println("4. Ler mensagem recebida de Bob");
            System.out.println("5. Gerar uma mensagem");
            System.out.println("6. Enviar a mensagem cifrada para Bob.");
            System.out.println("7. Sair");
            System.out.print("Escolha uma opção: ");

            // Lê a escolha do usuário
            option = scanner.nextInt();
            scanner.nextLine(); 

            // Executa a ação com base na escolha
            switch (option) {
                case 1:
                    System.out.println("\nVocê escolheu a Opção 1 - Gerar novo par de chaves");
                    keyPair = KeyGenerator.generateKeyPair(); // Gera novas chaves
                    break;

                case 2:
                    System.out.println("\nVocê escolheu a Opção 2 - Enviar chave pública para Bob");        

                    if (keyPair != null) {
                        sendPublicKeyToBob(keyPair.getPublic());
                    } else {
                        System.out.println("Para enviar a chave pública para Bob você precisará gerar um par de chaves");
                    }
                    break;

                case 3:
                    System.out.println("\nVocê escolheu a Opção 3 - Verificar a assinatura da mensagem de Bob");

                    if (keyPair != null) {
                        verifyBobSignatureInMessage(keyPair.getPrivate());
                    } else {
                        System.out.println("Para verificar a assinatura da mensagem de Bob você precisará gerar um par de chaves");
                    }
                    break;

                case 4:
                    System.out.println("\nVocê escolheu a Opção 4 - Ler mensagem recebida de Bob\n");

                    if (keyPair != null) {
                        readBobMessage(keyPair.getPrivate());
                    } else {
                        System.out.println("Para ler a mensagem recebida de Bob você precisará gerar um par de chaves");
                    }
                    break;

                case 5:
                    System.out.println("\nVocê escolheu a Opção 5 - Gerar uma mensagem");
                    System.out.print("Digite a mensagem: ");
                    plainMessage = scanner.nextLine();  // Armazena a mensagem digitada
                    break;

                case 6:
                    System.out.println("\nVocê escolheu a Opção 6 - Enviar a mensagem cifrada para Bob");

                    if (keyPair != null && !plainMessage.isEmpty()) {
                        PublicKey bobPublicKey = Serializer.getSerializedObject("alice/bobPublicKey.ser");
                        sendEncryptedMessageToBob(plainMessage, bobPublicKey);  // Envia a mensagem cifrada para Bob
                    } else {
                        System.out.println("Você precisa gerar um par de chaves e uma mensagem antes de enviar.");
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

    // Enviar a chave pública para Bob
    public static void sendPublicKeyToBob(PublicKey publicKey) {
        Serializer.saveObject("bob/alicePublicKey.ser", publicKey);
    }

   public static void verifyBobSignatureInMessage(PrivateKey alicePrivateKey) throws Exception{
        PublicKey bobPublicKey = Serializer.getSerializedObject("alice/bobPublicKey.ser");
        Message message = getMessage();
        
        long start = System.currentTimeMillis();
        System.out.println("Verificando a assinatura da mensagem...");

        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, alicePrivateKey);
        String decipheredMessage = new String(decryptCipher.doFinal(message.cipherText), StandardCharsets.UTF_8);
    
        if(decipheredMessage != null){
            Signature publicSignature = Signature.getInstance("SHA256withRSA");
            publicSignature.initVerify(bobPublicKey);

            publicSignature.update(decipheredMessage.getBytes(StandardCharsets.UTF_8));
            boolean isCorrect = publicSignature.verify(message.signature);

            long end = System.currentTimeMillis();
            System.out.println("Assinatura da mensagem verificada em " + (end - start) + "ms");
            
            if(isCorrect){
                System.out.println("Assinatura da mensagem é válida.");
            }else{
                System.out.println("Assinatura de mensagem é inválida.");
            }
        } 


    }

    // Ler a mensagem de Bob
    public static void readBobMessage(PrivateKey alicePrivateKey) throws Exception{
        Message message = getMessage();
      
        long start = System.currentTimeMillis();
        System.out.println("Decifrando a mensagem...");
        
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, alicePrivateKey);
        String decipheredMessage = new String(decryptCipher.doFinal(message.cipherText), StandardCharsets.UTF_8);

        long end = System.currentTimeMillis();
        System.out.println("Mensagem decifrada em " + (end - start) + "ms");

        System.out.println("Mensagem cifrada: " +  new String(message.cipherText));
        System.out.println("Assinatura: " +  new String(message.signature));
        System.out.println("Texto claro: " +  decipheredMessage);
    }



    public static Message getMessage() throws Exception {
        Message message = Serializer.getSerializedObject("alice/aliceMessages.ser");
        return message;
    }

    // Cifrar e enviar a mensagem para Bob
    public static void sendEncryptedMessageToBob(String plainMessage, PublicKey bobPublicKey) throws Exception {
        if (plainMessage == null || plainMessage.isEmpty()) {
            throw new IllegalArgumentException("A mensagem não pode ser vazia.");
        }
    
        long start = System.currentTimeMillis();
        System.out.println("Cifrando e enviando a mensagem para Bob...");

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, bobPublicKey);
        byte[] encryptedMessage = encryptCipher.doFinal(plainMessage.getBytes(StandardCharsets.UTF_8));

        Serializer.saveObject("bob/aliceEncryptedMessage.ser", encryptedMessage);
    
        long end = System.currentTimeMillis();
        System.out.println("Mensagem cifrada e enviada para Bob em " + (end - start) + "ms");
    }
}
