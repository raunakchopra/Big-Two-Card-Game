/**
 * The Hand class is a subclass of the CardList class, and is used to model a hand of cards. It
 * has a private instance variable for storing the player who plays this hand. It also has methods
 * for getting the player of this hand, checking if it is a valid hand, getting the type of this hand,
 * getting the top card of this hand, and checking if it beats a specified hand.
 *
 * @author Raunak Chopra
 */
public abstract class Hand extends CardList {
    /*
    Player who is going to play the hand
     */
    private  CardGamePlayer player;

    /**
     * Constructor function to create the instance of the class
     * @param player player who plays the card
     * @param cards list of cards played by the player
     */
    public Hand(CardGamePlayer player, CardList cards){

        this.player = new CardGamePlayer();
        this.player = player;

        for(int i=0; i<cards.size();i++){
            addCard(cards.getCard(i));  //adding cards
        }
    }

    /**
     * function to return the player
     * @return player who is playing the game
     */

    public CardGamePlayer getPlayer() {
        return player;
    }

    /**
     * Method to return the top card of the hand
     * @return top card of the hand
     */

    public Card getTopCard(){
        if(size() == 1){
            return this.getCard(0);
        }

        if(size() == 2){
            sort();
            return this.getCard(1);
        }

        if(size() == 3){
            sort();
            return this.getCard(2);
        }
        return null;
    }

    /**
     * Checks if the hand played  beats the other hand
     * checks for all the types of hands
     * @param hand hand to be compared
     * @return boolean value, true if the current card beats, else false
     */
    public boolean beats(Hand hand){
        if(size() == hand.size() && size() != 5){
            if(getTopCard().compareTo(hand.getTopCard()) == 1){
                return true;
            }
        }
        if(size() == hand.size() && size() == 5){
            if(getType() == hand.getType()){
                if(getTopCard().compareTo(hand.getTopCard()) == 1){
                    return true;
                }
                return false;
            }
            else {
                String[] handLevel = {"Straight", "Flush", "FullHouse", "Quad", "StraightFlush"};

                int currentHandLevel = -1;
                int compareHandLevel = -1;

                for(int i=0; i<5;i++){
                    if(handLevel[i] == getType()){
                        currentHandLevel = i;
                    }
                    if(handLevel[i] == hand.getType()){
                        compareHandLevel = i;
                    }
                }

                if(currentHandLevel > compareHandLevel){
                    return true;
                }
                else {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * to return the type of hand
     * @return String value with the name of hand
     */

    public abstract String getType();

    /**
     * Checks if the hand played is valid or not
     * @return boolean value, true if valid, false if invalid
     */
    public abstract boolean isValid();

}
