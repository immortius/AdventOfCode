package xyz.immortius.advent2018.day19;

public record Instruction(OpCode opCode, int inA, int inB, int outC) {

    public void apply(long[] registers) {
        opCode.apply(registers, this);
    }
}
