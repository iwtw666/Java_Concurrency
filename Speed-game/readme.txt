/* readme.txt */

Code is following the guide of Speed game.
Using Peterson's algorithm to realize the mutex between two Speed players.
Many player's activities like draw or place a card are in the critical session of each process.
These can prevent two players from conflicting.

In order to call the Event class, some check actions are in the beginning or end of each process.
Such as the draw checks are in the loop at the beginning, and the win checks are out of loop at the end.
Moreover, some other draw and win checks are before the matching, place & draw card activities to prevent double win.

Class:
P (Thread)- one of the Peterson's algorithm process, as a player here.
Q (Thread)- another player(process) similar as P, according to symmetry.
  Also, P & Q simulate the players' actions, like draw, place cards.
  These two can call Event class additionally.
Global - a class to save all global variables, like the wanp, wantq and last to define Peterson's algorithm.
  And some global variables used for the Speed game, like pwin, qwin, pthink, qthink ......
Deck - a class to call Card class to make a card deck, which can be used as left card deck, pile and cards in hand.
Speed - a main class to instantiate P,Q and Global to run these two thread.


