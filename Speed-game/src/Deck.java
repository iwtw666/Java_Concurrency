import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A card deck class for each required class.
 */
public class Deck {

    // initial the card deck
    private List<Card> deck = new ArrayList<Card>();

    /**
     * constructor to create a deck include n cards.
     * @param n - the number of cards.
     */
    public Deck(int n) {
        for(int i=1; i <= n; i++) {
            deck.add(new Card(new Random()));
        }
    }

    /**
     * Get the deck as a list.
     * @return - deck as an arraylist.
     */
    public List<Card> getDeck() {
        return this.deck;
    }
}
