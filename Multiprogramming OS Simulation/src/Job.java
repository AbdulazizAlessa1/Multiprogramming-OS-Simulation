import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class Job {

    private String name;
    private Queue<Burst> bursts;
    private Process process;
    private List<RAM.Block> allocatedBlocks = new ArrayList<>();
    private int pid;

    public Job(String name, Queue<Burst> bursts, int pid) {
        this.name = name;
        this.bursts = bursts;
        this.pid = pid;
    }

    public boolean hasEnoughMemoryForNextBurst(RAM ram) {
        Burst next = bursts.peek();
        if (next == null) {
            throw new IllegalStateException("No remaining bursts to be done for the job");
        }
        return ram.getAvailableSpace() >= next.memoryChange;
    }

    public void createProcess() {
        process = new Process(this);
        process.setCurrentBurst(loadNextBurst());
    }

    public Process getProcess() {
        return process;
    }

    // returns next burst by removing it from the queue.
    public Burst loadNextBurst() {
        return bursts.poll();
    }

    public void allocateMemory(Burst burst, RAM ram) {
        if (burst.memoryChange > 0) {
            List<RAM.Block> allocated = ram.allocate(burst.memoryChange);
            allocatedBlocks.addAll(allocated);
        }
    }

    public void freeMemory(RAM ram, int size) {
        int blocksToFree = size / ram.getSizePerBlock();
        while (blocksToFree > 0) {
            RAM.Block block = allocatedBlocks.get(0);
            block.free();
            allocatedBlocks.remove(block);
            blocksToFree--;
        }
    }

    public void freeAllocatedMemory() {
        for (RAM.Block block : allocatedBlocks) {
            block.free();
        }
    }

    public void kill() {
        process.setState(Process.State.TERMINATED);
        freeAllocatedMemory();
    }

    public String getName() {
        return name;
    }

    public int getPid() {
        return pid;
    }

    @Override
    public String toString() {
        return "JOB - Name: " + name + " Bursts: " + Arrays.toString(bursts.toArray());
    }

}
