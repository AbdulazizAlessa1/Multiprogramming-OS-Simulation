import java.util.ArrayList;
import java.util.List;

public class RAM {

    //private static final int OS_MEMORY = 32;

    private final int sizePerBlock;
    private final int totalSize;
    private Block[] blocks;

    public RAM(int totalSize, int sizePerBlock) {
        this.totalSize = totalSize;
        this.sizePerBlock = sizePerBlock;
        initBlocks(totalSize);
        //initOS();
    }

    private void initBlocks(int totalSize) {
        int totalBlocks = totalSize / sizePerBlock;
        blocks = new Block[totalBlocks];
        for (int i = 0; i < totalBlocks; i++) {
            blocks[i] = new Block(i);
        }
    }

    List<Block> allocate(int requiredMemory) {
        if (requiredMemory > getAvailableSpace()) {
            System.err.println("Required: " + requiredMemory + " Avail:" + getAvailableSpace());
            return null;
        }
        List<Block> allocatedBlocks = new ArrayList<>();
        while (requiredMemory > 0) {
            Block available = getAvailableBlock();
            if (available != null) {
                available.use();
                allocatedBlocks.add(available);
                requiredMemory -= sizePerBlock;
            }
        }
        return allocatedBlocks;
    }

    int getAvailableSpace() {
        int count = 0;
        for (Block block : blocks) {
            if (block.isFree()) {
                count++;
            }
        }
        return count * sizePerBlock;
    }

    public int getSizePerBlock() {
        return sizePerBlock;
    }

    public int getTotalSize() {
        return totalSize;
    }

    /**
     * @return true if less than 10% of total memory is available.
     */
    public boolean is90PercentFull() {
        return getAvailableSpace() < 0.1 * totalSize;
    }

    private Block getAvailableBlock() {
        for (Block block : blocks) {
            if (block.isFree()) return block;
        }
        return null;
    }

    public class Block {
        private int address;
        private boolean free;

        private Block(int address) {
            this.address = address;
            free = true;
        }

        private boolean isFree() {
            return free;
        }

        private void use() {
            if (!free) {
                throw new RuntimeException("The block is already used.");
            }
            free = false;
        }

        public void free() {
            free = true;
        }

    }

}
