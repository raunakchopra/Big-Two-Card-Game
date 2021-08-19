/**
 *  This class is a subclass of the Hand class, and are used to model a hand of Single.
 *  It overrides isValid and getType method that it inherits from Hand class.
 *
 * @author Raunak Chopra
 */
public class Single extends Hand {

    /**
     * Constructor for the Single type hand. Calls the constructor of Hand superclass.
     *
     * @param player Player who plays the hand
     * @param cards  List of cards played by the player
     */

    public Single(CardGamePlayer player, CardList cards){
        super(player, cards);
    }

    /**
     * Returns type of Hand
     * @return String value of the type of hand
     */

    public String getType(){
        return "Single";
    }

    /**
     *  Checks if the hand is valid or not
     * @return boolean value, true if its valid, else false
     */
    public boolean isValid(){
        if(this.size() == 1){
            return true;
        }
        else return false;
    }
}
