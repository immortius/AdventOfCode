package xyz.immortius.advent2016.day7;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day7 {

    private static final String YEAR = "2016";
    private static final String DAY = "7";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day7().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines);
        part2(lines);
    }


    private void part1(List<String> lines) {
        long validCount = lines.stream().filter(this::supportsTLS).count();
        
        System.out.println("Part 1: " + validCount);
    }

    private void part2(List<String> lines) {
        long validCount = lines.stream().filter(this::supportsSSL).count();

        System.out.println("Part 2: " + validCount);
    }

    private boolean supportsSSL(String ip) {
        List<String> supernetParts = new ArrayList<>();
        List<String> hypernetParts = new ArrayList<>();
        extractIPParts(ip, supernetParts, hypernetParts);

        List<String> abas = supernetParts.stream().flatMap(x -> extractABAs(x).stream()).toList();
        Set<String> babs = hypernetParts.stream().flatMap(x -> extractABAs(x).stream()).collect(Collectors.toSet());
        for (String aba : abas) {
            String bab = new String(new char[]{aba.charAt(1), aba.charAt(0), aba.charAt(1)});
            if (babs.contains(bab)) {
                return true;
            }
        }

        return false;
    }

    private List<String> extractABAs(String x) {
        List<String> extract = new ArrayList<>();
        for (int i = 2; i < x.length(); i++) {
            if (x.charAt(i - 2) == x.charAt(i) && x.charAt(i - 1) != x.charAt(i)) {
                extract.add(x.substring(i - 2, i + 1));
            }
        }
        return extract;
    }

    private boolean supportsTLS(String ip) {
        List<String> supernetParts = new ArrayList<>();
        List<String> hypernetParts = new ArrayList<>();
        extractIPParts(ip, supernetParts, hypernetParts);
        return supernetParts.stream().anyMatch(this::containsAbba) && hypernetParts.stream().noneMatch(this::containsAbba);
    }

    private void extractIPParts(String ip, List<String> supernetParts, List<String> hypernetParts) {
        int currentIndex = 0;
        while (currentIndex < ip.length()) {
            int startHypernetIndex = ip.indexOf('[', currentIndex);
            if (startHypernetIndex != -1) {
                supernetParts.add(ip.substring(currentIndex, startHypernetIndex));
                int endHypernetIndex = ip.indexOf(']', startHypernetIndex);
                hypernetParts.add(ip.substring(startHypernetIndex + 1, endHypernetIndex));
                currentIndex = endHypernetIndex + 1;
            } else {
                supernetParts.add(ip.substring(currentIndex));
                currentIndex = ip.length();
            }
        }
    }

    private boolean containsAbba(String x) {
        for (int i = 3; i < x.length(); i++) {
            if (x.charAt(i - 3) == x.charAt(i) && x.charAt(i - 2) == x.charAt(i - 1) && x.charAt(i - 1) != x.charAt(i)) {
                return true;
            }
        }
        return false;
    }


}

