package xyz.immortius.advent2015.day4;

import com.google.common.base.Strings;
import com.google.common.hash.HashCode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Day4 {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        new Day4().run();
    }

    private void run() throws NoSuchAlgorithmException {
        String input = "ckczppom";

        part1(input);
        part2(input);
    }

    private void part1(String key) throws NoSuchAlgorithmException {
        System.out.println("Part 1: " + findDigestPrefixedWithZeroes(key, 5));
    }

    private void part2(String key) throws NoSuchAlgorithmException {
        System.out.println("Part 1: " + findDigestPrefixedWithZeroes(key, 6));
    }

    private int findDigestPrefixedWithZeroes(String key, int length) throws NoSuchAlgorithmException {
        String targetPrefix = Strings.padStart("", length, '0');
        int ext = 1;
        while (true) {
            String pass = key + ext;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(pass.getBytes(StandardCharsets.US_ASCII));
            String hash = HashCode.fromBytes(md5.digest()).toString();
            if (hash.startsWith(targetPrefix)) {
                return ext;
            }
            ext++;
        }
    }



}

