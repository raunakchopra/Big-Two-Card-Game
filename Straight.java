/**
 *  This class is a subclass of the Hand class, and are used to model a hand of Straight.
 *  It overrides getTopCard,isValid and getType method that it inherits from Hand class.
 *
 * @author Raunak Chopra
 */
public class Straight extends Hand {

    /**
     * Constructor for the Striaght type hand. Calls the constructor of Hand superclass.
     *
     * @param player Player who plays the hand
     * @param cards  List of cards played by the player
     */

    public Straight(CardGamePlayer player, CardList cards){
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
        return "Straight";
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
        boolean flag = true;
        int cardRefRank = getCard(0).rank;

        for(int i=1; i<5;i++){
            int compareRank = getCard(i).rank;

            if(compareRank == 0 || compareRank == 1){
                compareRank += 13;
            }

            if(cardRefRank == 0 || cardRefRank == 1){
                cardRefRank += 13;
            }

            if(cardRefRank != compareRank - 1){
                flag = false;
                break;
            }
            else cardRefRank = getCard(i).rank;
        }

        if(flag == true){
            return true;
        }
        return false;
    }
}
