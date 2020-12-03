
/**
 * Speed game session, run by main().
 */
public class Speed {

    public static void main(String[] args) {
        Global g = new Global();
        Thread player1 = new P(g);
        Thread player2 = new Q(g);
        player1.start();
        player2.start();

        // for comparing the difference.
//        player2.start();
//        player1.start();
    }
}
