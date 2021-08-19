import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * BigTwoTable class implements the CardGameTable interface. It is used to build a GUI
 * for the Big Two card game and handle all user actions.
 */
public class BigTwoTable implements CardGameTable{

    /**
     * Card game which associates with this table
     */
    private BigTwoClient game;

    /**
     * Boolean array indicating which cards are being selected
     */
    private boolean[] selected;

    /**
     * Integer specifying the index of the active player
     */
    private int activePlayer;

    /**
     * Main window of the application
     */
    private JFrame frame;

    /**
     * JPanel object for showing the cards of each player and the cards played on the table
     */
    private JPanel bigTwoPanel;

    /**
     * "PLAY" button to play the selected cards
     */
    private JButton playButton;

    /**
     * "PASS" button to pass the move
     */
    private JButton passButton;

    /**
     * Text area for showing the current game status as well as end of game messages
     */
    private JTextArea msgArea;

    private JTextArea chatBox; //1

    private JTextField chatText; //2
    /**
     * 2D array to store the images for the faces of cards
     */
    private Image[][] cardImages;

    /**
     * Image for the back of the card
     */
    private Image cardBackImage;

    /**
     * Array for storing the images of the avatars
     */
    private Image[] avatars;

    /**
     * Constructor for creating a BigTwoTable.
     * @param game
     *              game is a reference to a card game associates with this table.
     */
    public BigTwoTable(BigTwoClient game){

        this.game = game;
        selected = new boolean[13];
        setActivePlayer(game.getCurrentIdx());
        imagesStore();
        GUIsection();

    } // constructor function

    /**
     * Function to add images of the cards and player avatars to the respective arrays
     */
    private void imagesStore(){ //storing
        avatars = new Image[4]; //creating an Image array to store avatars

        avatars[0] = new ImageIcon("avatars/batman.png").getImage(); //player 1 avatar
        avatars[1] = new ImageIcon("avatars/flash.png").getImage(); // player 2 avatar
        avatars[2] = new ImageIcon("avatars/green.png").getImage(); // player 3 avatar
        avatars[3] = new ImageIcon("avatars/superman.png").getImage(); //player 4 avatar

        cardBackImage = new ImageIcon("cards/back.gif").getImage(); //back image of the card

        char[] suit = {'d','c','h','s'}; //character array to store suits
        char[] rank = {'a','2','3','4','5','6','7','8','9','t','j','q','k'}; //character array to store ranks

        cardImages = new Image[4][13]; //2D array to store card Images
        for(int i=0; i<13; i++){
            for(int j=0; j<4; j++){
                String address = new String();//String to hold address of the card Image
                address = "cards/" + rank[i] + suit[j] + ".gif";
                cardImages[j][i] = new ImageIcon(address).getImage();
            }

        }
    }

    /**
     * Function to set up the GUI
     */
    private void GUIsection(){
        frame = new JFrame(); // creating a new frame
        frame.setLayout(new BorderLayout());
        frame.setSize(1600,900); // setting dimensions of the frame
        // visibility of the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closing operation
        frame.setTitle("Big Two"); //setting title

        JMenuBar menuBar = new JMenuBar(); // creating a ne menu bar
        frame.setJMenuBar(menuBar); //setting menu bar of the frame
        JMenu menu = new JMenu("Game"); //creating a menu
        menuBar.add(menu); //adding the menu to the menu bar

        //creating menu items

        JMenuItem connect = new JMenuItem("Connect"); // creating a menu item for restarting the game
        connect.addActionListener(new ConnectMenuItemListener()); //using inner class to attach it to a actionListener
        JMenuItem quit = new JMenuItem("Quit"); // creating a menu item to quit the game
        quit.addActionListener(new QuitMenuItemListener());

        menu.add(connect); //adding 'restart' to the menu
        menu.add(quit); // adding 'quit' to the menu

        //menu bar complete

        //Button Panel [Bottom]

        JPanel buttonPanel = new JPanel(); //button panel
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); //setting layout of the buttonPanel

