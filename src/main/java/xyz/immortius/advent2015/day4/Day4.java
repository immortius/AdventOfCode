package xyz.immortius.advent2015.day4;

import com.google.common.hash.HashCode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Day4 {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        new Day4().run();
    }

    private void run() throws NoSuchAlgorithmException {
        String key = "ckczppom";
        int ext = 1;
        boolean found = false;
        while (!found) {
            String pass = key + ext;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(pass.getBytes(StandardCharsets.US_ASCII));
            String hash = HashCode.fromBytes(md5.digest()).toString();
            if (hash.startsWith("000000")) {
                System.out.println(pass + " - " + hash);
                found = true;
            }
            ext++;
        }
    }



}

