package xyz.immortius.util;

import java.util.function.Function;

public class NewtonEstimation {

    private final Function<Long, Long> scorer;

    public NewtonEstimation(Function<Long, Long> scorer) {
        this.scorer = scorer;
    }

    public long findValue(long initialLeft, long initialRight, long target) {
        long l = initialLeft;
        long r = initialRight;
        int sign = -Long.signum(scorer.apply(r) - scorer.apply(l));
        while (l < r) {
            long m = l / 2 + r / 2;
            long diff = scorer.apply(m);
            int cmp = Long.compare(diff, target) * sign;
            if (cmp > 0) {
                l = m + 1;
            } else if (cmp < 0) {
                r = m - 1;
            } else {
                while (target == scorer.apply(m - 1)) {
                    m = m - 1;
                }
                return m;
            }
        }
        return r;
    }

    public long minimise(long initialLeft, long initialRight) {
        long l = initialLeft;
        long r = initialRight;
        long lValue = scorer.apply(l);
        long rValue = scorer.apply(r);
        while (l != r) {
            long m = l / 2 + r / 2;
            long diff = scorer.apply(m);

            long lDiff = Math.abs(lValue - diff);
            long rDiff = Math.abs(rValue - diff);

            if (diff < lValue && (diff >= rValue || lDiff >= rDiff)) {
                lValue = diff;
                l = m;
            } else if (diff < rValue) {
                rValue = diff;
                r = m;
            } else {
                return m;
            }
        }
        return l;
    }

}