        playButton = new JButton("Play"); //'PLAY' button
        passButton = new JButton("Pass"); //'PASS' button

        playButton.addActionListener(new PlayButtonListener()); //play button action listener
        passButton.addActionListener(new PassButtonListener()); // pass button action listener

        buttonPanel.add(playButton); // adding play button to panel
        buttonPanel.add(Box.createHorizontalStrut(25)); //adding space between the two buttons
        buttonPanel.add(passButton); // adding pass button to panel

        // main section
        bigTwoPanel = new BigTwoPanel(); //creating a new panel
        bigTwoPanel.setLayout(new BoxLayout(bigTwoPanel, BoxLayout.Y_AXIS)); //setting the layout of the panel to BoxLayout.Y_AXIS so that as to stack the contents vertically

        // creating the message area to display the logs
        msgArea = new JTextArea();
        msgArea.setEditable(false); //disabling the edit feature, so that user does not enter anything

        JScrollPane scrollPane = new JScrollPane(msgArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(525,450));
        DefaultCaret caret = (DefaultCaret)msgArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        //creating chat area

        chatBox = new JTextArea();
        chatBox.setEnabled(false);
        DefaultCaret caretChatBox = (DefaultCaret) chatBox.getCaret();
        caretChatBox.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane chatScrollPane = new JScrollPane(chatBox,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //
        chatScrollPane.setPreferredSize(new Dimension(525,450));

        JPanel chatArea = new JPanel();
        chatArea.setLayout(new FlowLayout());
        chatArea.add(new JLabel("Send Message"));

        chatText = new JTextField();
        chatText.addActionListener(new SendChatListener());
        chatText.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        chatText.setPreferredSize(new Dimension(200, 25));
        chatArea.add(chatText);

        JPanel messageSection = new JPanel();
        messageSection.setLayout(new BoxLayout(messageSection, BoxLayout.PAGE_AXIS));
        messageSection.add(scrollPane);
        messageSection.add(chatScrollPane);
        messageSection.add(chatArea);

        frame.add(BorderLayout.EAST, messageSection);
        frame.add(BorderLayout.CENTER, bigTwoPanel);
        frame.add(BorderLayout.SOUTH, buttonPanel);
        frame.setVisible(true);

    }

