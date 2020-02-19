package com.fengdis.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 1.0
 * @Descrittion: MD5加密工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class MD5Utils {

    protected static Logger logger = LoggerFactory.getLogger(MD5Utils.class);

    private static MessageDigest digest = null;


    public static String encrypt(String originalStr) {
        if (originalStr == null) {
            originalStr = "";
        }
        return hash(originalStr);
    }

    public synchronized static final String hash(String data) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException nsae) {
                logger.error("Failed to load the MD5 MessageDigest");
            }
        }
        // Now, compute hash.
        digest.update(data.getBytes());
        return encodeHex(digest.digest());
    }


    public static final String encodeHex(byte[] bytes) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        int i;

        for (i = 0; i < bytes.length; i++) {
            if (((int)bytes[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int)bytes[i] & 0xff, 16));
        }
        return buf.toString();
    }
}
