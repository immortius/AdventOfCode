package xyz.immortius.advent2019.day2;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IntCodeComputer {
    private final List<Long> state;
    private long instructionPointer;
    private long relativeBase;
    private final IntCodeInputStream input;
    private final IntCodeOutputStream output;

    private final static OpCode[] ops = new OpCode[100];

    static {
        ops[1] = new OpCode(3, (computer, params) -> {
            long value = params[0].read() + params[1].read();
            params[2].write(value);
            return true;
        });
        ops[2] = new OpCode(3, (computer, params) -> {
            long value = params[0].read() * params[1].read();
            params[2].write(value);
            return true;
        });
        ops[3] = new OpCode(1, (computer, params) -> {
            try {
                params[0].write(computer.input.send());
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupted!");
                return false;
            }
        });
        ops[4] = new OpCode(1, (computer, params) -> {
            try {
                computer.output.receive(params[0].read());
                return true;
            } catch (InterruptedException e) {
                return false;
            }
        });
        ops[5] = new OpCode(2, (computer, params) -> {
            long value = params[0].read();
            if (value != 0) {
                computer.instructionPointer = params[1].read();
            }
            return true;
        });
        ops[6] = new OpCode(2, (computer, params) -> {
            long value = params[0].read();
            if (value == 0) {
                computer.instructionPointer = params[1].read();
            }
            return true;
        });
        ops[7] = new OpCode(3, (computer, params) -> {
            long lhs = params[0].read();
            long rhs = params[1].read();
            params[2].write((lhs < rhs) ? 1 : 0);
            return true;
        });
        ops[8] = new OpCode(3, (computer, params) -> {
            long lhs = params[0].read();
            long rhs = params[1].read();
            params[2].write((lhs == rhs) ? 1 : 0);
            return true;
        });
        ops[9] = new OpCode(1, (computer, params) -> {
            computer.relativeBase += params[0].read();
            return true;
        });
        ops[99] = new OpCode(0, (computer, params) -> false);
    }

    public IntCodeComputer(long[] program) {
        this(program, new ConsoleInputStream(), new ConsoleOutputStream());
    }

    public IntCodeComputer(long[] program, IntCodeInputStream input) {
        this(program, input, new ConsoleOutputStream());
    }

    public IntCodeComputer(long[] program, IntCodeInputStream input, IntCodeOutputStream output) {
        this.state = new ArrayList<>();
        for (long i : program) {
            state.add(i);
        }
        this.input = input;
        this.output = output;
    }

    public void run() {
        while (instructionPointer >= 0) {
            long instruction = get(instructionPointer);
            OpCode opCode = ops[(int) (instruction % 100)];
            Parameter[] params = assembleParameters(instruction, opCode);

            instructionPointer += opCode.numParameters + 1;
            if (!opCode.operation.run(this, params)) {
                return;
            }
            if (Thread.interrupted()) {
                return;
            }
        }
        System.out.println("Instruction pointer underflow");
    }

    @NotNull
    private Parameter[] assembleParameters(long instruction, OpCode opCode) {
        Parameter[] params = new Parameter[opCode.numParameters];
        long remainder = instruction / 100;
        for (int i = 0; i < opCode.numParameters; i++) {
            params[i] = new Parameter(this, instructionPointer + i + 1, ParameterMode.get((int) (remainder % 10)));
            remainder = remainder / 10;
        }
        return params;
    }

    public void set(long position, long value) {
        if (position > Integer.MAX_VALUE) {
            System.out.println("Very large address detected");
        }
        if (position < state.size()) {
            state.set((int) position, value);
        } else {
            for (int i = state.size(); i < position; i++) {
                state.add(0L);
            }
            state.add(value);
        }
    }

    public long get(long position) {
        if (position > Integer.MAX_VALUE) {
            System.out.println("Very large address detected");
        }
        if (position < state.size()) {
            return state.get((int) position);
        }
        return 0;
    }

    public long getRelativeBase() {
        return relativeBase;
    }

    public void setRelativeBase(long value) {
        relativeBase = value;
    }

    @Override
    public String toString() {
        return "IntCodeComputer{" +
                "state=" + state +
                '}';
    }

    private record Parameter(IntCodeComputer computer, long address, ParameterMode mode) {

        public long read() {
            return mode.read(computer, address);
        }

        public void write(long value) {
            mode.write(computer, address, value);
        }
    }

    private enum ParameterMode implements ParamReader, ParamWriter {
        Position(0,
                (computer, address) -> computer.get(computer.get(address)),
                (computer, address, value) -> computer.set(computer.get(address), value)),
        Immediate(1,
                (computer, address) -> computer.get(address),
                (computer, address, value) -> computer.set(computer.get(address), value)),
        Relative(2,
                (computer, address) -> computer.get(computer.getRelativeBase() + computer.get(address)),
                (computer, address, value) -> computer.set(computer.getRelativeBase() + computer.get(address), value));

        private final ParamReader reader;
        private final ParamWriter writer;
        private final int id;

        ParameterMode(int id, ParamReader reader, ParamWriter writer) {
            this.id = id;
            this.reader = reader;
            this.writer = writer;
        }

        public static ParameterMode get(int id) {
            return ParameterMode.values()[id];
        }

        @Override
        public long read(IntCodeComputer computer, long pointer) {
            return reader.read(computer, pointer);
        }

        @Override
        public void write(IntCodeComputer computer, long pointer, long value) {
            writer.write(computer, pointer, value);
        }
    }

    private interface ParamReader {
        long read(IntCodeComputer computer, long address);
    }

    private interface ParamWriter {
        void write(IntCodeComputer computer, long address, long value);
    }

    private record OpCode(int numParameters, Operation operation) {
    }

    private interface Operation {
        boolean run(IntCodeComputer computer, Parameter[] parameters);
    }
}
