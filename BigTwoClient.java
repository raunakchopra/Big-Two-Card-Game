import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The BigTwoClient class is a public class to represent the BigTwoClient object and contains the overall flow of the BigTwo card game.
 *
 * @author Raunak Chopra
 */

public class BigTwoClient implements CardGame,NetworkGame{

    /**
     * A Deck of card for the game
     */
    private Deck deck;

    /**
     * Integer specifying number of Players
     */

    private int numOfPlayers;

    /**
     * A list of all players
     */
    private ArrayList<CardGamePlayer> playerList;

    /**
     * List of all hands on the table
     */
    private ArrayList<Hand> handsOnTable;

    /**
     * Integer specifying the index of current player
     */
    private int currentIdx;

    /**
     * Integer specifying the playerID (i.e., index) of the local player
     */
    private int playerID;

    /**
     * String specifying name of the player
     */
    private String playerName;

    /**
     * String specifying IP Address of the game server
     */
    private String serverIP;

    /**
     * Integer specifying TCP port of the game
     */
    private int serverPort;

    /**
     * Socket for connecting to the server
     */
    private Socket sock;

    /**
     * ObjectOutputStream for sending messages to the server
     */
    private ObjectOutputStream oos;

    /**
     * ObjectInputStream for recieving messages from the server
     */
    private ObjectInputStream ois;

    /**
     * BigTwoTable which builds GuI for the game
     */
    private BigTwoTable table;

    /**
     * Constructor function for creating a BigTwo card game.
     * The function creates 4 players and adds them to the list, creates a BigTwo table
     */
    public BigTwoClient(){
        playerList = new ArrayList<CardGamePlayer>();  //initializing the player list to store the player info
        handsOnTable = new ArrayList<Hand>();
        table = new BigTwoTable(this);

        for(int i=0;i<4; i++){
            CardGamePlayer p = new CardGamePlayer();
            playerList.add(p);
        }

        numOfPlayers = this.playerList.size();
        this.setPlayerName(JOptionPane.showInputDialog("Name:"));
        this.setServerPort(2396);
        this.setServerIP("127.0.0.1");
        makeConnection();
        table.disable();
        table.repaint();
    }

    /**
     * Function to return Deck of cards
     * @return Deck object
     */

    public Deck getDeck(){
        return deck;
    }

    /**
     * Function to return list of player
     * @return ArrayList
     */
    public ArrayList<CardGamePlayer> getPlayerList(){
        return playerList;
    }

    /**
     * Function to return hands on table
     * @return ArrayList having Hand objects
     */

    public ArrayList<Hand> getHandsOnTable(){
        return handsOnTable;
    }

    /**
     * Function to return number of players
     * @return Integer value for the number of the players
     */
    @Override
    public int getNumOfPlayers() {
        return playerList.size();
    }

    /**
     * Function to return the currentIdx
     * @return Integer value for currentIdx
     */
    public int getCurrentIdx(){
        return currentIdx;
    }

    /**
     * Function to remove cards from the players and the table
     */
    public void removeCards(){
        for(int i=0;i<4;i++){
            this.playerList.get(i).removeAllCards(); // removing all cards from all the players
        }

        handsOnTable = new ArrayList<Hand>(); // creating a new handsOnTable ArrayList so as to remove all the previous hands

    }

    /**
     * Function to distribute cards to all the 4 players who are going to play the game
     * @param deck
     *              Deck object which has all the cards
     */

    public void distributeCards(Deck deck){
        for(int i=0;i<4;i++){
            for(int j=0;j<13;j++){
                Card card = deck.getCard((i*13) + j);
                playerList.get(i).addCard(card);

                if(card.rank == 2 && card.suit == 0){ //checking for activePlayer
                    currentIdx = i;
                    table.setActivePlayer(i);
                }
            }
            playerList.get(i).sortCardsInHand();
        }
    }


    /**
     * Start Function, to start/restart the game
     * @param deck Deck of cards, used for the game
     */
    public void start(Deck deck){
        removeCards();
        distributeCards(deck);
        table.printMsg("Players ready");
        table.printMsg(playerList.get(currentIdx).getName() + "'s turn");
        table.repaint();
    }

    /**
     * Function for making any move in the game
     * @param playerID
     *            the playerID of the player who makes the move
     * @param cardIdx
     *           the indices of the cards played by the player
     */

    public void makeMove(int playerID, int[] cardIdx){
        sendMessage(new CardGameMessage(CardGameMessage.MOVE,-1,cardIdx));
    }