    /**
     * Inner class that implements the ActionListener interface. Implements the actionPerformed() method from the ActionListener interface
     * to handle button-click events for the Play button.
     *
     * @author Raunak Chopra
     */
    class PlayButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent event) {
            if (game.getPlayerID() == game.getCurrentIdx()) {
                if (getSelected() == null) { //checking if the user has played any cards
                    printMsg("Select cards to play.\\n");
                } else {
                    game.makeMove(activePlayer, getSelected());
                }
                resetSelected(); // to reset the selected array
            }
        }
    }

    /**
     * Inner class that implements the ActionListener interface. Implements the actionPerformed() method from the ActionListener interface
         * to handle button-click events for Chat Text Field.
     *
     * @author Raunak Chopra
     */

    class SendChatListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            String chatMessage = chatText.getText();
            CardGameMessage message = new CardGameMessage(CardGameMessage.MSG, -1, chatMessage);
            game.sendMessage(message);
            chatText.setText("");
        }
    }

    /**
     * Inner class that implements the ActionListener
     * interface. Implements the actionPerformed() method from the ActionListener interface
     * to handle button-click events for the Pass button.
     *
     * @author Raunak Chopra
     */

    class PassButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if(game.getPlayerID() == game.getCurrentIdx()) {
                game.makeMove(activePlayer, null); //sending no cards
            }
            repaint();
        }
    }

    /**
     * Inner class that implements the ActionListener
     * interface. Implements the actionPerformed() method from the ActionListener interface
     * to handle menu-item-click events for the Quit menu item
     * @author Raunak Chopra
     */
    class QuitMenuItemListener implements ActionListener{ //quit menu action listener
        public void actionPerformed(ActionEvent event){
            System.exit(0); //terminating the game
        }
    }


    /**
     * Inner class that implements the ActionListener
     * interface. Implements the actionPerformed() method from the ActionListener interface
     * to handle menu-item-click events for the Connect menu item.
     *
     * @author Raunak Chopra
     */
    class ConnectMenuItemListener implements ActionListener{ //connect menu action listener
        public void actionPerformed(ActionEvent event){
            game.makeConnection();
        }
    }

    /**
     * Inner class that extends the JPanel class and implements the
     * MouseListener interface. Overrides the paintComponent() method inherited from the
     * JPanel class to draw the card game table. Implements the mouseClicked() method from
     * the MouseListener interface to handle mouse click events.
     * @author Raunak Chopra
     */
    class BigTwoPanel extends JPanel implements MouseListener{
        private static final long serialVersionUID = 1l;

        /**
         * Constructor function, registers the mouseListener
         */
        public BigTwoPanel(){
            this.addMouseListener(this);
        }


        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        /*
         * {@inheritDoc}
         */

        @Override
        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        /*
         * {@inheritDoc}
         */

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        /*
         * {@inheritDoc}
         */

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            this.setBackground(Color.GREEN.darker()); //setting the background color of the panel


            //relevant coordinates
            int stringPosY = 20;
            int imgPosY = 30;
            int linePosY = 160;
            int cardPosY = 33;

            for(int i=0;i<4;i++){
                if(game.getPlayerID() == i) {
                    g.drawString(game.getPlayerList().get(i).getName()+" <You> ", 5, stringPosY);
                }
                else{
                    g.drawString(game.getPlayerList().get(i).getName(), 5, stringPosY);
                }
                g.setColor(Color.BLACK);
                g.drawImage(avatars[i],5,imgPosY,this);
                g2.drawLine(i,linePosY,1600,linePosY);

                if(game.getPlayerID() == i){
                    for (int k=0;k<game.getPlayerList().get(i).getNumOfCards();k++){
                        if(!selected[k]){
                            g.drawImage(cardImages[game.getPlayerList().get(i).getCardsInHand().getCard(k).suit][game.getPlayerList().get(i).getCardsInHand().getCard(k).rank],155+(40*k),cardPosY,this);
                        }
                        else {
                            g.drawImage(cardImages[game.getPlayerList().get(i).getCardsInHand().getCard(k).suit][game.getPlayerList().get(i).getCardsInHand().getCard(k).rank],155+(40*k),cardPosY-20,this);
                        }
                    }
                }

                else{
                    for(int j=0;j<game.getPlayerList().get(i).getCardsInHand().size();j++){
                        g.drawImage(cardBackImage,155+(40*j),cardPosY,this);
                    }
                }

                stringPosY += 160;
                imgPosY += 160;
                linePosY += 160;
                cardPosY += 160;
            }

            g.drawString("Hand on Table",10,670);

            if (!game.getHandsOnTable().isEmpty())
            {
                Hand handOnTable = game.getHandsOnTable().get(game.getHandsOnTable().size()-1);
                g.drawString("Hand Type: "+game.getHandsOnTable().get(game.getHandsOnTable().size()-1).getType(), 10, 690);
                for (int i = 0; i < handOnTable.size(); i++)
                {
                    g.drawImage(cardImages[handOnTable.getCard(i).suit][handOnTable.getCard(i).rank], 160 + 40*i, 655, this);
                }

                g.drawString("Played by: " + game.getHandsOnTable().get(game.getHandsOnTable().size()-1).getPlayer().getName(), 10, 725);
            }
            this.repaint();
        }

        public void mouseClicked(MouseEvent event)
        {
            boolean flag = false;
            int startPoint = game.getPlayerList().get(activePlayer).getNumOfCards()-1;

            if(event.getX() >= (155+(startPoint*40)) && event.getX() <= (155 +(startPoint*40) +73)){
                if(selected[startPoint]){
                    if(event.getY() >= (43+(160*activePlayer)-20) && event.getY() <= (43 +(160*activePlayer) +97-20)){
                        selected[startPoint] =false;
                        flag = true;
                    }
                }
                else{
                    if(event.getY() >= (43+(160*activePlayer)) && event.getY() <= (43 +(160*activePlayer) +97)){
                        selected[startPoint] = true;
                        flag = true;
                    }
                }
            }

            for(startPoint = game.getPlayerList().get(activePlayer).getNumOfCards()-2; startPoint >=0 && !flag; startPoint--){
                if(event.getX() >= (155+(startPoint*40)) && event.getX() <= (155+(startPoint*40)+40)){
                    if(!selected[startPoint]){
                        if(event.getY() >= (43+(160*activePlayer)) && event.getY() <= (43+(160*activePlayer)+97)){
                            flag = true;
                            selected[startPoint] = true;
                        }
                    }
                    else {
                        if(event.getY() >= (43+(160*activePlayer)-20) && event.getY() <= (43+(160*activePlayer)+97-20)){
                            selected[startPoint] = false;
                            flag = true;
                        }
                    }
                }
                else if (event.getX() >= (155+(startPoint*40)+40) && event.getX() <= (155+(startPoint*40)+73) && event.getY() >= (43+(160*activePlayer)) && event.getY() <= 43+(160*activePlayer)+97) {
                    if (selected[startPoint+1] && !selected[startPoint]) {
                        selected[startPoint] = true;
                        flag = true;
                    }
                }
                else if (event.getX() >= (155+(startPoint*40)+40) && event.getX() <= (155+(startPoint*40)+73) && event.getY() >= (43+(160*activePlayer)-20) && event.getY() <= 43+(160*activePlayer)+97-20) {
                    if (!selected[startPoint+1] && selected[startPoint]){
                        selected[startPoint] = false;
                        flag = true;
                    }
                }
            }
            this.repaint();
        }
    }

    public void setActivePlayer(int activePlayer){ //function to set active player
        this.activePlayer = activePlayer;
    }

    public int[] getSelected(){
        int count = 0; //count variable to count the number of cards played

        for(int i=0;i<selected.length;i++){ //so as to get the count of selected cards
            if(selected[i]){
                ++count;
            }
        }

        if(count==0){ //incase no card has been selected
            return null;
        }

        int[] returnResult = new int[count]; //to store the indices of the cards played
        int flag = 0;// to increment the counter of returnResult

        for(int j=0;j< selected.length;j++){
            if(selected[j]){
                returnResult[flag++] = j; //adding index of selected cards
            }
        }
        return returnResult;
    }

    public void resetSelected(){
        // reseting the list of selected cards
        for(int i=0;i<13;i++){
            selected[i] = false;
        }
    }

    public void repaint(){
        resetSelected();
        frame.repaint();
        frame.revalidate();
    }

    public void printMsg(String msg){
        msgArea.append(msg +"\n");
    }

    public void clearMsgArea(){
        msgArea.setText(null);
    }

    /**
     * Function used to add chat messages in the chat box
     * @param msg String value for the message
     */

    public void printChatMsg(String msg){
        chatBox.append(msg +"\n");
    }

    /**
     * Function used for clearing the Chat TextArea
     */

    public void clearChatMsgArea(){
        chatBox.setText(null);
    }


    public void enable(){
        playButton.setEnabled(true);
        passButton.setEnabled(true);
        bigTwoPanel.setEnabled(true);
    }

    public void disable(){
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        bigTwoPanel.setEnabled(false);
    }

    public void reset(){
        resetSelected();
        clearMsgArea();
        enable();
    }

}

