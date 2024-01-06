package xyz.immortius.advent2018.day19;

public record OpCode(String name, boolean fromRegisterA, boolean fromRegisterB, OpFunc function) {
    void apply(long[] registers, Instruction instruction) {
        long a = (fromRegisterA) ? registers[instruction.inA()] : instruction.inA();
        long b = (fromRegisterB) ? registers[instruction.inB()] : instruction.inB();
        registers[instruction.outC()] = function.apply(a, b);
    }

    @Override
    public String toString() {
        return "OpCode{" +
                "name='" + name + '\'' +
                '}';
    }
}