    /**
     * Function to change the active player
     */

    public void changeActivePlayer(){
        currentIdx = (currentIdx +1)%4;
        table.setActivePlayer(currentIdx);
    }


    /**
     * Function to check the move made by the player
     * @param playerID
     *            the playerID of the player who makes the move
     * @param cardIdx
     *              the indices of the cards played by the player
     */
    public void checkMove(int playerID, int[] cardIdx) {
        CardList cardList = new CardList(); //creating a cardList object to hold cards played by the player
        boolean isValidMove = true; //to check if the move is valid or not
        String prevPlayer;

        //logic

        if(handsOnTable.isEmpty()){
            prevPlayer = "";
        }
        else{
            Hand lastHandPlayed = handsOnTable.get(handsOnTable.size() - 1);
            prevPlayer = lastHandPlayed.getPlayer().getName();
        }

        if(cardIdx == null) {
            if(handsOnTable.get(handsOnTable.size()-1).getPlayer() != playerList.get(playerID) && handsOnTable.isEmpty() == false){
                //legal pass
                changeActivePlayer(); //changing active player
                table.printMsg("{Pass}");
                table.printMsg(playerList.get(playerID).getName() + "'s turn");
            }
            else{
                //illegal move
                table.printMsg("Not a legal move!!!");
                isValidMove = false;
            }
        }

        else if(cardIdx != null) {
            cardList = playerList.get(playerID).play(cardIdx);
            Hand handPlayed = composeHand(playerList.get(playerID), cardList);

            if (handsOnTable.isEmpty()) {
                if (handPlayed.contains(new Card(0, 2)) && handPlayed.isValid()) {
                    isValidMove = true;
                } else {
                    isValidMove = false;
                }
            } else {
                if (playerList.get(playerID).getName() != handsOnTable.get(handsOnTable.size() -1).getPlayer().getName()){
                    if (handPlayed != null) {
                        Hand prevHand = handsOnTable.get(handsOnTable.size() - 1);
                        isValidMove = handPlayed.beats(prevHand);
                    } else {
                        isValidMove = false;
                    }
                } else if (handPlayed != null) {
                    isValidMove = true;
                } else {
                    isValidMove = false;
                }
            }

            if (isValidMove && handPlayed.isValid()) {
                for (int i = 0; i < cardList.size(); i++) {
                    playerList.get(playerID).getCardsInHand().removeCard(cardList.getCard(i)); // removing the played cards
                }
                table.printMsg("{" + handPlayed.getType() + "}" + handPlayed); //printing message
                handsOnTable.add(handPlayed); //adding the hand played to the table
                changeActivePlayer(); //calling another function to change the activePlayer
                table.printMsg(playerList.get(currentIdx).getName() + "'s turn");
            }
            else {
                table.printMsg(cardList.toString() + " <= Not a legal move!!!");
            }
        }

        if(endOfGame()){
            table.repaint();
            table.printMsg("Game ends");
            for(int i = 0; i < playerList.size();i++)
            {
                if(playerList.get(i).getCardsInHand().size() == 0) {
                    table.printMsg(playerList.get(i).getName() + " wins the game");
                }
                else {
                    table.printMsg(playerList.get(i).getName() + " has " + playerList.get(i).getCardsInHand().size() + " cards in hand"); // cards left with other players
                }
            }
            table.disable();
            sendMessage(new CardGameMessage(CardGameMessage.READY,-1,null));
        }
    }

    /**
     * Function to check if the game has ended or not
     * @return  boolean value, true if game has ended else false
     */

