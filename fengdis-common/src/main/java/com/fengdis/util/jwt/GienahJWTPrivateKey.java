package com.fengdis.util.jwt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

class GienahJWTPrivateKey {
    private static String privateKeyString = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCYMFXx3sTnGWFzq7SnKeR0e1Kkebt1wqDiOZT9lmDXK5VDA2ozBRB1F9+zkHwXmAVQ4Hckb6NM4L70QSX8+LacoyfjJUvlanMqegJwlgTsiREDF518Jyq8L5ySs3Ru8srKtNo69pI1meww3EJ7aQszPKaG0zSXAFEYtBAGyMqDaeCXtB2OuXhFcScIjDM9RQ9Pnjo2Knv1b2hw2qxtMKY/91I2tNiOLCM7vzlFxHpSGTXx9MifkUdyd9e1dYsW4Zqkbxcw2i32BUjFKYFBteoPTUxiXQljWUNCwhEAWAFtK0W9LrvpZYGYt6qnVXYcdLBHNoHGUh0jnMndK4toYnWHAgMBAAECggEAWiNGxBrNRJMjxP7N4LRPOy5PpFr1TKoZ3+N6+zc15DtMDITZQZ6eQLwDIrdQyCih/WT1K/9zGPWEI+byOQKWJe/8j+xnJ1oFHANnrocsI2yLRumVvRL1nMAWXE0tiQkEU0sKdiHUNysTWV0bgzyUBaTGfNRbMZRec+MyGtKSH4KMR1VRWxYiIGk/N0/Kg0ZuXUbUv1PszAOiSckd5AAZA7puI1l+GNz+CDlc3W5bVIZ1FW3XmOz2vh5wlOaZEwk03XhezXQHqZG1kO0+4YAxjE0CHEz0yWaN3BJ4IAI5ob9BMg6+6ygS5RlV3xuk5YyCaiJOjWJLIONEIanVFbq2AQKBgQDJRCBHBGO4AmaDLdiL94nbXi83+xC6IdnALeodk0R57dUjshah2Dq0ZVrlGaST0pRwYYFxUmHaE1S7ISaOnH0rfbW07ymr6qX1wwbNVFAsb1XNyzEvVNFWy1M6Rk1Y/tR/rIzdbfXaNkn0hcdwrkKLR3OSNGqyORa82igR9sn+iQKBgQDBk4OiucVpy4OSi3XN+rKlCjKHyHHlEl5ExRSYrxfhKbH/eB2tMrRKFLUgUMXUwExb21paAB9+0MB0q17vA1jGRQcS3VFlx6uElkWh5B4R9hVePSp5CessZX3mpPlk3qmyHMSUceSWM+rlPpBQ+jdeQSenSdNq5ScL7IztT7lPjwKBgBN7jMRTIHQHh3TbU0L5S1b43wlEPHJIJJP93dPPFanX4/H4o0g3bjdYOFxFBzFoCQZ36dfWabJ6fJFAvELB0zwKhzRkklwZ8sa/gkEOEFS80kmBidlTFJCaTgwuEf6zbE6PnXMx2cKtzqrk6FF4DOj+malleY6XCCRv2cEjappBAoGAQG0MPrh9j7yASeHIj4mEs5E1SCPaZFc8sL63ICoGVJY/+7rKhb5+armBnwPDFA/8WgYjiBjKYravlyUCL6J5hWOr+wdV8/4Eg9hPzJXTvD6e1gKdhDNc5iXRRVZa+stwxyrz8kQFxEf7QdkUxx7AFsqwm+jiSI2D9WABd2o1TtkCgYEAt7oh5AQzS/SCTGNvaWd/LzbBpjsRSz0kVVf9Gh5Z5w4ZvGIAaNuPcWRy9iLcFeUAZ/oEUdPe3ufJWv3EBbW0nJBDzV/XWZrUWci03XjJf2SXLf2s+1yrcgP198V9r4nVOVnBFWJY1OyCmreRNX2WmRCqAoMp0VeKNnB51eP+f1k=";

    private static RSAPrivateKey privateKey = null;

    private GienahJWTPrivateKey() {
        if (privateKey != null) {
            throw new RuntimeException("Use getPrivateKey() method to get the single instance of this class.");
        }
    }

    static RSAPrivateKey getPrivateKey() {
        if (privateKey == null) {
            synchronized (GienahJWTPrivateKey.class) {
                if (privateKey == null) {
                    try {
                        KeyFactory kf = KeyFactory.getInstance("RSA");
                        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
                        privateKey = (RSAPrivateKey) kf.generatePrivate(keySpecPKCS8);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        throw new ExceptionInInitializerError(e);
                    }
                }
            }
        }
        return privateKey;
    }

}
