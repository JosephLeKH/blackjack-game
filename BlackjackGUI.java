import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The BlackjackGUI class serves as the graphical user interface for the Blackjack game.
 * It manages user interactions and displays the game state visually, including player and dealer hands,
 * betting actions, and game results. This class interfaces directly with the BlackjackGame class
 * to enact game logic based on user inputs.
 */
public class BlackjackGUI extends JFrame {
    // GUI components
    public JTextField betField;
    public JButton betButton;
    private JLabel highScoreLabel;
    private JLabel betLabel;
    private JLabel balanceLabel;
    private JLabel backgroundLabel;
    private JButton hitButton;
    private JButton standButton;
    private JButton instructionsButton;
    private JButton cashOutButton;
    private JPanel playerPanel;
    private JPanel dealerPanel;

    // Game variables
    private int highScore = 0;
    private int betAmount = 0;
    private int balance = 1000; // Initial balance
    private boolean betPlaced = false;
    private BlackjackGame blackjackGame;

    /**
     * Constructs a new Blackjack GUI window with controls for playing the game.
     * Initializes all UI components including buttons, labels, and panels.
     *
     * @param blackjackGame A reference to the {@link BlackjackGame} instance for game logic integration.
     */
    public BlackjackGUI(BlackjackGame blackjackGame) {
        this.blackjackGame = blackjackGame;

        // Set up the JFrame
        setTitle("Blackjack");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // Create and configure the background label
        backgroundLabel = new JLabel();
        backgroundLabel.setLayout(new BorderLayout());
        mainPanel.add(backgroundLabel, BorderLayout.CENTER);

        // Set the high score
        highScore = blackjackGame.getHighScore();

        // Initialize and set up the top panel (betting and balance information)
        setupTopPanel();

        // Initialize and set up the center panel (dealer and player hands)
        setupCenterPanel();

        // Initialize and set up the bottom panel (action buttons)
        setupBottomPanel();

        // Set up button listeners for interactions
        setupButtonListeners();

        // Automatically focus the betting field when the program launches
        betField.requestFocusInWindow();

        // Display the welcome screen when the game first launches
        showWelcomeScreen();
    }

