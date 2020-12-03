
/**
 * Callcentre1 program based on the Q1(b) 'deque': circular buffer and can read from both sides.
 * run by main().
 * In random 0-0.25s, a call will be appended, and workers try to handle them (about 0.5s for each).
 * Details in CC1. Only use synchronized, wait()/notifyAll(),
 * -and not use any classes from java.util.concurrent.
 */
public class CallCentre1 {

    public static void main(String[] args) {
        CC1 CC1 = new CC1(25, 3); //25 Callers and 3 Workers.
    }
}
