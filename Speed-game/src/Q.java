import java.util.Random;

/** Process Q based on Peterson's algorithm. */
public class Q extends Thread {
	/* Global shared variables. */
	Global g;
	/* No. of cards in hand is 3 initial. */
	private Deck handDeck = new Deck(3);
	/* No. of deck off hand is 26 (29-3) initial. */
	private int deckSize = 26;

	/**
	 * Constructor of Q.
	 * @param g - global variables class.
	 */
	public Q(Global g) {
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
		g.pile.getDeck().add(d); // place a card to pile.
		g.pile.getDeck().remove(index); // replace the previous one.
		Event.PlayerPlacesCard(2,index + 1, d);
		if (this.deckSize > 0) {
			Card newCard = new Card(new Random());
			this.handDeck.getDeck().add(newCard);
			this.deckSize--; // if there are left cards, draw 1 card after place.
			Event.PlayerPicksNewCard(2, newCard);
		}
	}

	/**
	 * main run() function of the process.
	 */
	public void run() {
		while (this.handDeck.getDeck().size() > 0) {
			if (g.qdraw) {
				return; // check 'draw' condition.
			}
			/* Non-critical section */
			g.wantq = true;
			g.last = 2;
			while (!(!g.wantp || g.last == 1)) {
				// await wantp = false or last = 1
			}
			/* Critical section */
//			g.critical++;
			if (g.pwin || g.pdraw) {
				return; // if p win or found draw, cannot think or place cards.
			}
			// compare all cards in hand with the pile (place if matched, else stuck count +1).
			for (Card d : this.handDeck.getDeck()) {
				if (d.matches(g.pile.getDeck().get(0))) {
					placeNpick(d, 0);
					g.qthink = 0;
					break;
				} else if (d.matches(g.pile.getDeck().get(1))) {
					placeNpick(d, 1);
					g.qthink = 0; // if matched success, stuck set to 0.
					break;
				} else {
					g.qthink++; // stuck count +1.
				}
			}
//			System.out.println(g.critical);
//			g.critical--;
			if (this.handDeck.getDeck().size() == 0) {
				g.qwin = true; // empty cards in hand means win.
			}
			if (g.pthink >= 3 && g.qthink >= 3) {
				g.qdraw = true; // 3 times to ensure to traverse all cards in hand.
				Event.GameEndsAsDraw();
			}
			g.wantq = false;
		}
		if (g.pwin) {
		} else {
			Event.PlayerWins(2); // out of the loop means win.
		}
	}
}
