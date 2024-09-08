import javax.crypto.Cipher;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Scanner;

public class AliceMain {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        
        //First generate a public/private key pair
        var aliceKeyPair = KeyGenerator.generateKeyPair();

        int option = 0;
        System.out.println("\n\n\n\n\n");

        while (option != 4) {
            // Exibe o menu
            System.out.println("------ MENU do Alice ------");
            System.out.println("1. Enviar chave pública para Bob");
            System.out.println("2. Verificar a assinatura da mensagem de Bob");
            System.out.println("3. Ler mensagem recebida de Bob");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");

            // Lê a escolha do usuário
            option = scanner.nextInt();

            // Executa a ação com base na escolha
            switch (option) {
                case 1:
                    System.out.println("\nVocê escolheu a Opção 1 - Enviar chave pública para Bob");        
                    sendPublicKeyToBob(aliceKeyPair.getPublic());
                    
                    break;
                    
                case 2:
                    System.out.println("\nVocê escolheu a Opção 2 -Verificar a assinatura da mensagem de Bob");
                    PublicKey bobPublicKey = Serializer.getSerializedObject("alice/aliceFriendPublicKey.ser");

                    Signature publicSignature = Signature.getInstance("SHA256withRSA");
                    publicSignature.initVerify(bobPublicKey);

                    Message message1 = getMessage();
                    
                    Cipher decryptCipher = Cipher.getInstance("RSA");
                    decryptCipher.init(Cipher.DECRYPT_MODE, aliceKeyPair.getPrivate());
                    String decipheredMessage = new String(decryptCipher.doFinal(message1.cipherText), StandardCharsets.UTF_8);
                    

                    if(decipheredMessage != null){
                        publicSignature.update(decipheredMessage.getBytes(StandardCharsets.UTF_8));
                        boolean isCorrect = publicSignature.verify(message1.signature);
                        
                        if(isCorrect){
                            System.out.println("Assinatura da mensagem é válida.");
                        }else{
                            System.out.println("Assinatura de mensagem é inválida.");
                        }
                    } 
            
                    break;

                case 3:
                    System.out.println("\nVocê escolheu a Opção 3 - Ler mensagem recebida de Bob\n");
                    Message message2 = getMessage();
      


                    Cipher decryptCipher2 = Cipher.getInstance("RSA");
                    decryptCipher2.init(Cipher.DECRYPT_MODE, aliceKeyPair.getPrivate());
                    String decipheredMessage2 = new String(decryptCipher2.doFinal(message2.cipherText), StandardCharsets.UTF_8);
            
                    System.out.println("Mensagem limpa: " +  decipheredMessage2);
                    System.out.println("Texto Cifrado: " + new String(message2.cipherText));
                    System.out.println("Assintura: " + new String(message2.signature));

                    break;

                case 4:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida, tente novamente.");
            }

            System.out.println("\n\n\n\n\n");
        }

        // Fecha o scanner
        scanner.close();
    }

    public static void sendPublicKeyToBob(PublicKey publicKey){
        Serializer.saveObject("bob/bobFriendPublicKey.ser", publicKey);
    }

    public static Message getMessage() throws Exception{
        Message message = Serializer.getSerializedObject("alice/aliceMessages.ser");
        return message;
    }
}

