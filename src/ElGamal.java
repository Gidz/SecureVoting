import java.security.*;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ElGamal{

    static byte[] encryptVote(java.lang.String plainText,Key publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] input = plainText.getBytes();

        Cipher cipher = Cipher.getInstance("ELGamal","BC");
        SecureRandom random = new SecureRandom();
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, random);
        byte[] cipherText = cipher.doFinal(input);
        return cipherText;
    }

    static byte[] decryptVote(byte[] cipherText,Key privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("ELGamal","BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plainText = cipher.doFinal(cipherText);
        return plainText;
    }

    public static void main(String[] args) throws Exception {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        KeyPairGenerator generator = KeyPairGenerator.getInstance("ELGamal","BC");
        SecureRandom random = new SecureRandom();
        generator.initialize(256, random);
        KeyPair pair = generator.generateKeyPair();
        Key pubKey = pair.getPublic();
        Key privKey = pair.getPrivate();

        byte[] cipher = encryptVote("This is insane",pubKey);
        System.out.println(new String(cipher));
        System.out.println("\n\n");

        byte[] plain = decryptVote(cipher,privKey);
        System.out.println(new String(plain));
        System.out.println("\n\n");
    }


}

