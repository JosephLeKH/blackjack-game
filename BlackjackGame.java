import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * The BlackjackGame class manages the game logic and state of a Blackjack game session.
 * It coordinates between the game UI and the underlying game mechanics such as card dealing,
 * shuffling, and scoring.
 */
public class BlackjackGame {
    private static final String HIGH_SCORE_FILE_PATH = "high_score.txt";

    // Game variables
    private int highScore;
    private int highestBank;
    private int handsWon;

    // Image paths
    private static final String CARD_BACK_IMAGE = "src/Images/PNG-cards-1.3/back_of_card.png";

    // GUI and game components
    private BlackjackGUI blackjackGUI;
    private Map<String, String> cardImageMap;
    private Map<String, Integer> cardValueMap;
    private Stack<String> cardStack;
    private List<String> deck;
    private List<String> playerHand;
    private List<String> dealerHand;
    private boolean dealerPlayed;
    private int playerBalance;
    private int playerBet;

    /**
     * Constructs a new BlackjackGame instance, initializing all necessary game components
     * and loading the high score from a file.
     */
    public BlackjackGame() {
        highestBank = 1000;
        playerBalance = 0;
        playerBet = 0;

        cardImageMap = new HashMap<>();
        cardValueMap = new HashMap<>();
        deck = new ArrayList<>();
        cardStack = new Stack<>();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        dealerPlayed = false;

        initializeDeck();
        shuffleDeck();

        loadHighScore();
    }

