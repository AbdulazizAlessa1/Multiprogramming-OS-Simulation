import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Queue;

public class ProcessScheduler {

    private List<Job> jobsQueue;
    private Queue<Job> readyQueue;
    private RAM ram;
    private List<Process> completedProcesses = new ArrayList<>();


    public ProcessScheduler(List<Job> jobsQueue, Queue<Job> readyQueue, RAM ram) {
        this.jobsQueue = jobsQueue;
        this.readyQueue = readyQueue;
        this.ram = ram;
    }

    public void startSimulation() {
        printReadyQueue();
        while (!jobsQueue.isEmpty() || !readyQueue.isEmpty()) {

            boolean allWaiting = true;
            List<Job> finishedJobs = new ArrayList<>();
            try {
                for (Job job : readyQueue) {
                    Process process = job.getProcess();
                    process.addNoOfTimeInCPU();
                    Process.State state = process.getState();
                    if (state != Process.State.WAITING) {
                        allWaiting = false;
                    }
                    switch (state) {
                        case TERMINATED:
                        case KILLED:
                            finishedJobs.add(job);
                            break;
                        case READY:
                            process.execute(ram);
                            break;
                        case WAITING:
                            Burst burst = process.getCurrentBurst();
                            if (burst.time == -1) {
                                process.setState(Process.State.TERMINATED);
                                finishedJobs.add(job);
                            } else if (ram.getAvailableSpace() >= burst.memoryChange) {
                                allWaiting = false;
                                job.allocateMemory(burst, ram);
                                process.setState(Process.State.READY);
                            } else {
                                process.addNoOfWaitedTime();
                            }
                            break;
                    }
                }
            } catch (ConcurrentModificationException ignored) {
            }

            // remove finished jobs from ready queue.
            for (Job job : finishedJobs) {
                job.freeAllocatedMemory();
                readyQueue.remove(job);
                completedProcesses.add(job.getProcess());
            }

            printReadyQueue();

            // check for deadlock
            if (allWaiting && !readyQueue.isEmpty()) {
                Job jobToBeKilled = readyQueue.poll();
                for (Job job : readyQueue) {
                    if (job.getProcess().getCurrentBurst().memoryChange >
                            jobToBeKilled.getProcess().getCurrentBurst().memoryChange) {
                        jobToBeKilled = job;
                    }
                }
                jobToBeKilled.kill();
            }
        }

        saveSummary();
    }

    private void printReadyQueue() {
        StringBuilder builder = new StringBuilder("#### READY QUEUE ####");
        for (Job job : readyQueue) {
            builder.append("\n").append(job.toString());
        }
        GUI.getInstance().updateUI(builder.toString(), ram.getAvailableSpace());
    }

    private void saveSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("#### SUMMARY ####\n");
        for (Process process : completedProcesses) {
            builder.append("\nProcess id: ").append(process.getJob().getPid());
            builder.append("\nProgram name: ").append(process.getJob().getName());
            builder.append("\nLoaded Time: ").append(process.getReadyTime());
            builder.append("\nNo of time in CPU: ").append(process.getNoOfTimeInCPU());
            builder.append("\nTotal time spent in CPU: ").append(process.getSpentTimeInCPU());
            builder.append("\nNo of IO operations: ").append(process.getNoOfIOOps());
            builder.append("\nTotal time spent in IO: ").append(process.getSpentTimeInIO());
            builder.append("\nNo of time waiting for memory: ").append(process.getNoOfWaitedTime());
            builder.append("\nTime of termination or kill: ").append(process.getFinishedTime());
            builder.append("\nState: ").append(process.getState()).append("\n");
        }
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream("statistics.txt"));
            out.print(builder.toString());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        GUI.getInstance().updateUI("Simulation complete." +
                "\nStatistics saved in statistics.txt", ram.getAvailableSpace());
    }

}
