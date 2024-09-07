import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The InstructionScreen class provides a user interface to display the rules and instructions
 * for playing the Blackjack game. It is designed as a modal window that appears over the main
 * game interface, offering a brief tutorial on how the game is played before the user starts a session.
 */
public class InstructionScreen extends JFrame {
    private final BlackjackGUI currentGame; // Reference to the main game GUI

    /**
     * Constructs an InstructionScreen that displays rules and instructions for the game.
     * This screen includes a centralized text display and a button to begin playing.
     *
     * @param currentGame The main game GUI that this screen will interact with and control visibility of.
     */
    public InstructionScreen(BlackjackGUI currentGame) {
        this.currentGame = currentGame;

        setupWindow();
        setupInstructions();
        setupStartGameButton();
    }

    /**
     * Configures the JFrame settings such as title, size, and close operation.
     */
    private void setupWindow() {
        setTitle("Instructions and Rules");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window on the screen

        // Set up the main panel with border layout and padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 5));
        setContentPane(mainPanel);
    }

    /**
     * Sets up the instructions text pane for displaying the rules of the game.
     */
    private void setupInstructions() {
        JTextPane instructionsTextPane = new JTextPane();
        instructionsTextPane.setContentType("text/html"); // Use HTML for flexible formatting
        instructionsTextPane.setText(getInstructionsHtml());
        instructionsTextPane.setEditable(false);
        instructionsTextPane.setOpaque(false); // Make the background transparent

        // Add the instructions text pane to a scroll pane and add it to the main panel
        JScrollPane scrollPane = new JScrollPane(instructionsTextPane);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Returns the instructions HTML as a string.
     *
     * @return The instructions in HTML format.
     */
    private String getInstructionsHtml() {
        return "<html><body style='text-align: center; font-family: Arial; font-size: 16px; color: white; background-color: black;'>" +
                "<p>Instructions and Rules:</p>" +
                "<p>1. To interact with the game, use the 'Hit' and 'Stand' buttons.</p>" +
                "<p>2. To place a bet, enter the amount of money you'd like to bet into the text box and hit the 'Place Bet' button.</p>" +
                "<p>3. Aim to go as long as possible without losing all your money!</p>" +
                "<p>4. Welcome to Black Jack Yee Edition!</p><br>" +
                "<p>General Blackjack Instructions: The goal of Blackjack is to beat the dealer's hand without going over 21." +
                " Face cards are worth 10. Aces are worth 1 or 11, whichever makes a better hand." +
                " Each player starts with two cards, one of the dealer's cards is hidden until the end." +
                " 'Hit' asks for another card. 'Stand' ends your turn." +
                " If you go over 21, you bust, and the dealer wins regardless of the dealer's hand.</p><br>" +
                "<p>Good luck and have fun!</p></body></html>";
    }

    /**
     * Sets up the "Let's Play" button and adds an action listener to it.
     */
    private void setupStartGameButton() {
        // Button to start the game
        JButton startGameButton = new JButton("Let's Play");
        startGameButton.setPreferredSize(new Dimension(150, 50));
        startGameButton.setFont(new Font("Arial", Font.BOLD, 24));

        // Add the button to the main panel at the bottom
        getContentPane().add(startGameButton, BorderLayout.SOUTH);

        // Add an action listener to the button to start the game when clicked
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
    }

    /**
     * Handles the action of starting the game.
     */
    private void startGame() {
        setVisible(false); // Hide the instruction screen
        currentGame.setVisible(true); // Show the main game interface
    }
}
