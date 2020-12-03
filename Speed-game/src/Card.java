import java.util.Random;

/**
 * Class Card
 * DO NOT MODIFY THIS CLASS
 * 
 * This class represents a playing card and provides methods for card compatibility & comparison.
 * This is used to communicate with the provided Event Class.
 *
 */
public class Card {

	public enum Colour {
		RED, GREEN, BLUE, YELLOW, PURPLE, BLACK
	}
	public enum Shape {
		CIRLCE, SQUARE, TRIANGLE, DIAMOND, STAR, HEX
	}
	
	private Shape shape;
	private Colour colour;
	private int motifs;
	
	/**
	 * Instantiates a random card.
	 * 
	 * @param random the random number generator
	 */
	public Card(Random random) {
		this.shape = Shape.values()[random.nextInt(6)];
		this.colour = Colour.values()[random.nextInt(6)];
		this.motifs = random.nextInt(5);
	}
	
	/**
	 * Instantiates a new card.
	 * 
	 * @param shape the shape
	 * @param colour the colour
	 * @param motifs the number of motifs
	 */
	public Card(Shape shape, Colour colour, int motifs){
		this.shape = shape;
		this.colour = colour;
		this.motifs = motifs;
	}

	/**
	 * Gets the shape.
	 * 
	 * @return the shape
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Gets the colour.
	 * 
	 * @return the colour
	 */
	public Colour getColour() {
		return colour;
	}

	/**
	 * Gets the number of motifs.
	 * 
	 * @return the number of motifs
	 */
	public int getMotifs() {
		return motifs;
	}	

	/**
	 * Checks if two cards match in any of the three variables
	 * 
	 * @param card the card
	 * @return true, if a match occurs
	 */
	public boolean matches(Card card) {
		return (card.colour == this.colour || 
				card.shape == this.shape ||
				card.motifs == this.motifs);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return motifs + " " + colour.toString() + " " + shape.toString();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Card) {
			Card card = (Card) o;
			if (card.getColour() == this.colour
					&& card.getMotifs() == this.motifs
					&& card.getShape() == this.shape) {
				return true;
			}
		}
		return false;
	}
	

}
