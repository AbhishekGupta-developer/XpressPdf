package com.myorganisation.XpressPdf.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    public static String generateMD5(String data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] hashBytes = messageDigest.digest(data.getBytes());
        StringBuilder stringBuilder = new StringBuilder();
        for(byte b : hashBytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }
}
