import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class SmolISA{
    // The idea is to make the smallest ISA possible, essentially a VM
    // Control
    // Logic/Arithmetic
    // Data movement

    // Why am I making this?
    // If something starts to be too complicated I can fall back to implementing on this machine

    // inspired by PDP-8
    // start goal: AC outputting final result and asserted to be correct 
    // mid goal: shell
    // End goal: Graphics, Network, Interactivity. Multiplayer ping pong.

    // Let's do a 64-bit word

    // Registers
    // - Programmer visible
    //  - Accumulator
    //  - Program Counter
    //  - Link Register
    // - Not visible
    //  - Memory-buffer register
    //  - Memory-Address register  
    // Ram
    // CPU
    // ALU

    // How do IO buses work? 

    /*
     * ISA:
     * LOAD
     * STORE
     * ADD
     * MULTIPLY
     * DIV
     * JUMP IF < 0
     */

    // C(r1,r2) -> r1
    enum ISACode {
        LOD,
        STR,
        ADD,
        MUL,
        DIV,
        JLZ,
    }

    enum register {
        AC,     // Accumulator
        PC,     // Program counter
        L,      // For overflow checks
        MBR,    // Memory Buffer register
        MAR,    // Memory Address register
    }

    public record ISAInstruction(ISACode code, long r1, long r2) {}

    // Assembly file probably looks like:
    /*
     * LOD 1,2
     * ADD 1,3
     * ADD 1,4
     * MUL 2,6
     * JLZ 1,3
     */
    // 10000 items * 64-bit/items * 1 byte/8 bits = 80000 bytes = 80kB
    public static long[] memory = new long[10000];
    public static long[] registers = new long[5];

    /*
     * How do you handle float operations?
     * How do you handle int operations?
     * How do you handle string operations?
     * How do you handle function representations?
     * How do you handle structs?
     * How do you handle Data structures?
     */

    public static void main(String[] args) throws IOException {
        // read file
        Stream<String> stream = Files.lines(Paths.get("bruh"));
        // The lines needs to be put inside memory not read one by one.
        // Let's do a harvard architecture first where the instructions is coming from a file.
        // Heap/Stack/Data is in RAM.
        stream.forEach(string -> {
            String[] instruction = string.split(" ");
            // parse file
            ISAInstruction inst = new ISAInstruction(ISACode.valueOf(instruction[0]), Long.parseLong(instruction[1]), Long.parseLong(instruction[2]));
            // execute on file commands
            parseCmd(inst);
        });
        // finish
        System.out.println("bruh");
    
    }

    // How is LOAD and STORE implemented in the CPU or logic gates?
    // How many bits is stored in a single memory address?

    /*
     * 1. Establish all the constants in file
     * 2. Load all the constants into a memory section
     * 3. Then you can continue executing
     */

    public static void parseCmd(ISAInstruction inst) {
        switch (inst.code){
            case LOD:
                // load from r2 memory into r1 register
                registers[(int)inst.r1] = memory[(int)inst.r2];
                break;
            case STR:
                // store from r2 register into r1 memory address
                memory[(int)inst.r1] = registers[(int)inst.r2];
                break;
            case ADD:
                registers[(int)inst.r1] = registers[(int)inst.r1] + registers[(int)inst.r2];
                break;
            case MUL:
                registers[(int)inst.r1] = registers[(int)inst.r1] * registers[(int)inst.r2];
                break;
            case DIV:
                // don't forget to handle divide by zero
                try{
                    registers[(int)inst.r1] = registers[(int)inst.r1] / registers[(int)inst.r2];
                } catch (ArithmeticException e) {
                    registers[register.L.ordinal()] = 1;
                }
                break;
            case JLZ:
                if (registers[(int)inst.r1] <= 0){
                    registers[register.PC.ordinal()] = registers[(int)inst.r2];
                }
                break;
            default:
                System.out.println("Invalid instruction code");
        }
    }

}