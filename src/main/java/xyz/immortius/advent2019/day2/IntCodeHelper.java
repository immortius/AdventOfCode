package xyz.immortius.advent2019.day2;

import java.util.Arrays;
import java.util.List;

public final class IntCodeHelper {
    private IntCodeHelper() {}

    public static long[] parse(List<String> program) {
        return parse(program.get(0));
    }

    public static long[] parse(String program) {
        return Arrays.stream(program.split(",\\s*")).mapToLong(Long::parseLong).toArray();
    }
}
