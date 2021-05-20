package sample;

public class KeysUtils {

    private String[] keysF = new String[4];

    /*
        keysF[0] = encrypt key
        keysF[1] = rsa private key
        keysF[2] = mac
        keysF[3] = iv
    */


    public KeysUtils(String encrypt, String privateKey, String mac, String iv) {
        keysF[0]= encrypt;
        keysF[1]= privateKey;
        keysF[2]= mac;
        keysF[3]= iv;
    }

    public void setKeys(String encrypt, String privateKey, String mac, String iv){
        keysF[0]= encrypt;
        keysF[1]= privateKey;
        keysF[2]= mac;
        keysF[3]= iv;
    }

    public String[] getKeysF() {
        return keysF;
    }
}
