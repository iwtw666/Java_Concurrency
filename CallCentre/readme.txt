/* readme.txt */

Code is following the guide of CallCentre specification.
Using modified 'Producer-consumer algorithm' to realize the two programs.
In random 0-0.25s, a call will be appended, and workers try to handle them (about 0.5s for each).
If worked queue is empty, worker can also steal calls from others (if not empty)
.
Run by main() methods in files 'CallCentre1' and 'CallCentre2',
code details in 'CC1' and 'CC2' correspondingly. Both programs can stop normally (exit code 0).

CallCentre1: Array('[]') as buffer (circular buffer), and can read from both sides. Use only synchronized,
             wait()/notify()/notifyAll(), and not use classes in java.util.concurrent.
CallCentre2: LinkedList('Deque') as buffer (not circular), and can read from both sides.
             Use classes in java.util.concurrent, such as Semaphore, TimeUnit classes.

Classes:
(1) CallCentre1:
Buffer - the circular buffer (array) class, has methods append, answer and beStolen,
         which all operate the same buffer (in synchronized ways), like append calls to this buffer
         or answer calls in this buffer. 3 Buffers (A, B, C) are created in CC1 initially.
CC1 - the main implementation of 'CallCentre1', contains classes Caller and Worker.
      Create threads (Callers and Workers) and run them.
Caller - trigger append a call to a buffer randomly. Based on producer-consumer algorithm's producer.
Worker - trigger answer or beStolen a call in worked buffer. Workers select worked queue randomly at beginning.
         Based on modified producer-consumer algorithm's consumer and stealer.
CallCentre1 - a main class to instantiate CC1 by 25 Callers and 3 Workers.

Buffer and Worker can call methods in Event class when relevant events happen,
where Buffer calls "CallAppendedToQueue", "WorkerAnswersCall" and "WorkerStealsCall",
while Worker calls "WorkerChoosesQueue" and "AllCallsAnswered".

(2) CallCentre2:
CC2 - the main implementation of 'CallCentre2', contains classes DequeBuffer, Caller and Worker.
      Create threads (Callers and Workers) and run them.
DequeBuffer - LinkedList class (Deque), can be adjusted in both sides (addFirst/addLast or removeFirst/removeLast).
              Contains semaphores related to it. 3 DequeBuffers (A, B, C) are created in CC2 initially.
Caller - contains 'append' method, can append a call to a LinkedList randomly.
         Based on producer-consumer algorithm's producer.
Worker - contains 'answer', 'steal' methods, can answer or steal calls in worked LinkedList.
         Workers select worked LinkedList at start.
         Based on modified producer-consumer algorithm's consumer and stealer.
CallCentre2 - a main class to instantiate CC2 by 25 Callers and 3 Workers.

Caller and Worker can call methods in Event class when relevant events happen, where Caller calls "CallAppendedToQueue",
while Worker calls "WorkerChoosesQueue", "WorkerAnswersCall" and "WorkerStealsCall" and "AllCallsAnswered".
