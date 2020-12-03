
/**
 * Callcentre2 program based on the linear LinkedList (an implementation of Deque) with size limit
 * -instead of circular buffers. It can add/remove from both sides (First/Last).
 * run by main().
 * In random 0-0.25s, a call will be appended, and workers try to handle them (about 0.5s for each).
 * Details in CC2. Use many java.util.concurrent classes, like TimeUnit, Semaphore.
 */
public class CallCentre2 {

    public static void main(String[] args) {
        CC2 CC2 = new CC2(25, 3); // 25 Callers and 3 Workers.
    }
}
