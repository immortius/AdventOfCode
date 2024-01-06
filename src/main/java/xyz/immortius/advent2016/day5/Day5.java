package xyz.immortius.advent2016.day5;

import com.google.common.hash.HashCode;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Day5 {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        long start = System.currentTimeMillis();
        new Day5().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws NoSuchAlgorithmException {
        part1();
        part2();
    }

    private void part1() throws NoSuchAlgorithmException {
        String doorId = "wtnhxymk";
        int ext = 0;
        StringBuilder password = new StringBuilder();
        while (password.length() < 8) {
            String input = doorId + ext;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(input.getBytes(StandardCharsets.US_ASCII));
            String hash = HashCode.fromBytes(md5.digest()).toString();
            if (hash.startsWith("00000")) {
                password.append(hash.charAt(5));
            }
            ext++;
        }


        System.out.println("Part 1: " + password);
    }

    private void part2() throws NoSuchAlgorithmException {
        String doorId = "wtnhxymk";
        int ext = 0;
        Map<Integer, Character> validCharacters = new HashMap<>();
        while (validCharacters.size() < 8) {
            String input = doorId + ext;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(input.getBytes(StandardCharsets.US_ASCII));
            String hash = HashCode.fromBytes(md5.digest()).toString();
            if (hash.startsWith("00000")) {
                int index = hash.charAt(5) - '0';
                if (index >= 0 && index < 8 && !validCharacters.containsKey(index)) {
                    validCharacters.put(index, hash.charAt(6));
                }
            }
            ext++;
        }

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(validCharacters.get(i));
        }

        System.out.println("Part 2: " + password);
    }



}

