package xyz.immortius.advent2018.day19;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class OpCodes {
    public static final List<Long> values = new ArrayList<>();

    public static final List<OpCode> OP_CODES = ImmutableList.of(
            new OpCode("addr", true, true, Long::sum),
            new OpCode("addi", true, false, Long::sum),
            new OpCode("mulr", true, true, (a, b) -> a * b),
            new OpCode("muli", true, false, (a, b) -> a * b),
            new OpCode("banr", true, true, (a, b) -> a & b),
            new OpCode("bani", true, false, (a, b) -> a & b),
            new OpCode("borr", true, true, (a, b) -> a | b),
            new OpCode("bori", true, false, (a, b) -> a | b),
            new OpCode("setr", true, false, (a, b) -> a),
            new OpCode("seti", false, false, (a, b) -> a),
            new OpCode("gtir", false, true, (a, b) -> (a > b) ? 1 : 0),
            new OpCode("gtri", true, false, (a, b) -> (a > b) ? 1 : 0),
            new OpCode("gtrr", true, true, (a, b) -> (a > b) ? 1 : 0),
            new OpCode("eqir", false, true, (a, b) -> (a == b) ? 1 : 0),
            new OpCode("eqri", true, false, (a, b) -> (a == b) ? 1 : 0),
            new OpCode("eqrr", true, true, (a, b) -> (a == b) ? 1 : 0),
            new OpCode("modr", true, true, (a, b) -> a % b),
            new OpCode("divr", true, true, (a, b) -> {
                if (a % b == 0) {
                    return a / b;
                } else {
                    return 0;
                }
            }),
            new OpCode("null", true, false, (a, b) -> a),
            new OpCode("prnt", true, true, (a, b) -> {
                if (!values.contains(a)) {
                    values.add(a);
                    return 0;
                } else {
                    return 1;
                }
            })
    );

    private static final Map<String, OpCode> lookup;

    static {
        lookup = ImmutableMap.copyOf(OP_CODES.stream().collect(Collectors.toMap(OpCode::name, x -> x)));
    }

    public static OpCode parse(String code) {
        return lookup.get(code);
    }

    private OpCodes() {}
}
