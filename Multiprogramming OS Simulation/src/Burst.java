public class Burst {
    // burst time.
    int time;

    // true for CPU burst, false for I/O burst.
    boolean cpuBurst;

    // change in memory; +ve for allocation, -ve for freeing up.
    int memoryChange;

    /**
     * A CPU burst
     *
     * @param time         burst time.
     * @param memoryChange the change in memory;  +ve for allocation, -ve for freeing up.
     */
    public Burst(int time, int memoryChange) {
        this.time = time;
        this.memoryChange = memoryChange;
        cpuBurst = true;
    }


    /**
     * An I/O burst.
     *
     * @param time burst time.
     */
    public Burst(int time) {
        this.time = time;
        cpuBurst = false;
    }

    @Override
    public String toString() {
        if (cpuBurst) {
            return "CPU: " + time + ", MEM: " + memoryChange;
        } else {
            return "IO: " + time;
        }
    }

}
