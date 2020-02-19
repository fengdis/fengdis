package com.fengdis.util.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.Claim;

import java.util.*;

/**
 * @version 1.0
 * @Descrittion: JWT工具类（采用RSA256公钥私钥加密算法）
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class JWTToken {

    /**
     * token秘钥，请勿泄露，请勿随便修改 backups:JKKLJOoasdlfj
     */
    //public static final String SECRET = "JKKLJOoasdlfj";

    /**
     * token 过期时间: 7天
     */
    public static final int calendarField = Calendar.DATE;
    public static final int calendarInterval = 7;

    private static String issuer = "fengdi";

    /**
     * JWT生成token
     * JWT构成: header, payload, signature
     * @param userId
     * @return
     * @throws Exception
     */
    public static String createToken(String userId) throws Exception {
        Date iatDate = new Date();
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(calendarField, calendarInterval);
        Date expiresDate = nowTime.getTime();

        Map<String, Object> header = new HashMap<>();
        //header.put("alg", "HS256");
        //header.put("typ", "JWT");

        // build token
        // param backups {iss:Service, aud:APP}
        String token = JWT.create().withHeader(header) // header
                //.withClaim("iss", "Service")  // payload
                //.withClaim("aud", "App")
                .withClaim("userId", userId)
                .withIssuedAt(iatDate)          // sign time
                .withExpiresAt(expiresDate)     // expire time
                .withIssuer(issuer)             //
                //.sign(Algorithm.HMAC256(SECRET)); // signature
                .sign(GienahJWT.getAlgorithm());

        return token;
    }

    /**
     * 校验Token
     * @param token
     * @return
     * @throws Exception
     */
    public static boolean verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(GienahJWT.getAlgorithm()).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Map<String, Claim> getClaims(String token) {
        Map<String, Claim> claims;
        try {
            JWTVerifier verifier = JWT.require(GienahJWT.getAlgorithm()).build();
            return verifier.verify(token).getClaims();
        } catch (Exception e) {
            return null;
        }
    }

}
