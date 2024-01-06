package xyz.immortius.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Range {
    private final long min;
    private final long max;

    public static Range createMinMax(long min, long max) {
        return new Range(min, max);
    }

    public static Range createMinLength(long min, long length) {
        return new Range(min, min + length - 1);
    }

    private Range(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public long length() {
        return max - min + 1;
    }

    public long min() {
        return min;
    }

    public long max() {
        return max;
    }

    public Collection<Range> deintersect(Range other) {
        if (other.min > max || other.max < min) {
            return Collections.singletonList(this);
        }
        List<Range> result = new ArrayList<>();
        if (other.min > min) {
            result.add(new Range(min, other.min - 1));
        }
        if (other.max < max) {
            result.add(new Range(other.max + 1, max));
        }
        return result;
    }

    public Range intersect(Range other) {
        if (other.min <= max && other.max >= min) {
            return new Range(Math.max(min, other.min), Math.min(max, other.max));
        }
        return null;
    }


    public boolean overlaps(Range other) {
        return other.min <= max && other.max >= min;
    }
}
