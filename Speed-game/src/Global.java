
/** Global variables for Peterson's algorithm and Speed game */
public class Global {
    /* Number of processes currently in critical section */
    volatile int critical = 0;
    /* Process p wants to enter critical section */
    volatile boolean wantp = false;
    /* Process q wants to enter critical section */    
    volatile boolean wantq = false;
    /* Which process's last (turn) it is */
    volatile int last = 1;
    /* pile cards initially */
    volatile Deck pile = new Deck(2);
    /* player1 win */
    volatile boolean pwin = false;
    /* player2 win */
    volatile boolean qwin = false;
    /* player1 wait */
    volatile int pthink = 0;
    /* player2 wait */
    volatile int qthink = 0;
    /* process p finds 'draw condition' firstly */
    volatile boolean pdraw = false;
    /* process p finds 'draw condition' firstly */
    volatile boolean qdraw = false;
}