    /**
     * Initializes the deck with 52 cards, mapping each card to its corresponding image file and value.
     */
    private void initializeDeck() {
        String[] suits = {"Clubs", "Diamonds", "Hearts", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        int value;
        for (String suit : suits) {
            for (int i = 0; i < ranks.length; i++) {
                String rank = ranks[i];
                String cardName = rank + " of " + suit;
                String imagePath = "src/Images/PNG-cards-1.3/" + rank.toLowerCase() + "_of_" + suit.toLowerCase() + ".png";

                // Calculate card value
                if (rank.equals("Ace")) {
                    value = 11;
                } else if (i >= 8) {
                    value = 10;
                } else {
                    value = i + 2;
                }

                // Add card information to the maps and deck
                deck.add(cardName);
                cardImageMap.put(cardName, imagePath);
                cardValueMap.put(cardName, value);
            }
        }
    }

    /**
     * Shuffles the deck and refills the card stack with the shuffled cards.
     */
    private void shuffleDeck() {
        List<String> tempDeck = new ArrayList<>(deck);
        Random random = new Random();
        cardStack = new Stack<>();

        while (!tempDeck.isEmpty()) {
            int index = random.nextInt(tempDeck.size());
            cardStack.add(tempDeck.get(index));
            tempDeck.remove(index);
        }
    }

    /**
     * Counts the total value of the cards in the specified hand, accounting for the best use of aces.
     *
     * @param hand The hand whose total value is to be computed.
     * @return The computed value of the hand.
     */
    private int countHandValue(List<String> hand) {
        int value = 0;
        int aceCount = 0;

        // Calculate the total value and count aces
        for (String card : hand) {
            int cardValue = cardValueMap.get(card);

            if (cardValue == 11) {
                aceCount++;
            } else {
                value += cardValue;
            }
        }

        // Adjust for aces
        while (aceCount > 0) {
            if (value + 11 <= 21) {
                value += 11;
            } else {
                value += 1;
            }
            aceCount--;
        }

        // Check for Five-Card Charlie
        if (hand.size() == 5 && value < 21) {
            value = 21;
        }

        return value;
    }

    /**
     * Starts a new game round with the given bet and balance. It also deals two cards each to the player and the dealer.
     *
     * @param blackjackGUI The GUI instance to interact with.
     * @param balance The starting balance of the player.
     * @param bet The bet amount for the round.
     */
    public void startGame(BlackjackGUI blackjackGUI, int balance, int bet) {
        this.blackjackGUI = blackjackGUI;
        this.playerBalance = balance;
        this.playerBet = bet;

        // Deal initial cards
        playerHand.add(cardStack.pop());
        dealerHand.add(cardStack.pop());
        playerHand.add(cardStack.pop());
        dealerHand.add(cardStack.pop());

        // Display player's cards and dealer's visible card
        blackjackGUI.addPlayerCardImage(getPlayerHand().get(0));
        blackjackGUI.addPlayerCardImage(getPlayerHand().get(1));
        blackjackGUI.addDealerCardImage(getDealerHand().get(0));
        blackjackGUI.addDealerCardImage(CARD_BACK_IMAGE);

        // Check for win conditions after dealing cards
        checkForWin();
    }

    /**
     * Deals an additional card to the player and checks for any end-of-round conditions.
     */
    public void hit() {
        playerHand.add(cardStack.pop());
        blackjackGUI.addPlayerCardImage(getPlayerHand().get(getPlayerHand().size() - 1));

        checkForWin();
    }

    /**
     * Ends the player's turn and allows the dealer to play according to Blackjack rules.
     */
    public void stand() {
        while (countHandValue(dealerHand) < 17) {
            dealerHand.add(cardStack.pop());
            blackjackGUI.addDealerCardImage(getDealerHand().get(getDealerHand().size() - 1));
        }

        dealerPlayed = true;
        checkForWin();
    }

    /**
     * Determines if the game is over and returns the outcome.
     *
     * @return -1 if the game is still ongoing, 0 if the dealer wins, 1 if the player wins, or 2 if it's a tie.
     */
    private int checkGameOver() {
        int playerValue = countHandValue(playerHand);
        int dealerValue = countHandValue(dealerHand);

        // Check for Blackjacks first
        boolean playerBlackjack = (playerValue == 21 && playerHand.size() == 2);
        boolean dealerBlackjack = (dealerValue == 21 && dealerHand.size() == 2);

        if (playerBlackjack && dealerBlackjack) {
            return 2; // Tie if both have Blackjack
        }
        if (playerBlackjack) {
            return 1; // Player wins with Blackjack
        }
        if (dealerBlackjack) {
            return 0; // Dealer wins with Blackjack
        }

        // Handle other end-of-round conditions
        if (!dealerPlayed) {
            if (playerValue > 21) {
                return 0; // Dealer wins if player busts
            }
        } else {
            if (playerValue == dealerValue) {
                return 2; // Tie if values are equal
            } else if (dealerValue > 21 || playerValue > dealerValue) {
                return 1; // Player wins if dealer busts or player has a higher value
            } else {
                return 0; // Dealer wins
            }
        }

        return -1; // Game continues
    }

    /**
     * Checks the result of the game after each action and updates the GUI accordingly.
     */
    public void checkForWin() {
        int result = checkGameOver();

        // Handle the outcome based on the game result
        if (result == 0) {
            // Dealer wins
            blackjackGUI.flipDealer();
            handleDealerWin();
        } else if (result == 1) {
            // Player wins
            handsWon++;
            updateHighestBank();
            blackjackGUI.flipDealer();
            blackjackGUI.updateBalance(playerBalance + playerBet);
            JOptionPane.showMessageDialog(null, "You won $" + playerBet + "!");
            resetRound();
        } else if (result == 2) {
            // Tie
            blackjackGUI.flipDealer();
            blackjackGUI.updateBalance(playerBalance);
            JOptionPane.showMessageDialog(null, "You tied this hand.");
            resetRound();
        }
    }

    /**
     * Handles the outcome when the dealer wins the round.
     */
    private void handleDealerWin() {
        if (playerBalance - playerBet == 0) {
            blackjackGUI.resetGame();
        } else {
            // Update player balance and inform them of the loss
            blackjackGUI.updateBalance(playerBalance - playerBet);
            JOptionPane.showMessageDialog(null, "You lost $" + playerBet + "!");
            resetRound();
        }
    }

    /**
     * Resets the game to a new round, re-shuffling the deck if necessary and clearing player hands.
     */
    public void resetRound() {
        if (cardStack.size() < 20) {
            shuffleDeck();
        }
        blackjackGUI.betField.setEditable(true);
        blackjackGUI.betButton.setEnabled(true);
        //blackjackGUI.betField.setText("");

        // Clear player and dealer hands
        playerHand.clear();
        dealerHand.clear();
        dealerPlayed = false;

        // Notify GUI to reset hand views
        blackjackGUI.resetHandViews();
    }

    /**
     * Retrieves the player's hand with the corresponding card images.
     *
     * @return A list of card image file paths for the player's hand.
     */
    public List<String> getPlayerHand() {
        List<String> imageFiles = new ArrayList<>();

        for (String card : playerHand) {
            imageFiles.add(cardImageMap.get(card));
        }

        return imageFiles;
    }

    /**
     * Retrieves the dealer's hand with the corresponding card images.
     *
     * @return A list of card image file paths for the dealer's hand.
     */
    public List<String> getDealerHand() {
        List<String> imageFiles = new ArrayList<>();

        for (String card : dealerHand) {
            imageFiles.add(cardImageMap.get(card));
        }

        return imageFiles;
    }

    /**
     * Loads the highest score achieved from a file and updates the highScore attribute.
     */
    private void loadHighScore() {
        try {
            File file = new File(HIGH_SCORE_FILE_PATH);

            // If the file does not exist, create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // Read the high score from the file
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                highScore = (line != null) ? Integer.parseInt(line) : 0;
            }
        } catch (IOException | NumberFormatException e) {
            highScore = 0; // Default to 0 if an error occurs
        }
    }

    /**
     * Checks and updates the high score if the current score exceeds the previously saved high score.
     *
     * @param currentScore The current score to compare against the high score.
     */
    public void checkAndUpdateHighScore(int currentScore) {
        if (currentScore > highScore) {
            highScore = currentScore;
            saveHighScore(); // Save the new high score to the file
        }
    }

    /**
     * Saves the current high score to a file.
     */
    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE_PATH))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }

    /**
     * Updates the highest bank if the current player balance plus bet is greater than the previous highest bank.
     */
    private void updateHighestBank() {
        if (playerBalance + playerBet > highestBank) {
            highestBank = playerBalance + playerBet;
        }
    }

    /**
     * Retrieves the current high score
     *
     * @return The High Score
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Retrieves the current highest bank
     *
     * @return The Highest Bank
     */
    public int getHighestBank() {
        return highestBank;
    }

    /**
     * Retrieves the current number of hands won
     *
     * @return The number of hands won
     */
    public int getHandsWon() {
        return handsWon;
    }
}
