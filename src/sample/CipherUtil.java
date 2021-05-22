package sample;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class CipherUtil {

    // Cifra, Modo de Cifra e Padding Escolhido
    private static final String algorithm = "AES/CBC/PKCS5Padding";


    // Armazena o IvParameterSpec do Objeto
    private IvParameterSpec iv = null;

    // Armazena os bytes do IV do Objeto
    private byte[] ivBytes;

    // Usado para armazenar o texto limpo ou o criptograma
    private String input;

    // Armazena a Chave gerada ou lida de um ficheiro
    private SecretKey key;

    // Usado quando se está a criar um ficheiro pela primeira vez, em que ainda não existe
    // Chave nem IV
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

    // Usado quando se abre um ficheiro e a key e IV correspondente a esse ficheiro
    public CipherUtil(String input, String key, byte[] ivBytesInput) {
        this.input = input;
        this.key = StringToSecretKey(Base64.getDecoder().decode(key));
        this.ivBytes = ivBytesInput;
        iv = new IvParameterSpec(ivBytesInput);
    }

    public byte[] getIvBytes() {
        return ivBytes;
    }

    // Transforma os bytes em Base64 para possívelmente ser armazenado
    public String getIvBytesAsString() {
        return Base64.getEncoder().encodeToString(ivBytes);
    }

    // Transforma os IV em Base64 em Bytes para ser utilizado pela classe (após a leitura deste do ficheiro)
    public static byte[] getStringAsIv(String s) {
        return Base64.getDecoder().decode(s);
    }

    //Devolve chave como string (para ser escrita para o ficheiro)
    public String getKeyAsString() {
        return SecretKeyToString(this.key);
    }

    // Atualiza o input atual
    // Usado quando o utilizador abriu um ficheiro e pretende gravar com a mesma chave/IV
    public void setInput(String input) {
        this.input = input;
    }

    //Devolve string cifrada
    public String getEncryptedString() {
        return encrypt(algorithm, this.input, this.key, this.iv);
    }

    //Devolve string decifrada
    public String getDecryptedString() {
        return decrypt(algorithm, this.input, this.key, this.iv);
    }

    // Gera uma chave aleatória
    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        return keyGenerator.generateKey();
    }

    // Gera um IV aleatório
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /*
        Encripta texto
        algorithm -> algortimo utilizado para a cifra
        input -> texto de entrada a ser encriptado
        key -> chave usada para encriptar
        iv -> IV usado para este modo de encriptação

        devolve criptograma no formato de Base64
     */
    public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv) {
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

    /*
        Decifra texto
        algorithm -> algortimo utilizado para a cifra
        input -> texto de entrada a ser encriptado
        key -> chave usada para encriptar
        iv -> IV usado para este modo de encriptação

        devolve string
     */
    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv) {

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

    // transforma a chave em string para ser possível guardar
    public static String SecretKeyToString(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    // transforma a string em chave para ser utilizado pelo objeto
    public static SecretKey StringToSecretKey(byte[] decodedKey) {
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}