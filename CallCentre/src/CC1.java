
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CallCenter1(CC1) class, the main implementation of 'CallCentre1'.
 * In random 0-0.25s, a call will be appended, and the workers handle a call for about 0.5s.
 * "not want to make it static in the main() of CallCentre1,
 * so create a new file to implement this class".
 */
public class CC1 {
    /* 3 queues */
    Buffer A = new Buffer('A');
    Buffer B = new Buffer('B');
    Buffer C = new Buffer('C');
    /* random */
    Random random = new Random();
    /* choice for workers */
    List<Integer> choice = new ArrayList<>();
    /* end flag */
    private boolean end = false;
    private int printed = 0;

    /**
     * Constructor of CallCenter1 (CC1).
     *
     * @param nOfcaller - number of callers.
     * @param nOfworker - number of workers
     */
    CC1(int nOfcaller, int nOfworker) {
        while (choice.size() < 3) {
            int num = random.nextInt(3); // initialize the queue choices.
            if (!choice.contains(num)) {
                choice.add(num);
            }
        }
        // create Worker and Caller threads.
        for (int j = 1; j <= nOfworker; j++) {
            (new Worker(j, choice.get(j - 1))).start();
        }
        for (int i = 1; i <= nOfcaller; i++) {
            try {
                Thread.sleep(random.nextInt(251)); // random interval to call (0-250ms).
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            (new Caller(i)).start();
        }
    }

    /**
     * Caller class.
     */
    class Caller extends Thread {
        /* call number */
        private int call;

        /**
         * Constructor of Caller.
         *
         * @param call - call number.
         */
        Caller(int call) {
            this.call = call;
        }

        @Override
        public void run() {
            int q = random.nextInt(3); // random call a queue.
            if (q == 0) {
                A.append(call);
            } else if (q == 1) {
                B.append(call);
            } else if (q == 2) {
                C.append(call);
            }
        }
    }

    /**
     * Worker class.
     */
    class Worker extends Thread {
        /* worker number */
        private int No;
        /* selected queue */
        private int Q;
        /* service queue */
        private Buffer service;

        /**
         * Constructor of worker.
         *
         * @param No - worker number.
         * @param Q  - selected queue.
         */
        Worker(int No, int Q) {
            this.No = No;
            this.Q = Q;
            // initialize selected queues by random number.
            if (Q == 0) {
                Event.WorkerChoosesQueue(No, 'A');
                this.service = A;
                this.service.setWorkerNo(No);
            } else if (Q == 1) {
                Event.WorkerChoosesQueue(No, 'B');
                this.service = B;
                this.service.setWorkerNo(No);
            } else if (Q == 2) {
                Event.WorkerChoosesQueue(No, 'C');
                this.service = C;
                this.service.setWorkerNo(No);
            }
        }

        /**
         * terminate the program in a normal way.
         * (make them jump from wait() and continue executing to end),
         * not force to exit/stop them.
         */
        private void terminate() {
            if (!A.stop && !B.stop && !C.stop) {
                A.cancel(); // cancel the tasks.
                B.cancel();
                C.cancel();
                if (printed == 0 && !end) {
                    end = true;
                    printed++;
                    Event.AllCallsAnswered();
                }
            }
        }

        @Override
        public void run() {
            while ((A.callOut+B.callOut+C.callOut) < 25) {
                    try {
                    service.answer(No); // answer a call.
                    // be stolen (others steal from here).
                    if (service.callNo > 0) {
                        if (A.callNo == 0) {
                            service.beStolen(A);
                        } else if (B.callNo == 0) {
                            service.beStolen(B);
                        } else if (C.callNo == 0) {
                            service.beStolen(C);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            terminate(); // terminate the program when have answered 25 calls.
        }
    }
}

