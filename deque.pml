/*
  Promela for Modified Producer-consumer Algorithm
*/
/* 
   Ensure synchronization by semaphores notFull, notEmpty and mutex.
   Simulate 10 rounds (total 10 products) for producing(d)-consuming(DI+D2) with a 
   -4-size buffer, which might spend much memory, plz wait it to finish.
   Verify Correctness by
     1. Count: number of elements in buffer actually,
       1.1 'assert (Count < 4)' in p to verify whether append when the buffer is full;
       1.2 'assert(Count > 0)' in q and r to verify whether take when the buffer is empty.
     2. lastAppend, lastTake: the last added element and the last taken element from the 
     -buffer, which make sure 'append' and 'take' execute in order.
     As d will auto increment 1 (++), so the appended element would '>(=)' the taken one:
       2.1 'assert (lastTake < lastAppend)' in p to verify whether the last(newest) added 
       -product value is greater than the last taken;
       2.2 'assert (lastTake <= lastAppend)' in q and r to verify whether the taken one
       -(in this process) value is less than or equal to the last(newest) added product.
     3. 'assert (mutex <= 1 && mutex >= 0)' in process r to check whether there is only one 
     -process(p or r) can adjust pointer 'in' and read/write the pointed slot in a time period.
*/

/* main pml code*/
/* define operations in semaphore */
inline wait(s) {
   atomic {s > 0;s--}
}
inline signal(s) {s++}
byte Buffer[4] = 0;    /* the buffer (size =4) */
byte in = 0, out = 0;    /* in and out pointers */
byte D1 =1 , D2 = 1;    /* count(s) of turns for consumers consuming */
/* semaphores */
byte notFull = 4, notEmpty = 0, mutex = 1;
/* verification variables */
byte Count = 0, lastAppend = 0, lastTake = 0;

active proctype p() {
   byte d = 1;    /* product (equals count of turns for producing) */
   do
   :: d > 10 -> break
   :: else
      printf("Appending %d\n", d);
      wait(notFull);
      wait(mutex);
      assert (Count < 4);   /* verfy not append when full */
      Buffer[in] = d;
      lastAppend = d;
      assert (lastTake < lastAppend);    /* verfication of p-c order */
      in = ( in + 1 ) % 4;
      Count++;
      signal(mutex);
      signal(notEmpty);
      d++
   od
}

active proctype q() {
   byte take1;
   do
   :: (D1+D2) > 10 -> break
   :: else
       wait(notEmpty);
       assert(Count > 0);    /* verfy not take when empty */
       take1 = Buffer[out];
       lastTake = take1;
       assert (lastTake <= lastAppend);    /* verfication of p-c order */
       out = ( out + 1 ) % 4;
       Count--;
       signal(notFull);
       printf("Taken %d\n", take1);
       D1++
   od
}

active proctype r() {
   byte take2;
   do
   :: (D1+D2) > 10 -> break
   :: else
       wait(notEmpty);
       wait(mutex);
       assert(Count > 0);    /* verfy not take when empty */
       in = ( in - 1 + 4 ) % 4;
       take2 = Buffer[in];
       lastTake = take2;
       assert (lastTake <= lastAppend);    /* verfication of p-c order */
       Count--;
       /* an assertion for mutex (p and r) */
       assert (mutex <= 1 && mutex >= 0);
       signal(mutex);       
       signal(notFull);
       printf("Taken %d\n", take2);
       D2++
   od
}

/* 
  Test Results:
   1. Safety:
     Full statespace search for:
	  never claim         	  - (not selected)
	  assertion violations	  +
	  cycle checks       	  - (disabled by -DSAFETY)
	  invalid end states	  +

     State-vector 44 byte, depth reached 273, errors: 0
   2. Liveness:
     Full statespace search for:
	  never claim         	  - (not selected)
	  assertion violations	  +
	  acceptance   cycles 	  - (not selected)
	  invalid end states	  +

     State-vector 44 byte, depth reached 273, errors: 0
   
  According to the results above, the modified p-c algorithm is correct.
*/
