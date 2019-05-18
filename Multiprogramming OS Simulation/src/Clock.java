/**
 * A singleton class representing clock.
 */
public class Clock {

    private static Clock instance = new Clock();

    public static Clock getInstance() {
        return instance;
    }

    private int time;

    private Clock() {
    }

    public int getTime() {
        return time;
    }

    public void useTime(int timeToUse) {
        time += timeToUse;
    }

}
