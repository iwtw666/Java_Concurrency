
/**
 * Class Event
 * DO NOT MODIFY THIS CLASS
 * 
 * This class is used to log and print all events happening during the game
 * 
 * NOTE : Please make sure that you have called EVERY event method below in your main program whenever such an event happens.
 * This class will be replaced with a similar class that will validate the game flow automatically.
 *
 */
public class Event {
	
	/**
	 * Call this event method when a player places a card onto a pile
	 * 
	 * @param player int (either 1 or 2) representing which player has played the card
	 * @param pile int (either 1 or 2) representing to which pile the card has been added to
	 * @param card Card the card being played
	 *
	 */
	public static void PlayerPlacesCard(int player, int pile, Card card){
		System.out.println("Player #"+player+" has played "+card+" into pile # "+pile);
	}
	
	
	/**
	 * Call this event method when either player wins and the game ends
	 * 
	 * @param player int (either 1 or 2) representing which player has won
	 *
	 */
	public static void PlayerWins(int player){
		System.out.println("Game Over: Player #"+player+" has won !");
	}
	
	/**
	 * Call this event method when a player draws a new random card from his deck
	 * 
	 * @param player int (either 1 or 2) representing which player
	 * @param card Card the card that has been drawn from the deck
	 *
	 */
	public static void PlayerPicksNewCard(int player, Card card){
		System.out.println("Player #"+player+" has picked "+card);
	}
	
	/**
	 * Call this event method when the game ends as a draw (when neither of the 2 players can play anymore)
	 *
	 */
	public static void GameEndsAsDraw(){
		System.out.println("Game Over: It's a draw!");
	}

}
