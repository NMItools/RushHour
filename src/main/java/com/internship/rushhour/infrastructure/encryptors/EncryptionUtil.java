package com.internship.rushhour.infrastructure.encryptors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class EncryptionUtil {

    @Value("${encryption.util.key.type}")
    private String keyType;
    @Value("${encryption.util.algorithm}")
    private String AES_CIPHER_ALGORITHM;
    private IvParameterSpec ivParameterSpec;
    @Value("${encryption.util.key}")
    private String key;
    @Value("${encryption.util.bytes}")
    private byte[] vectorBytes;

    @PostConstruct
    private void setIvParameterSpec(){
        ivParameterSpec = new IvParameterSpec(vectorBytes);
    }

    public SecretKey createAESKey() {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        SecretKey key = new SecretKeySpec(keyBytes, 0, keyBytes.length, keyType);
        return key;
    }

    public String doAESEncryption(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, createAESKey(), ivParameterSpec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String doAESDecryption(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, createAESKey(), ivParameterSpec);

        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

}
