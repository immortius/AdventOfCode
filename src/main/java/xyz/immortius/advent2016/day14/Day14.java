package xyz.immortius.advent2016.day14;

import com.google.common.hash.HashCode;
import xyz.immortius.util.CircularBuffer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Day14 {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        long start = System.currentTimeMillis();
        new Day14().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException, NoSuchAlgorithmException {
        example();
        part1();
        example2();
        part2();
    }

    private void example() throws NoSuchAlgorithmException {
        String salt = "abc";

        int index = indexOfNthKey(salt, 64, 0);

        System.out.println("Example: " + index);
    }

    private void part1() throws NoSuchAlgorithmException {
        String salt = "ngcjuoqr";

        int index = indexOfNthKey(salt, 64, 0);

        System.out.println("Part 1: " + index);
    }

    private void example2() throws NoSuchAlgorithmException {
        String salt = "abc";

        int index = indexOfNthKey(salt, 64, 2016);

        System.out.println("Example2: " + index);
    }

    private void part2() throws NoSuchAlgorithmException {
        String salt = "ngcjuoqr";

        int index = indexOfNthKey(salt, 64, 2016);

        System.out.println("Part 2: " + index);
    }

    private int indexOfNthKey(String salt, int toGenerate, int stretch) throws NoSuchAlgorithmException {
        CircularBuffer<String> hashes = CircularBuffer.create(1000);
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        for (int i = 0; i < 1000; i++) {
            String input = salt + i;
            md5.update(input.getBytes(StandardCharsets.US_ASCII));
            String hash = HashCode.fromBytes(md5.digest()).toString();
            md5.reset();
            for (int j = 0; j < stretch; j++) {
                md5.update(hash.getBytes(StandardCharsets.US_ASCII));
                hash = HashCode.fromBytes(md5.digest()).toString();
                md5.reset();
            }
            hashes.add(hash);
        }

        int index = -1;
        while (toGenerate > 0) {
            index++;
            String hash = hashes.get(0);

            String newLast = salt + (index + 1000);
            md5.update(newLast.getBytes(StandardCharsets.US_ASCII));
            String newHash = HashCode.fromBytes(md5.digest()).toString();
            md5.reset();
            for (int j = 0; j < stretch; j++) {
                md5.update(newHash.getBytes(StandardCharsets.US_ASCII));
                newHash = HashCode.fromBytes(md5.digest()).toString();
                md5.reset();
            }

            hashes.add(newHash);

            char triple = containsSameCharacterInARow(hash, 3);
            if (triple != 0) {
                for (String futureHash : hashes) {
                    if (containsCharacterInARow(futureHash, 5, triple)) {
                        toGenerate--;
                        break;
                    }
                }

            }
        }
        return index;
    }

    private char containsSameCharacterInARow(String string, int length) {
        char last = '\0';
        int count = 0;
        for (char c : string.toCharArray()) {
            if (c == last) {
                count++;
                if (count == length) {
                    return c;
                }
            } else {
                last = c;
                count = 1;
            }
        }
        return '\0';
    }

    private boolean containsCharacterInARow(String string, int length, char desired) {
        int count = 0;
        for (char c : string.toCharArray()) {
            if (c == desired) {
                count++;
                if (count == length) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }




}

