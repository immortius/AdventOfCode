package xyz.immortius.util;

import com.google.common.math.BigIntegerMath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

