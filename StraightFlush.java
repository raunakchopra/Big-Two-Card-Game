/**
 *  This class is a subclass of the Hand class, and are used to model a hand of Straight Flush
 *  It overrides getTopCard,isValid and getType method that it inherits from Hand class.
 *
 * @author Raunak Chopra
 */
public class StraightFlush extends Hand {

    /**
     * Constructor for the StraightFlush type hand. Calls the constructor of Hand superclass.
     *
     * @param player Player who plays the hand
     * @param cards  List of cards played by the player
     */

    public StraightFlush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }


    /**
     * This method returns the top card of the hand played
     * @return top card of the hand
     */
    public Card getTopCard() {
        sort();
        return getCard(4);
    }

    /**
     * Returns type of Hand
     * @return String value of the type of hand
     */
    @Override
    public String getType() {
        return "StraightFlush";
    }

    /**
     *  Checks if the hand is valid or not
     * @return boolean value, true if its valid, else false
     */

    public boolean isValid() {
        if(size() != 5){
            return false;
        }
        sort();

        boolean flushChecked = flushCheck();
        boolean striaghtChecked = straightCheck();

        return flushChecked && striaghtChecked;
    }

    /**
     * Check if the hand is Straight hand also or not
     * @return boolean value, true if the hand is Straight , else false
     */

    boolean straightCheck(){
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

    /**
     * Checks if the hand is Flush hand also or not
     * @return boolean value, true if the hand is Straight , else false
     */

    boolean flushCheck() {
        int suitCheck = getCard(0).suit;

        boolean flag = true;

        for (int i = 1; i < 5; i++) {
            if (suitCheck != getCard(i).suit) {
                return false;
            }
        }
        return flag;
    }

}
