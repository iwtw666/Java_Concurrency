
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * CallCenter2(CC2) class, the main implementation of 'CallCentre2'.
 * In random 0-0.25s, a call will be appended, and the workers handle a call for about 0.5s.
 * "not want to make it static in the main() of CallCentre2,
 * so create a new file to implement this class".
 */
public class CC2 {
    /* Size of the queue */
    final int N = 5;
    /* Queues (buffers) */
    DequeBuffer A = new DequeBuffer('A');
    DequeBuffer B = new DequeBuffer('B');
    DequeBuffer C = new DequeBuffer('C');
    /* random and queues choice */
    Random random = new Random();
    ArrayList<Integer> choice = new ArrayList<>();
    /* number of answered calls totally */
    volatile int callOut = 0;
    /* end flags */
    boolean end = false;
    int printed = 0;

    /**
     * CallCenter2(CC2) constructor.
     * @param nOfcaller - # Callers.
     * @param nOfworker - # Workers.
     */
    public CC2(int nOfcaller, int nOfworker) {
        while (choice.size() < 3) {
            int num = random.nextInt(3); // initialize the queue choices.
            if (!choice.contains(num)) {
                choice.add(num);
            }
        }
        // create Caller and Worker threads.
        for (int j = 1; j <= nOfworker; j++) {
            (new Worker(j, choice.get(j - 1))).start();
        }
        for (int i = 1; i <= nOfcaller; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(251)); // random interval to call (0-250ms).
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            (new Caller(i)).start();
        }
    }

    /**
     * terminate the program in a normal way.
     * (make all semaphores release and make all code continue executing to end),
     * instead of forcing to exit/stop them.
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

    /**
     * DequeBuffer class, use deque buffer fot CallCentre2.
     */
    class DequeBuffer {
        /* buffer name/number */
        char name;
        /* worker number for the buffer */
        int workNo;
        /* buffer's main storage */
        Deque<Integer> deque;
        /* Semaphores */
        Semaphore notFull; // # empty in buffer.
        Semaphore notEmpty; // # element in buffer.
        Semaphore mutex; // call and steal mutex.
        /* stop flag */
        boolean stop = false;

        /**
         * Constructor of deque buffer.
         * @param name - buffer name/number.
         */
        public DequeBuffer(char name) {
            this.name = name;
            this.deque = new LinkedList<>();
            this.notFull = new Semaphore(N);
            this.notEmpty = new Semaphore(0);
            this.mutex = new Semaphore(1);
        }

        /**
         * set stop flags to true and release semaphores.
         */
        private void cancel(){
            stop = true;
            this.notEmpty.release();
            this.mutex.release();
//            this.notFull1.release(); // maybe no need.
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
         * @param call - call number.
         */
        public Caller(int call) {
            this.call = call;
        }

        /**
         * Append a cal.
         * @param deque - which deque to add.
         * @param call - call number.
         */
        private void add(DequeBuffer deque, int call) {
            try {
                deque.notFull.acquire();
                deque.mutex.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Event.CallAppendedToQueue(call, deque.name);
            deque.deque.add(call);
            deque.mutex.release();
            deque.notEmpty.release();
        }

        @Override
        public void run() {
            int q = random.nextInt(3); // random call a deque.
            if (q == 0) {
                add(A, call);
            } else if (q == 1) {
                add(B, call);
            } else if (q == 2) {
                add(C, call);
            }
        }
    }

    /**
     * Worker class.
     */
    class Worker extends Thread {
        /* worker number */
        private int No;
        /* random choice number */
        private int Q;
        /* worker-selected deque/queue (service) */
        private DequeBuffer service;

        /**
         * Add worker to selected queue.
         * @param No - worker number.
         * @param deque - selected queue/deque.
         */
        private void addWorker(int No, DequeBuffer deque){
            Event.WorkerChoosesQueue(No, deque.name);
            this.service = deque;
            this.service.workNo = No;
        }

        /**
         * Constructor of Worker.
         * Workers select queues by random numbers.
         * @param number - worker number.
         * @param Q - a random choice number.
         */
        public Worker(int number, int Q) {
            this.No = number;
            this.Q = Q;
            // initialize selected queues by random number.
            if (Q == 0) {
                addWorker(No, A);
            } else if (Q == 1) {
                addWorker(No, B);
            } else if (Q == 2) {
                addWorker(No, C);
            }
        }

        /**
         * Stealer steals elements from here (this service).
         * @param db - the stealer's deque.
         * @throws InterruptedException - thread interrupt errors.
         */
        private void steal(DequeBuffer db) throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(501); // 0.501s to handle a call(steal from here).
            if (db.deque.size() == 0) {
                service.mutex.acquire();
                if (service.stop) {
                    return; // terminate if stop flag is true.
                }
                Event.WorkerStealsCall(db.workNo, service.deque.removeLast(), service.name);
                callOut++;
                service.mutex.release();
            }
        }

        @Override
        public void run() {
            while (callOut < 25) {
                try {
                    service.notEmpty.acquire();
                    // ensure it can terminate when 'stop flag' is true.
                    if (service.stop) {
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // answer a call.
                try {
                    if (!service.deque.isEmpty()) {
                        Event.WorkerAnswersCall(No, service.deque.remove());
                        callOut++;
                        TimeUnit.MILLISECONDS.sleep(500); // 0.5s to answer a call.
                    }
                    // try to be stolen a call (others steal from here).
                    if (service.deque.size() > 0) {
                        if (A.deque.size() == 0) {
                            steal(A);
                        } else if (B.deque.size() == 0) {
                            steal(B);
                        } else if (C.deque.size() == 0) {
                            steal(C);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                service.notFull.release();
            }
            terminate(); // terminate if answered 25 calls.
        }
    }
}

