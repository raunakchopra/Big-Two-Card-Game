/**
 *  This class is a subclass of the Hand class, and are used to model a hand of Flush.
 *  It overrides getTopCard,isValid and getType method that it inherits from Hand class.
 *
 * @author Raunak Chopra
 */
public class Flush extends Hand {

    /**
     * Constructor for the Flush type hand. Calls the constructor of Hand superclass.
     *
     * @param player Player who plays the hand
     * @param cards  List of cards played by the player
     */

    public Flush(CardGamePlayer player, CardList cards){
        super(player, cards);
    }

    /**
     * This method returns the top card of the hand played
     * @return top card of the hand
     */
    public Card getTopCard(){
        sort();
        return getCard(4);
    }

    /**
     * Returns type of Hand
     * @return String value of the type of hand
     */

    public String getType(){
        return "Flush";
    }

    /**
     *  Checks if the hand is valid or not
     * @return boolean value, true if its valid, else false
     */
    public boolean isValid(){
        if(size() != 5){
            return false;
        }
        boolean flag = true;
        int suitCheck = getCard(0).suit;

        for(int i=1; i<5;i++){
            if(getCard(i).suit != suitCheck){
                flag = false;
                break;
            }
        }

        if(flag == true){
            return true;
        }
        return false;
    }
}
