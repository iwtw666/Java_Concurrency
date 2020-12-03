
/*
  Prove mutex by an assertion (critical <= 1) in q():
    1. When p() or q() in critical section, 'critical' + 1.
    2. When p() or q() exit critical section, 'critical' - 1.
    3. The assertion can check whether there is only one process in critical section.
    If two processes are all in critical section, the assertion can throw an error.
  Prove starvation freedom in p() by an LTL property 'nostarve':
    1. The 'nostarve' try to get 'p() always eventually progresses to critical section'.
    2. When the algorithm executed, the 'nostarve' should be always right eventually.
    If 'nostarve' is wrong, spin can throw an error about this LTL property.
*/

/* main pml code*/
bool    wantp = false, wantq = false;
byte    last = 1;
byte critical = 0;
bool pcs = false;
ltl nostarve {[]<> pcs}

active proctype p() {                                                                                             
  do
  /* [non-critical section] */
  ::  wantp = true;
      last = 1;
      /* adjusted (wantq==false || last==2); */
      do
      :: wantq ->
         do
         :: (last == 1) -> break;
         :: else -> goto CS;
         od
      :: else -> goto CS;
      od
    /* critical section */
    CS:
      printf("p in CS\n");
      critical++;
      /* pcs: whether free of starvation */
      pcs = true;
      pcs = false;
      critical--;
      wantp = false
  od
}

active proctype q() {
  do
  /* [non-critical section] */
  ::  wantq = true;
      last = 2;
      /* adjusted (wantp==false || last==1); */
      do
      :: wantp ->
         do
         :: (last == 2) -> break;
         :: else -> goto CS;
         od
      :: else -> goto CS;
      od
    /* critical section */
    CS:
      printf("q in CS\n");
      critical++;
      /* an assertion for mutex */
      assert (critical <= 1);
      critical--;
      wantq = false
  od
}

/* 
   Test Results:
   1.Safety (only mutex test):
       Full statespace search for:
	 never claim         	 - (not selected)
	 assertion violations	 +
	 cycle checks       	 - (disabled by -DSAFETY)
	 invalid end states	 +

       State-vector 28 byte, depth reached 36, errors: 0
   2.Safety (mutex test with check 'nostarve'):
       Full statespace search for:
	 never claim         	 + (nostarve)
	 assertion violations	 + (if within scope of claim)
	 cycle checks       	 - (disabled by -DSAFETY)
	 invalid end states	 - (disabled by never claim)

       State-vector 36 byte, depth reached 73, errors: 0
   3.Liveness (starvation freedom):
       Full statespace search for:
	 never claim         	 + (nostarve)
	 assertion violations	 + (if within scope of claim)
	 acceptance   cycles 	 + (fairness enabled)
	 invalid end states	 - (disabled by never claim)

       State-vector 36 byte, depth reached 93, errors: 0
   
  According to the results above, the modified version is still correct.
*/