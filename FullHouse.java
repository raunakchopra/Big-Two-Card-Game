/**
 *  This class is a subclass of the Hand class, and are used to model a hand of Full House.
 *  It overrides getTopCard,isValid and getType method that it inherits from Hand class.
 *
 * @author Raunak Chopra
 */
public class FullHouse extends Hand {

    /**
     * Constructor for the FullHouse type hand. Calls the constructor of Hand superclass.
     *
     * @param player Player who plays the hand
     * @param cards  List of cards played by the player
     */

    public FullHouse(CardGamePlayer player, CardList cards){
        super(player, cards);
    }

    /**
     * This method returns the top card of the hand played
     * @return top card of the hand
     */
    public Card getTopCard(){
        sort();
        return this.getCard(2);
    }


    /**
     * Returns type of Hand
     * @return String value of the type of hand
     */
    public String getType(){
        return "FullHouse";
    }

    /**
     *  Checks if the hand is valid or not
     * @return boolean value, true if its valid, else false
     */

    public boolean isValid(){
        if(size() != 5){
            return false;
        }
        sort();

        if(getCard(0).rank == getCard(1).rank && getCard(1).rank == getCard(2).rank && getCard(3).rank == getCard(4).rank){
            return true;
        }
        else if(getCard(0).rank == getCard(1).rank && getCard(2).rank == getCard(3).rank && getCard(3).rank == getCard(4).rank){
            return true;
        }
        return false;
    }
}
