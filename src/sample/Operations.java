package sample;

public class Operations {

    public static OperationType chooseOperation(boolean hasEncryptKey, boolean hasPrivateKey, boolean hasHmac, boolean hasIv, boolean hasPublicKey, boolean hasSignature){
        System.out.println(hasEncryptKey);
        System.out.println(hasPrivateKey);
        System.out.println(hasIv);
        System.out.println(hasHmac);
        if (hasEncryptKey && hasPrivateKey && hasIv && hasHmac) return OperationType.ENCRYPT_HMAC;
        if (hasEncryptKey && hasPublicKey && hasIv ) return OperationType.ENCRYPT_SIGN;
        if (hasPublicKey && hasSignature) return OperationType.SIGN;
        if (hasHmac && hasPrivateKey) return OperationType.HMAC;
        if (hasEncryptKey && hasIv) return  OperationType.ENCRYPT;
        return OperationType.NOTHING;
    }







}

enum OperationType{

    ENCRYPT,
    HMAC,
    SIGN,
    ENCRYPT_SIGN,
    ENCRYPT_HMAC,
    NOTHING

}
