package sample;

import java.util.Arrays;

public class KeysUtils {

    private String[] keysF = new String[6];

    /*
        keysF[0] = encrypt key
        keysF[1] = rsa private key
        keysF[2] = mac
        keysF[3] = iv
        keysF[4] = rsa public key
        keysF[5] = signature
    */


    public KeysUtils(String encrypt, String privateKey, String mac, String iv, String publicKey, String signature) {
        keysF[0] = encrypt;
        keysF[1] = privateKey;
        keysF[2] = mac;
        keysF[3] = iv;
        keysF[4] = publicKey;
        keysF[5] = signature;
        //System.out.printf("Key> " + Arrays.toString(keysF));
    }

    public KeysUtils(String encrypt, String privateKey, String mac, String iv) {
        keysF[0] = encrypt;
        keysF[1] = privateKey;
        keysF[2] = mac;
        keysF[3] = iv;
        keysF[4] = "";
        keysF[5] = "";
        //System.out.printf("Key> " + Arrays.toString(keysF));
    }
    public void setKeys(String encrypt, String privateKey, String mac, String iv, String publicKey) {
        keysF[0] = encrypt;
        keysF[1] = privateKey;
        keysF[2] = mac;
        keysF[3] = iv;
        keysF[4] = publicKey;
    }

    public String[] getKeysF() {
        return keysF;
    }
}
