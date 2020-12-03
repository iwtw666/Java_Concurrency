import java.util.Random;

/** Process P based on Peterson's algorithm. */
public class P extends Thread {
	/* Global shared variables. */
	Global g;
	/* No. of cards in hand is 3 initial. */
	private Deck handDeck = new Deck(3);
	/* No. of deck off hand is 26 (29-3) initial. */
	private int deckSize = 26;

	/**
	 * Constructor of P.
	 * @param g - global variables class.
	 */
	public P(Global g) {
		this.g = g;	
	}

	/**
	 * Get the cards in hand.
	 * @return - handDeck.
	 */
	public Deck getHand() {
		return this.handDeck;
	}

	/**
	 * Place the matched card and draw a new card from the deck.
	 * @param d - the matched card.
	 * @param index - the index of matched card in pile.
	 */
	private void placeNpick(Card d, int index) {
		this.handDeck.getDeck().remove(d);
		g.pile.getDeck().add(d); // place card to pile.
		g.pile.getDeck().remove(index); // replace the previous pile card.
		Event.PlayerPlacesCard(1,index + 1, d);
		if (this.deckSize > 0) {
			Card newCard = new Card(new Random()); // draw a new card, if deck has leftovers.
			this.handDeck.getDeck().add(newCard);
			this.deckSize--; // left cards - 1
			Event.PlayerPicksNewCard(1, newCard);
		}
	}

	public void run() {
		while (this.handDeck.getDeck().size() > 0) {
			if (g.pdraw) {
				return; // check 'draw' condition.
			}
			/* Non-critical section */
			g.wantp = true;
			g.last = 1;
			while (!(!g.wantq || g.last == 2)) {
				// await wantq = 1 or last =2
			}
			/* Critical section */
//			g.critical++;
			if (g.qwin || g.qdraw) {
				return; // if q win or found draw, stop following code.
			}
			// compare all cards in hand with the pile.
			for (Card d : this.handDeck.getDeck()) {
				if (d.matches(g.pile.getDeck().get(0))) {
					placeNpick(d, 0);
					g.pthink = 0; // set stuck condition to 0.
					break;
				} else if (d.matches(g.pile.getDeck().get(1))) {
					placeNpick(d, 1);
					g.pthink = 0; // set stuck condition to 0.
					break;
				} else {
					g.pthink++; // stuck count +1.
				}
			}
//			g.critical--;
			if (this.handDeck.getDeck().size() == 0) {
				g.pwin = true; // empty cards in hands means win.
			}
			if (g.pthink >= 3 && g.qthink >= 3) {
				g.pdraw = true; // 3 times to ensure to traverse all cards in hand.
				Event.GameEndsAsDraw();
			}
			g.wantp = false;
		}
		if (g.qwin) {
		} else {
			Event.PlayerWins(1); //check win condition.
		}
	}
}
