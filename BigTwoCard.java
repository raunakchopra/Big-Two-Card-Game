
/**
 * The BigTwoCard class is a subclass of the Card class, and is used to model a card used in a Big Two card game.
 * It overrides the compareTo() method it inherited from the Card class to reflect the ordering of cards
 * used in a Big Two card game.
 *
 * @author Raunak Chopra
 *
 */
public class BigTwoCard extends Card{

    /**
     * Creates an instance of BigTwoCard. Invokes the constructor function of the Card class using super.
     *
     * @param suit
     *            an int value between 0 and 3 representing the suit of a card:
     *            <p>
     *            0 = Diamond, 1 = Club, 2 = Heart, 3 = Spade
     * @param rank
     *            an int value between 0 and 12 representing the rank of a card:
     *            <p>
     *            0 = 'A', 1 = '2', 2 = '3', ..., 8 = '9', 9 = '10', 10 = 'J', 11
     *            = 'Q', 12 = 'K'
     */

    public BigTwoCard(int suit, int rank){
        super(suit,rank);
    }

    /**
     * Overriden function from the Card Class, so as to make adjustments in the rank and compare accordingly
     * @param card
     *            the card to be compared
     * @return integer value to interpret the result.
     */
    public int compareTo(Card card) {
      int currentSuit = this.suit;
      int currentRank = this.rank;
      int compareRank = card.rank;
      int compareSuit = card.suit;

      if(currentRank == 0 || currentRank == 1){
          currentRank += 13;
      }

      if(compareRank == 0 || compareRank == 1){
          compareRank += 13;
      }

      if(currentRank > compareRank){
          return 1;
      }
      else if(compareRank > currentRank){
          return -1;
      }
      else if(currentRank == compareRank){
          if(compareSuit < currentSuit){
              return 1;
          }
          else return -1;
      }
      else return 0;
    }

}
