package com.fengdis.util.jwt;

import com.auth0.jwt.algorithms.Algorithm;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class GienahJWT {

    private static volatile Algorithm algorithm;

    private GienahJWT() {
        if (algorithm != null) {
            throw new RuntimeException("Use getAlgorithm() method to get the single instance of this class.");
        }
    }

    public static Algorithm getAlgorithm() {
        if (algorithm == null) {
            synchronized (GienahJWT.class) {
                if (algorithm == null) {
                    RSAPublicKey publicKey = GienahJWTPublicKey.getPublicKey();
                    RSAPrivateKey privateKey = GienahJWTPrivateKey.getPrivateKey();
                    algorithm = Algorithm.RSA256(publicKey, privateKey);
                }
            }
        }
        return algorithm;
    }
}
