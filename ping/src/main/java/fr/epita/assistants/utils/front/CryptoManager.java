package fr.epita.assistants.utils.front;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


/**
 * This is the crypto class which we use to simplify the process of password and save encryption
 *
 * @author remy.decourcelle@epita.fr
 * @version 1.0
 */
public class CryptoManager {

    public static byte[] encryptAES(String plaintext, String secretKey) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(plaintext.getBytes());
    }

    public static String decryptAES(byte[] ciphertext, String secretKey) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(ciphertext);
        return new String(decryptedBytes);
    }

    public static String generateRandomKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // Spécifie la taille de clé (128, 192 ou 256 bits)
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded()).substring(0, 32);
    }

    public static void main(String[] args) throws Exception {
        String plaintext = "Hello, world!";
        String secretKey = generateRandomKey(); // Génère une clé aléatoire

        byte[] encryptedText = encryptAES(plaintext, secretKey);
        String cipheredText = Base64.getEncoder().encodeToString(encryptedText);
        System.out.println("Texte chiffré : " + cipheredText);

        String decryptedText = decryptAES(encryptedText, secretKey);
        System.out.println("Texte déchiffré : " + decryptedText);
    }

    public static byte[] readByte(String path)
    {
        try {
            FileInputStream fis = new FileInputStream(path);
            // Obtient la taille du fichier
            long fileSize = fis.available();
            // Crée un tableau de bytes de la taille du fichier
            byte[] data = new byte[(int) fileSize];
            // Lit le contenu du fichier dans le tableau de bytes
            fis.read(data);
            fis.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static void writeByte(byte[] toWrite, String path)
    {
        try {
            FileOutputStream fos = new FileOutputStream(path, false);
            fos.write(toWrite); // Écrit les bytes dans le fichier
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void encryptFile(String path)
    {
        Path javaFile = Paths.get(path);

        // Obtenir le nom de fichier sans extension
        String fileNameWithoutExtension = javaFile.getFileName().toString();
        int dotIndex = fileNameWithoutExtension.lastIndexOf(".");
        String baseFileName = fileNameWithoutExtension.substring(0, dotIndex);

        // Construire un nouveau Path avec l'extension modifiée
        String newFileName = baseFileName + ".vault";
        Path cryptoFile = javaFile.resolveSibling(newFileName);

        String plaintext = FileManager.readFileContent(path);
        try {
            byte[] encryptedText = encryptAES(plaintext, PasswordManager.getKey());
            writeByte(encryptedText, cryptoFile.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
