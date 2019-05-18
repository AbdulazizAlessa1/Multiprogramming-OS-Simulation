public class Process {

    public enum State {
        READY, WAITING, RUNNING, TERMINATED, KILLED;
    }

    private State state;
    private Job job;
    private Burst currentBurst;

    private int readyTime;
    private int noOfTimeInCPU;
    private int spentTimeInCPU;
    private int spentTimeInIO;
    private int noOfIOOps;
    private int noOfWaitedTime;
    private int finishedTime;

    public Process(Job job) {
        this.job = job;
        state = State.READY;
        readyTime = Clock.getInstance().getTime();
    }

    public void setCurrentBurst(Burst currentBurst) {
        this.currentBurst = currentBurst;
    }

    public int getReadyTime() {
        return readyTime;
    }

    public int getSpentTimeInCPU() {
        return spentTimeInCPU;
    }

    public int getSpentTimeInIO() {
        return spentTimeInIO;
    }

    public int getNoOfTimeInCPU() {
        return noOfTimeInCPU;
    }

    public int getNoOfIOOps() {
        return noOfIOOps;
    }

    public void addNoOfTimeInCPU() {
        noOfTimeInCPU++;
    }

    public void addNoOfWaitedTime() {
        noOfWaitedTime++;
    }

    public int getNoOfWaitedTime() {
        return noOfWaitedTime;
    }

    public int getFinishedTime() {
        return finishedTime;
    }

    public Job getJob() {
        return job;
    }

    public void execute(RAM ram) {
        setState(State.RUNNING);
        if (currentBurst.time > 0) {
            try {
                Thread.sleep(currentBurst.time);
            } catch (InterruptedException ignored) {
            }
            Clock.getInstance().useTime(currentBurst.time);
            if (currentBurst.cpuBurst) {
                spentTimeInCPU += currentBurst.time;
            } else {
                spentTimeInIO += currentBurst.time;
                noOfIOOps++;
            }
        }
        if (currentBurst.memoryChange < 0) {
            job.freeMemory(ram, -currentBurst.memoryChange);
        }
        setState(State.WAITING);
        currentBurst = job.loadNextBurst();
    }

    public Burst getCurrentBurst() {
        return currentBurst;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        if (state == State.TERMINATED || state == State.KILLED) {
            finishedTime = Clock.getInstance().getTime();
        }
    }

}