    /**
     * Displays the welcome screen when the game first launches.
     */
    private void showWelcomeScreen() {
        // Create a JDialog for the welcome screen
        JDialog welcomeDialog = new JDialog(this, "Welcome to Blackjack!", true);
        welcomeDialog.setSize(550, 650); // Set the size to match the main game size
        welcomeDialog.setLayout(new BorderLayout());

        // Create a panel for the welcome screen
        JPanel welcomePanel = new JPanel(new BorderLayout());

        // Load and scale the image to fit the screen size
        ImageIcon imageIcon = new ImageIcon("src/Images/MrYeeCasinoBackground.jpeg");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(800, 800, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        welcomePanel.add(imageLabel, BorderLayout.CENTER);

        // Add a "Let's Play" button to the welcome screen
        JButton letsPlayButton = new JButton("Let's Play!");
        letsPlayButton.setPreferredSize(new Dimension(800, 60)); // Optional: set the button width to match the dialog width
        welcomePanel.add(letsPlayButton, BorderLayout.SOUTH);

        // Add the welcome panel to the welcome dialog
        welcomeDialog.add(welcomePanel);

        // Add an ActionListener to the "Let's Play" button
        letsPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the welcome dialog and show the main game interface
                welcomeDialog.dispose();
                setVisible(true);
            }
        });

        // Initially hide the main game interface
        setVisible(false);

        // Show the welcome dialog
        welcomeDialog.setVisible(true);
    }

    /**
     * Initializes and configures the top panel which contains bet and balance information.
     */
    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        highScoreLabel = new JLabel("High Score: $" + highScore);
        betLabel = new JLabel("     Enter Bet:");
        betField = new JTextField(10);
        betButton = new JButton("Place Bet");
        balanceLabel = new JLabel("Balance: $" + balance);

        // Add components to the top panel
        topPanel.add(highScoreLabel);
        topPanel.add(betLabel);
        topPanel.add(betField);
        topPanel.add(betButton);
        topPanel.add(balanceLabel);

        // Add the top panel to the background label
        backgroundLabel.add(topPanel, BorderLayout.NORTH);
    }

    /**
     * Initializes and configures the center panel which contains player and dealer hands.
     */
    private void setupCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(2, 1)); // Grid layout with 2 rows and 1 column
        dealerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Add dealer and player panels to the center panel
        centerPanel.add(dealerPanel); // Dealer's cards at the top
        centerPanel.add(playerPanel); // Player's cards at the bottom

        // Add the center panel to the background label
        backgroundLabel.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Initializes and configures the bottom panel which contains action buttons.
     */
    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Centered alignment

        // Create and configure buttons
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");
        instructionsButton = new JButton("Instructions");
        cashOutButton = new JButton("Cash Out");

        Dimension buttonSize = new Dimension(150, 50);
        hitButton.setPreferredSize(buttonSize);
        standButton.setPreferredSize(buttonSize);
        instructionsButton.setPreferredSize(buttonSize);
        cashOutButton.setPreferredSize(buttonSize);

        // Add buttons to the bottom panel
        bottomPanel.add(instructionsButton);
        bottomPanel.add(hitButton);
        bottomPanel.add(standButton);
        bottomPanel.add(cashOutButton);

        // Add the bottom panel to the background label
        backgroundLabel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets up button listeners for various actions in the game.
     */
    private void setupButtonListeners() {
        betButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeBet();
            }
        });

        // Add an ActionListener to the betting field to place the bet when the "Enter" key is pressed
        betField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeBet();
            }
        });

        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleHitAction();
            }
        });

        standButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStandAction();
            }
        });

        // Instructions button listener
        instructionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayInstructions();
            }
        });

        // Cash Out button listener
        cashOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCashOutAction();
            }
        });
    }

    /**
     * Handles the action for placing a bet based on the bet amount entered.
     */
    public void placeBet() {
        try {
            betAmount = Integer.parseInt(betField.getText());
            if (betAmount < 1 || betAmount > balance) {
                throw new NumberFormatException();
            }

            // Update UI and start the game
            betField.setEditable(false);
            betButton.setEnabled(false);
            betPlaced = true;
            blackjackGame.startGame(this, balance, betAmount);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid bet amount!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the "Hit" action.
     */
    private void handleHitAction() {
        if (betPlaced) {
            blackjackGame.hit();
        } else {
            JOptionPane.showMessageDialog(null, "Please place a bet first.");
        }
    }

    /**
     * Handles the "Stand" action.
     */
    private void handleStandAction() {
        if (betPlaced) {
            blackjackGame.stand();
        } else {
            JOptionPane.showMessageDialog(null, "You cannot stand without placing a bet and starting the game.");
        }
    }

    /**
     * Displays the instruction screen for the game.
     */
    private void displayInstructions() {
        // Hide the current window
        setVisible(false);

        // Create the instruction screen and pass the current game instance
        InstructionScreen instructionScreen = new InstructionScreen(this);
        instructionScreen.setVisible(true);
    }

    /**
     * Handles the "Cash Out" action.
     */
    private void handleCashOutAction() {
        if (!betPlaced) {
            resetGame();
        } else {
            JOptionPane.showMessageDialog(null, "You cannot cash out in the middle of a hand.");
        }
    }

    /**
     * Adds an image of a card to the player's hand.
     *
     * @param cardImageFile The file path to the card image.
     */
    public void addPlayerCardImage(String cardImageFile) {
        ImageIcon cardImage = new ImageIcon(cardImageFile);
        Image image = cardImage.getImage();
        Image resizedImage = image.getScaledInstance(100, -1, Image.SCALE_SMOOTH);
        playerPanel.add(new JLabel(new ImageIcon(resizedImage)));
        playerPanel.revalidate();
        playerPanel.repaint();
    }

    /**
     * Adds an image of a card to the dealer's hand.
     *
     * @param cardImageFile The file path to the card image.
     */
    public void addDealerCardImage(String cardImageFile) {
        ImageIcon cardImage = new ImageIcon(cardImageFile);
        Image image = cardImage.getImage();
        Image resizedImage = image.getScaledInstance(100, -1, Image.SCALE_SMOOTH);
        dealerPanel.add(new JLabel(new ImageIcon(resizedImage)));
        dealerPanel.revalidate();
        dealerPanel.repaint();
    }

    /**
     * Updates the balance displayed in the GUI.
     *
     * @param newBalance The new balance value.
     */
    public void updateBalance(int newBalance) {
        balance = newBalance;
        balanceLabel.setText("Balance: $" + balance);
    }

    /**
     * Gets the current balance.
     *
     * @return The current balance.
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Resets the player and dealer hand views.
     */
    public void resetHandViews() {
        playerPanel.removeAll();
        dealerPanel.removeAll();
        playerPanel.revalidate();
        playerPanel.repaint();
        dealerPanel.revalidate();
        dealerPanel.repaint();
        betPlaced = false;
    }

    /**
     * Resets the game by closing the current window and starting a new game.
     */
    public void resetGame() {
        finalGameStats();
        dispose(); // Close the current window
        BlackjackGUI newGame = new BlackjackGUI(new BlackjackGame());
        newGame.setVisible(true);
    }

    /**
     * Displays the final game statistics including total score, high score, hands won, and highest bank.
     */
    private void finalGameStats() {
        // Calculate the total score and high score
        blackjackGame.checkAndUpdateHighScore(balance - 1000);
        int currentScore = (balance < 1000) ? 0 : balance - 1000;

        // Determine the dialog title based on score
        String title = (currentScore > blackjackGame.getHighScore()) ? "High Score!" : "Low Score!";

        // Create the dialog output message
        String output = String.format(
                "Total Score: %d\nHighest Score: %d\nHands Won: %d\nHighest Bank: %d",
                currentScore,
                blackjackGame.getHighScore(),
                blackjackGame.getHandsWon(),
                blackjackGame.getHighestBank()
        );

        // Display the final game statistics
        JOptionPane.showMessageDialog(null, output, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Flips the dealer's hand by displaying the dealer's card images.
     */
    public void flipDealer() {
        dealerPanel.removeAll();

        for (String card : blackjackGame.getDealerHand()) {
            addDealerCardImage(card);
        }

        dealerPanel.revalidate();
        playerPanel.repaint();
    }
}
