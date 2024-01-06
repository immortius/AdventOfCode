package xyz.immortius.util;

import com.google.common.math.BigIntegerMath;
import com.google.common.math.LongMath;

import java.math.RoundingMode;
import java.util.*;

public final class MathUtil {
    private MathUtil() {
    }

    public static List<Double> solveQuadratic(double a, double b, double c) {
        double variant = Math.sqrt(b * b - 4 * a * c);
        List<Double> result = new ArrayList<>();
        result.add((-b - variant) / (2 * a));
        result.add((-b + variant) / (2 * a));
        return result;
    }

    public static Set<Long> factorize(long value) {
        Set<Long> result = new LinkedHashSet<>();
        long s = LongMath.sqrt(value, RoundingMode.FLOOR);
        for (long i = 1; i < s; i++) {
            if (value % i == 0) {
                result.add(i);
                result.add(value / i);
            }
        }
        return result;
    }

    public static long lcm(Collection<Long> factors) {
        long largest = 0;
        for (long factor : factors) {
            largest = Math.max(largest, factor);
        }

        for (Long factor : factors) {
            long value = largest;
            while (value % factor != 0) {
                value += largest;
            }
            largest = value;
        }

        return largest;
    }

    public static long combinationsWithRepetitions(int number, int range) {
        if (number < 0) {
            return 0;
        }
        return BigIntegerMath.factorial(range + number - 1).divide(BigIntegerMath.factorial(number)).divide(BigIntegerMath.factorial(range - 1)).longValue();
    }
}

