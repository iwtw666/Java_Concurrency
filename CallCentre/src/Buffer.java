
public class Buffer {
    /* number of calls */
    volatile int callNo = 0;
    /* number of answered calls */
    volatile int callOut = 0;
    /* buffer size and buffer */
    private final int size = 5;
    int[] buffer = new int[size];
    /* pointer in and out for circular buffer */
    private int in = 0;
    private int out = 0;
    /* buffer (queue) number/name */
    private char name;
    /* worker number */
    private int workerNo;
    /* stop flag to prevent stuck in answer or steal */
    boolean stop = false;

    /**
     * Constructor of Buffer.
     * @param queue - number/name of the buffer.
     */
    public Buffer(char queue) {
        this.name = queue;
    }

    /**
     * Append call(s) to the buffer.
     * @param call - the call.
     */
    synchronized void append(int call) {
        while (callNo == size) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        buffer[in] = call;
        in = (in + 1) % size; // circular buffer.
        callNo += 1;
        Event.CallAppendedToQueue(call, name); // print call info.
        notifyAll();
    }

    /**
     * Answer a call.
     * @param no - worker number.
     * @throws InterruptedException - Thread interrupt error.
     */
    synchronized void answer(int no) throws InterruptedException {
        int answered;
        while (callNo == 0) {
            try {
                if (stop) {
                    return; // if stop, cancel the current task.
                } else {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Thread.sleep(500); // 0.5s to deal with a call.
        answered = buffer[out];
//        buffer[out] = 0; // for test
        out = (out + 1) % size;
        Event.WorkerAnswersCall(no, answered);
        callNo -= 1;
        callOut++;
        notifyAll();
    }

    /**
     * Be stolen a call from a stealer.
     * @param stealer - the stealer.
     * @throws InterruptedException - thread interrupt error.
     */
    synchronized void beStolen(Buffer stealer) throws InterruptedException {
        int stolen;
        while (callNo == 0) {
            try {
                if (stop) {
                    return; // stop flag.
                } else {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Thread.sleep(501); // 0.501s to handle a call (others steal from here).
        in = (in - 1 + size) % size;
        stolen = buffer[in];
//        buffer[in]=0; // for test
        // prevent most situations of stealing during appending.
        if (stealer.callNo == 0) {
            Event.WorkerStealsCall(stealer.getWorkerNo(), stolen, name);
            callNo -= 1;
            callOut++;
            notifyAll();
        } else {
            in = (in + 1) % size;
            stolen = 0;
            return;
        }
    }

    /**
     * set the worker number for this buffer.
     * @param workerNo - worker number.
     */
    public void setWorkerNo(int workerNo) {
        this.workerNo = workerNo;
    }
    /**
     * get the worker number for this buffer.
     * @return - worker number.
     */
    public int getWorkerNo(){
        return this.workerNo;
    }

    /**
     * set stop flag to true and cancel the current task by jump out wait().
     */
    synchronized void cancel(){
        stop = true;
        notifyAll();
    }
}
