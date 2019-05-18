import java.io.File;
import java.util.*;

public class JobScheduler {

    private List<Job> jobsQueue = new ArrayList<>();
    private Queue<Job> readyQueue = new LinkedList<>();
    private RAM ram;

    private ProcessScheduler processScheduler;

    public JobScheduler(RAM ram, String inputFile) throws Exception {
        this.ram = ram;
        processScheduler = new ProcessScheduler(jobsQueue, readyQueue, ram);
        parseJobs(inputFile);
    }

    private void parseJobs(String inputFile) throws Exception {
        Scanner in = new Scanner(new File(inputFile));
        int pid = 1;
        while (in.hasNextLine()) {
            String jobLine = in.nextLine();
            String[] tokens = jobLine.split(",");
            String jobName = tokens[0].substring(3);
            Queue<Burst> bursts = new LinkedList<>();
            for (int i = 1; i < tokens.length; i++) {
                String token = tokens[i];
                if (token.startsWith("CPU:")) {
                    String timeString = token.substring(4);
                    int burstTime = Integer.parseInt(timeString);
                    if (burstTime == -1) {
                        bursts.add(new Burst(-1, 0));
                        break;
                    }
                    String memToken = tokens[++i];
                    String memString = memToken.substring(4);
                    int reqMemory = Integer.parseInt(memString);
                    bursts.add(new Burst(burstTime, reqMemory));
                } else if (token.startsWith("IO:")) {
                    String timeString = token.substring(3);
                    int burstTime = Integer.parseInt(timeString);
                    bursts.add(new Burst(burstTime));
                }
            }
            jobsQueue.add(new Job(jobName, bursts, pid++));
        }
    }

    public void loadMoreJobs() {
        for (Job job : jobsQueue) {
            if (ram.is90PercentFull()) {
                break;
            }
            if (job.hasEnoughMemoryForNextBurst(ram)) {
                job.createProcess();
                Process process = job.getProcess();
                job.allocateMemory(process.getCurrentBurst(), ram);
                readyQueue.add(job);
            }
        }

        // remove all jobs in readyQueue from jobsQueue.
        jobsQueue.removeAll(readyQueue);
    }

    public void startSimulation() {
        loadMoreJobs();
        new Thread(() -> {
            // load more jobs if there are more jobs and RAM is not 90% full.
            while (!jobsQueue.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                if (!ram.is90PercentFull()) {
                    loadMoreJobs();
                }
            }
        }).start();
        processScheduler.startSimulation();
    }

}