    public boolean endOfGame(){ //to check if the game has end or not
        for(int i=0;i<4;i++){
            if(playerList.get(i).getCardsInHand().isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Method for returning a valid hand from the specified list if cards of the player, null if no valid hand can be composed
     * @param player
     *              CardGamePlayer object, the player who played the hand
     *
     * @param cards
     *              CardList Object, the cards played the player
     * @return
     *        Hand object, the type of hand
     */

    public Hand composeHand(CardGamePlayer player, CardList cards){
        Single single = new Single(player, cards);
        if(single.isValid()){
            return single;
        }

        Pair pair = new Pair(player, cards);
        if(pair.isValid()){
            return pair;
        }

        Triple triple = new Triple(player, cards);
        if(triple.isValid()){
            return triple;
        }

        Straight straight = new Straight(player, cards);
        if(straight.isValid()){
            return straight;
        }

        Flush flush = new Flush(player, cards);
        if(flush.isValid()){
            return flush;
        }

        FullHouse fullHouse = new FullHouse(player, cards);
        if(fullHouse.isValid()){
            return fullHouse;
        }

        Quad quad = new Quad(player, cards);
        if(quad.isValid()){
            return quad;
        }

        StraightFlush straightFlush = new StraightFlush(player, cards);
        if(straightFlush.isValid()){
            return straightFlush;
        }
        return null;
    }

    /**
     * Main function
     * @param args unused
     */
    public static void main(String[] args){
        BigTwoClient bigTwoClient = new BigTwoClient();
    }

    /**
     * Function to return playerID
     * @return Integere value holding the playerID
     */
    public int getPlayerID(){
        return playerID;
    }

    /**
     * Function to set the playerID
     * @param playerID the int value for playerID
     */
    @Override
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * Function to get The name of the player
     * @return String value for the playerName
     */
    public String getPlayerName(){
        return this.playerName;
    }

    /**
     * Function to set playerName
     * @param playerName the String value for playerName
     */

    public void setPlayerName(String playerName){
        this.playerName = playerName;
    }

    /**
     * Function returning the value of IP Address of the server
     * @return String value of serverIP
     */
    public String getServerIP(){
        return this.serverIP;
    }

    /**
     * Function to return server port
     * @return int value of serverPort
     */
    public int getServerPort(){
        return this.serverPort;
    }

    /**
     * Function to set server IP address
     * @param serverIP string value for the value of ip address
     */
    public void setServerIP(String serverIP){
        this.serverIP = serverIP;
    }

    /**
     * Function to ser Server Port
     * @param serverPort int value for the server Port
     */
    public void setServerPort(int serverPort){
        this.serverPort = serverPort;
    }

    /**
     * Function to connect to the server using socket
     */
    public void makeConnection() {
        try {
            sock = new Socket("127.0.0.1", 2396);
            oos = new ObjectOutputStream(sock.getOutputStream());
            Thread myThread = new Thread(new ServerHandler());
            myThread.start();
            sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, this.getPlayerName()));
            sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
            table.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override

    /**
     * Function to parse the messages received from the server
     */
    public synchronized void parseMessage(GameMessage message) {
        switch(message.getType()){
            case 0:
                setPlayerID(message.getPlayerID());
                table.setActivePlayer(message.getPlayerID());
                int counter = 0;
                while (counter < getNumOfPlayers())
                {
                    if(((String[])message.getData())[counter]!=null)
                    {
                        getPlayerList().get(counter).setName(((String[])message.getData())[counter]);
                    }
                    counter++;
                }
                break;
            case 2:
                table.printMsg("Server Full, unable to join");
                try{
                    sock.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;
            case 1:
                playerList.get(message.getPlayerID()).setName((String) message.getData());
                table.repaint();
                table.printMsg(playerList.get(message.getPlayerID()).getName() + "is here!");
                break;
            case 7:
                table.printChatMsg((String) message.getData());
                break;
            case 6:
                checkMove(message.getPlayerID(), (int []) message.getData());
                table.repaint();
                break;
            case 5:
                start((Deck) message.getData());
                table.enable();
                table.repaint();
                break;
            case 3:
                table.printMsg(playerList.get(message.getPlayerID()).getName() + " left the game.\n");
                playerList.get(message.getPlayerID()).setName("");

                if(!endOfGame()){
                    table.disable();
                    this.sendMessage(new CardGameMessage(4,-1,null));
                    for(int i=0; i<4; i++){
                        playerList.get(i).removeAllCards();
                    }
                    table.repaint();
                }
                table.repaint();
                break;
            case 4:
                table.printMsg(playerList.get(message.getPlayerID()).getName() + " is ready.\n");
                break;
        }


    }

    /**
     * Inner class that implements the Runnable Interface.
     * It is used for recieving messaged from the server
     *
     * @author Raunak Chopra
     */
    public class ServerHandler implements Runnable{
        public void run(){
            try{
                CardGameMessage message;
                ois = new ObjectInputStream(sock.getInputStream());
                while(sock.isClosed() == false){
                    message = (CardGameMessage) ois.readObject();
                    if(message != null){
                        parseMessage(message);
                    }
                }
                ois.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    /**
     * Function used to send messages to the server
     */
    public void sendMessage(GameMessage message) {
        try{
            oos.writeObject(message);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
