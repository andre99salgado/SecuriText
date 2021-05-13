package sample;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class AuthenticateUtils {

    private String input;
    private KeyPair keyPair;
    private static final String HMAC_SHA512 = "HmacSHA512";


    private String privateKey;
    private String publicKey;
    private String hmac;

    public AuthenticateUtils(String input) {
        this.input = input;
        keyPair = getKeyPair();
        privateKey = PrivateKeyToString(keyPair.getPrivate());
        publicKey = PublicKeyToString(keyPair.getPublic());
    }

    
    // Usado quando se abre um ficheiro e a key do ficheiro
    public AuthenticateUtils(String input, String key, String hmac) {
        this.input = input;
        this.privateKey = key;
        this.hmac= hmac;
    }
    

    public String getSignedText() {
        try {
            return sign(this.input, keyPair.getPrivate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] signature = privateSignature.sign();

        return Base64.getEncoder().encodeToString(signature);
    }

    public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }
    
///////////////   https://stackoverflow.com/questions/39355241/compute-hmac-sha512-with-secret-key-in-java

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    
    ////// usamos a publica ou a privada ? - verificar o algoritmo
    
    public String calculateHMAC(String data) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
       
        String key = getPrivateKey();
        //String key = getPublicKey(); -------------------------------_> VERIFICAR SE NÃO É COM A PUBLICA
        //System.out.println("\n é public key" + key);
        // System.out.println("\n Esta é a private key 1: " + getPrivateKey());
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(secretKeySpec);
        return toHexString(mac.doFinal(data.getBytes()));
    }
 
    
    public String calculateToVerifyHMAC(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        //String key = getPrivateKey();
        //System.out.println("\n é public key" + key);
        // System.out.println("\n Esta é a private key 1: " + getPrivateKey());
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA512);
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(secretKeySpec);
        return toHexString(mac.doFinal(data.getBytes()));
    }
    
    //// https://community.shopify.com/c/Shopify-APIs-SDKs/Java-HMAC-authentication-verification/td-p/498131
    
     public boolean verifyHmac(String message, String hmac, String secretKey) {
        try {
            
            String hmac1 = calculateToVerifyHMAC(message, secretKey);
            System.out.println("HMAC1 calculado segundo:" + hmac1);
            System.out.println("HMAC1 calculado primeiro:" + hmac);
            System.out.println("VERDADE OU MENTIRA:" + hmac.equals(hmac1));
            return hmac.equals(hmac1);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            System.out.println("Error verifying hmac" + ex);
            throw new IllegalArgumentException(ex);
        } catch (SignatureException ex) {
            Logger.getLogger(AuthenticateUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
     }
//////////////
    

    private static KeyPair getKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            return kpg.genKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getHmac() {
        return hmac;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }

   
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    
    public static String PrivateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

     public static String PublicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    
}