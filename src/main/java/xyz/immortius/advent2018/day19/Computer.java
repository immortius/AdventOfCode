package xyz.immortius.advent2018.day19;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Computer {

    public static final int UNBOUND = -1;

    private long[] registers;
    private int instructionPointer;
    private int ipRegister = UNBOUND;

    private int stepsRun = 0;
    private int maxSteps = -1;

    private int[] callCounts;

    private List<Instruction> instructions = new ArrayList<>();

    public Computer(int numRegisters) {
        this.registers = new long[numRegisters];
    }

    public Computer(Computer other) {
        this.registers = Arrays.copyOf(other.registers, other.registers.length);
        this.instructionPointer = other.instructionPointer;
        this.ipRegister = other.ipRegister;
        this.instructions = new ArrayList<>(other.instructions);
        this.maxSteps = other.maxSteps;
    }

    public void setInstructionRegister(int register) {
        ipRegister = register;
    }

    public void addInstruction(Instruction instruction) {
        this.instructions.add(instruction);
    }

    public void run() {
        callCounts = new int[instructions.size()];
        if (ipRegister != UNBOUND) {
            while (instructionPointer >= 0 && instructionPointer < instructions.size() && (stepsRun < maxSteps || maxSteps == -1)) {
                registers[ipRegister] = instructionPointer;
                callCounts[instructionPointer]++;
                instructions.get(instructionPointer).apply(registers);
                instructionPointer = (int) registers[ipRegister];
                instructionPointer++;
                stepsRun++;
            }
        } else {
            while (instructionPointer >= 0 && instructionPointer < instructions.size() && (stepsRun < maxSteps || maxSteps == -1)) {
                instructions.get(instructionPointer).apply(registers);
                callCounts[instructionPointer]++;
                instructionPointer++;
                stepsRun++;
            }
        }
    }

    public long getRegister(int num) {
        return registers[num];
    }

    public void setRegister(int num, long value) {
        registers[num] = value;
    }

    public int getStepsRun() {
        return stepsRun;
    }

    public int[] getCallCounts() {
        return callCounts;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }
}
