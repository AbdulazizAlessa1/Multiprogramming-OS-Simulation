import java.util.List;

public class Simulation {

    private static final int TOTAL_RAM = 192;
    private static final int OS_MEMORY = 32;
    private static final int SIZE_PER_BLOCK = 1;

    public static void main(String[] args) {
        RAM ram = new RAM(TOTAL_RAM, SIZE_PER_BLOCK);
        List<RAM.Block> blocks = ram.allocate(OS_MEMORY);
        if (blocks == null) {
            System.err.println("Insufficient RAM for OS");
            return;
        }

        JobScheduler jobScheduler;
        try {
            jobScheduler = new JobScheduler(ram, "data.txt");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error parsing data: " + e.getMessage());
            return;
        }
        jobScheduler.startSimulation();
    }

}
