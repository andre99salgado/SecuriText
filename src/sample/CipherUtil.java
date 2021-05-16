package sample;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

//TODO: remover signature exceptions
//TODO: Bibliografia/Credits https://www.baeldung.com/java-aes-encryption-decryption
public class CipherUtil {

    private static byte[] ivBytes;
    //TODO: remover ou deixar estar,porque IV pode ser público
    private static IvParameterSpec iv = null;
    private static final String algorithm = "AES/CTR/NoPadding";

    private String input;
    private SecretKey key;

    // Usado quando se está a criar um ficheiro pela primeira vez
    // A key é gerada pelo programa
    public CipherUtil(String input) {
        this.input = input;
        try {
            this.key = generateKey(128);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        iv = generateIv();
        ivBytes = iv.getIV();
    }

    // Usado quando se abre um ficheiro e a key do ficheiro
    public CipherUtil(String input, String key) {
        this.input = input;
        this.key = StringToSecretKey(Base64.getDecoder().decode(key));
        iv = new IvParameterSpec(ivBytes);
    }

    //Devolve chave como string (para gravar)
    public String getKeyAsString() {
        return SecretKeyToString(this.key);
    }

    public void setInput(String input) {
        this.input = input;
    }

    //Devolve string cifrada
    public String getEncryptedString() {
        return encrypt(algorithm, this.input, this.key);
    }

    //Devolve string decifrada
    public String getDecryptedString() {
        return decrypt(algorithm, this.input, this.key);
    }

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        return keyGenerator.generateKey();
    }

    //TODO: Remover caso IV seja removido
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }


    public static String encrypt(String algorithm, String input, SecretKey key) {


        byte[] cipherText = null;
        try {
            Cipher cipher = null;
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key) {

        byte[] plainText = null;
        try {

            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            plainText = cipher.doFinal(Base64.getDecoder()
                    .decode(cipherText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(plainText);
    }

    public static String SecretKeyToString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static SecretKey StringToSecretKey(byte[] decodedKey) {
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}