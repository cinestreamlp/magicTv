package com.allreplay.magicTv;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoUtils {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final String DIGEST_MD5 = "MD5";
    public static final String DIGEST_SHA1 = "SHA1";
    public static final String DIGEST_SHA256 = "SHA256";
    private static final String ENCRYPT_SHA256 = "AES/CBC/PKCS5Padding";
    public static final String HMAC_MD5 = "HmacMD5";
    public static final String HMAC_SHA1 = "HmacSHA1";
    public static final String HMAC_SHA256 = "HmacSHA256";
    private static final String KEY_AES = "AES";

    static {
        $assertionsDisabled = !CryptoUtils.class.desiredAssertionStatus() ? true : false;
    }

    private CryptoUtils() {
    }

    public static char[] encodeHex(byte[] data) {
        char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int length = data.length;
        char[] out = new char[(length << 1)];
        int j = 0;
        for (int i = 0; i < length; i++) {
            int i2 = j + 1;
            out[j] = DIGITS[(data[i] & 240) >>> 4];
            j = i2 + 1;
            out[i2] = DIGITS[data[i] & 15];
        }
        return out;
    }

    public static byte[] sha1Digest(String data) {
        return digest(DIGEST_SHA1, data);
    }

    public static byte[] sha1Digest(byte[] data) {
        return digest(DIGEST_SHA1, data);
    }

    public static byte[] md5Digest(String data) {
        return digest(DIGEST_MD5, data);
    }

    public static byte[] md5Digest(byte[] data) {
        return digest(DIGEST_MD5, data);
    }

    public static byte[] sha256Digest(String data) {
        return digest(DIGEST_SHA256, data);
    }

    public static byte[] sha256Digest(byte[] data) {
        return digest(DIGEST_SHA256, data);
    }

    public static byte[] md5Hmac(String key, String data) {
        return hmac(HMAC_MD5, key, data);
    }

    public static byte[] sha256Hmac(String key, String data) {
        return hmac(HMAC_SHA256, key, data);
    }

    public static byte[] sha1Hmac(String key, String data) {
        return hmac(HMAC_SHA1, key, data);
    }

    public static byte[] md5Hmac(byte[] key, byte[] data) {
        return hmac(HMAC_MD5, key, data);
    }

    public static byte[] sha256Hmac(byte[] key, byte[] data) {
        return hmac(HMAC_SHA256, key, data);
    }

    public static byte[] sha1Hmac(byte[] key, byte[] data) {
        return hmac(HMAC_SHA1, key, data);
    }

    public static byte[] digest(String algorithm, String data) {
        return digest(algorithm, data.getBytes());
    }

    public static byte[] digest(String algorithm, byte[] data) {
        try {
            return MessageDigest.getInstance(algorithm).digest(data);
        } catch (NoSuchAlgorithmException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    public static byte[] hmac(String algorithm, String key, String data) {
        return hmac(algorithm, key.getBytes(), data.getBytes());
    }

    public static byte[] hmac(String algorithm, byte[] key, byte[] data) {
        Exception ignored;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            ignored = e;
            ignored.printStackTrace();
            return null;
        } catch (IllegalStateException e2) {
            ignored = e2;
            ignored.printStackTrace();
            return null;
        } catch (InvalidKeyException e3) {
            ignored = e3;
            ignored.printStackTrace();
            return null;
        }
    }

    private static Cipher getAes256Cipher(int cipherMode, byte[] iv, byte[] key) {
        GeneralSecurityException ignored;
        try {
            AlgorithmParameterSpec ivSpec;
            if (key.length != 32) {
                key = sha256Digest(key);
            }
            SecretKeySpec keySpec = new SecretKeySpec(key, KEY_AES);
            Cipher cipher = Cipher.getInstance(ENCRYPT_SHA256);
            if (iv != null) {
                ivSpec = new IvParameterSpec(iv);
            } else {
                ivSpec = null;
            }
            cipher.init(cipherMode, keySpec, ivSpec);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            ignored = e;
            ignored.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e2) {
            ignored = e2;
            ignored.printStackTrace();
            return null;
        } catch (InvalidKeyException e3) {
            ignored = e3;
            ignored.printStackTrace();
            return null;
        } catch (InvalidAlgorithmParameterException e4) {
            ignored = e4;
            ignored.printStackTrace();
            return null;
        }
    }

    public static byte[] encryptAes256(byte[] key, byte[] iv, byte[] data) {
        GeneralSecurityException ignored;
        try {
            Cipher cipher = getAes256Cipher(1, iv, key);
            if ($assertionsDisabled || cipher != null) {
                return cipher.doFinal(data);
            }
            throw new AssertionError();
        } catch (IllegalBlockSizeException e) {
            ignored = e;
            ignored.printStackTrace();
            return null;
        } catch (BadPaddingException e2) {
            ignored = e2;
            ignored.printStackTrace();
            return null;
        }
    }

    public static byte[] decryptAes256(byte[] key, byte[] iv, byte[] data) {
        GeneralSecurityException ignored;
        try {
            Cipher cipher = getAes256Cipher(2, iv, key);
            if ($assertionsDisabled || cipher != null) {
                return cipher.doFinal(data);
            }
            throw new AssertionError();
        } catch (IllegalBlockSizeException e) {
            ignored = e;
            ignored.printStackTrace();
            return null;
        } catch (BadPaddingException e2) {
            ignored = e2;
            ignored.printStackTrace();
            return null;
        }
    }

    public static String getSecurityProviderAlgorithms() {
        StringBuilder sb = new StringBuilder();
        for (Provider provider : Security.getProviders()) {
            sb.append("provider: ").append(provider.getName()).append('\n');
            for (Service service : provider.getServices()) {
                sb.append("  algorithm: ").append(service.getAlgorithm()).append('\n');
            }
        }
        return sb.toString();
    }
}
