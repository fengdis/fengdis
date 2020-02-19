package com.fengdis.util.jwt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

class GienahJWTPublicKey {
    private static String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmDBV8d7E5xlhc6u0pynkdHtSpHm7dcKg4jmU/ZZg1yuVQwNqMwUQdRffs5B8F5gFUOB3JG+jTOC+9EEl/Pi2nKMn4yVL5WpzKnoCcJYE7IkRAxedfCcqvC+ckrN0bvLKyrTaOvaSNZnsMNxCe2kLMzymhtM0lwBRGLQQBsjKg2ngl7Qdjrl4RXEnCIwzPUUPT546Nip79W9ocNqsbTCmP/dSNrTYjiwjO785RcR6Uhk18fTIn5FHcnfXtXWLFuGapG8XMNot9gVIxSmBQbXqD01MYl0JY1lDQsIRAFgBbStFvS676WWBmLeqp1V2HHSwRzaBxlIdI5zJ3SuLaGJ1hwIDAQAB";

    private static volatile RSAPublicKey publicKey = null;

    private GienahJWTPublicKey() {
        if (publicKey != null) {
            throw new RuntimeException("Use getPublicKey() method to get the single instance of this class.");
        }
    }

    static RSAPublicKey getPublicKey() {
        if (publicKey == null) {
            synchronized (GienahJWTPublicKey.class) {
                if (publicKey == null) {
                    try {
                        KeyFactory kf = KeyFactory.getInstance("RSA");
                        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
                        publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        throw new ExceptionInInitializerError(e);
                    }
                }
            }
        }
        return publicKey;
    }

}
