import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Arrays;

public class Main extends JFrame implements ActionListener {

    //defined colors for my theme
    private static Color darkBackground = new Color(34, 40, 49);
    private static Color darkForeground = new Color(238, 238, 238);
    private static Color accentColor = new Color(0, 173, 181);
    private static Color errorColor = new Color(255, 87, 87);

    //gui components
    private JPanel mainPanel, setupPanel, gamePanel, summaryPanel;
    private JButton singlePlayerButton, multiPlayerButton;
    private JLabel welcomeLabel;

    //components for card layout management
    private CardLayout cardLayout = new CardLayout();
    private Container container;

    //game state variables
    private int currentMode = 0; //1=single, 2=multi
    private int selectedGameMode = 0; //1=make a wish, 2=no mistakes, 3=take chances, 4=time trial
    private int numQuestions = 0;
    private int timeLimit = 0;
    private Player[] users;
    private Game currentGame; //game object for the current player/turn
    private int currentPlayerIndex = 0;
    private int currentQuestionCount = 0;
    private Timer gameTimer;
    private int livesRemaining = 3;

    //components in setup panel
    private JTextField numPlayersField, numQuestionsField, timeLimitField;
    private JLabel numQuestionsLabel, timeLimitLabel;
    private JComboBox<String> gameModeDropdown;
    private JButton setupDoneButton;

    //components in game panel
    private JLabel questionLabel, scoreLabel, feedbackLabel, timeOrLivesLabel, turnLabel;
    private JTextField answerField;
    private JButton submitButton;

    //components in summary panel
    private JTextArea resultsArea;

    public Main() {
        setTitle("Math Learning Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        //consistent gui look
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            //ignore adn stick with default look
        }

        container = getContentPane();
        container.setLayout(cardLayout);

        //initial start panel
        createStartPanel();
        container.add(mainPanel, "start");

        //setup panel
        createSetupPanel();
        container.add(setupPanel, "setup");

        //game panel
        createGamePanel();
        container.add(gamePanel, "game");

        //summary panel
        createSummaryPanel();
        container.add(summaryPanel, "summary");

        cardLayout.show(container, "start");

        setLocationRelativeTo(null); //center the window
        setVisible(true);
    }

    //method to apply button styles
    private void applyButtonStyle(JButton button) {
        button.setBackground(accentColor);
        button.setForeground(darkBackground);
        button.setFont(new Font("arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    //panel creation methods
    private void createStartPanel() {
        //change layout to 3 rows 1 column with padding and dark background
        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(darkBackground);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));

        welcomeLabel = new JLabel("Welcome to my Math Challenge!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("arial", Font.BOLD, 30));
        welcomeLabel.setForeground(accentColor);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        //panel for side byside buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 30, 0)); //1 row 2 columns 30px horizontal gap
        buttonPanel.setBackground(darkBackground);

        singlePlayerButton = new JButton("Single Player");
        multiPlayerButton = new JButton("Multiplayer/Competition");

        //apply custom styles
        applyButtonStyle(singlePlayerButton);
        applyButtonStyle(multiPlayerButton);

        singlePlayerButton.addActionListener(this);
        multiPlayerButton.addActionListener(this);

        buttonPanel.add(singlePlayerButton);
        buttonPanel.add(multiPlayerButton);

        //add button panel to the center
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    private void createSetupPanel() {
        setupPanel = new JPanel(new GridLayout(7, 2, 15, 15));
        setupPanel.setBackground(darkBackground);
        setupPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        //helper function for dark theme labels
        setupPanel.add(createStyledLabel("Game Setup[:"));
        setupPanel.add(createStyledLabel(""));

        //game mode selection
        setupPanel.add(createStyledLabel("Select game mode:"));
        String[] modes = {"Make a Wish", "No Mistakes", "Take Chances", "Time Trial"};
        gameModeDropdown = new JComboBox<>(modes);
        gameModeDropdown.addActionListener(e -> updateSetupVisibility());//listener for dynamic fields
        gameModeDropdown.setBackground(darkBackground.brighter());
        gameModeDropdown.setForeground(darkForeground);
        setupPanel.add(gameModeDropdown);

        //number of players (for multiplayer)
        setupPanel.add(createStyledLabel("Number of Players:"));
        numPlayersField = createStyledTextField("1");
        setupPanel.add(numPlayersField);

        //questions input (for make a wish)
        numQuestionsLabel = createStyledLabel("Questions:");
        setupPanel.add(numQuestionsLabel);
        numQuestionsField = createStyledTextField("5");
        setupPanel.add(numQuestionsField);

        //time limit input (for time trial)
        timeLimitLabel = createStyledLabel("Time Limit (in seconds):");
        setupPanel.add(timeLimitLabel);
        timeLimitField = createStyledTextField("60");
        setupPanel.add(timeLimitField);

        setupPanel.add(new JLabel(""));//empty cell for spacing (row 6 col 1)
        setupDoneButton = new JButton("Start");
        applyButtonStyle(setupDoneButton);
        setupDoneButton.addActionListener(e -> handleSetupCompletion());
        setupPanel.add(setupDoneButton); //row 6 (col 2)

        updateSetupVisibility(); //set initial visibility based on default mode (make a wish)
    }

    //method for the dark theme labels
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(darkForeground);
        label.setFont(new Font("arial", Font.PLAIN, 16));
        return label;
    }

    //method for dark theme text fields
    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text);
        field.setBackground(darkBackground.brighter());
        field.setForeground(darkForeground);
        field.setCaretColor(darkForeground); //cursor color
        field.setBorder(BorderFactory.createLineBorder(accentColor, 1));
        return field;
    }

    //hides/shows fields based on game mode
    private void updateSetupVisibility() {
        int selectedIndex = gameModeDropdown.getSelectedIndex();

        //make a wish mode (index 0)
        boolean showNumQuestions = (selectedIndex == 0);
        numQuestionsLabel.setVisible(showNumQuestions);
        numQuestionsField.setVisible(showNumQuestions);

        //time trial mode (index 3)
        boolean showTimeLimit = (selectedIndex == 3);
        timeLimitLabel.setVisible(showTimeLimit);
        timeLimitField.setVisible(showTimeLimit);

        //update the layout after components are hidden/shown
        setupPanel.revalidate();
        setupPanel.repaint();
    }


    private void createGamePanel() {
        gamePanel = new JPanel(new BorderLayout(20, 20));
        gamePanel.setBackground(darkBackground);
        gamePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        //top bar for score lives time turn
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setBackground(darkBackground);

        turnLabel = createStyledLabel("Player: n/a");
        turnLabel.setHorizontalAlignment(SwingConstants.LEFT);

        scoreLabel = createStyledLabel("Score: 0");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        timeOrLivesLabel = createStyledLabel("Time/Lives: n/a");
        timeOrLivesLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(turnLabel);
        headerPanel.add(scoreLabel);
        headerPanel.add(timeOrLivesLabel);

        //center area for question
        questionLabel = new JLabel("Waiting for game to start...", SwingConstants.CENTER);
        questionLabel.setFont(new Font("monospaced", Font.BOLD, 64)); //question formatting
        questionLabel.setForeground(accentColor);

        //bottom bar for input and feedback
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBackground(darkBackground);

        feedbackLabel = new JLabel("Enter your answer below.", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("arial", Font.ITALIC, 16));
        feedbackLabel.setForeground(darkForeground); //default color

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(darkBackground);

        answerField = createStyledTextField("");
        answerField.setColumns(15);
        answerField.setFont(new Font("serif", Font.PLAIN, 24));
        answerField.setHorizontalAlignment(JTextField.CENTER);

        submitButton = new JButton("Submit Answer");
        applyButtonStyle(submitButton);
        submitButton.addActionListener(e -> handleSubmitAnswer());

        inputPanel.add(answerField);
        inputPanel.add(submitButton);

        controlPanel.add(feedbackLabel, BorderLayout.NORTH);
        controlPanel.add(inputPanel, BorderLayout.CENTER);

        gamePanel.add(headerPanel, BorderLayout.NORTH);
        gamePanel.add(questionLabel, BorderLayout.CENTER);
        gamePanel.add(controlPanel, BorderLayout.SOUTH);
    }

    private void createSummaryPanel() {
        summaryPanel = new JPanel(new BorderLayout(10, 10));
        summaryPanel.setBackground(darkBackground);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Game Results/Leaderboard", SwingConstants.CENTER);
        title.setFont(new Font("arial", Font.BOLD, 28));
        title.setForeground(accentColor);

        resultsArea = new JTextArea(20, 50);
        resultsArea.setEditable(false);
        resultsArea.setBackground(darkBackground.brighter().brighter().brighter()); //slightly lighter dark gray
        resultsArea.setForeground(darkForeground);
        resultsArea.setFont(new Font("monospaced", Font.PLAIN, 14));

        //set up the scroll pane with the dark background
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(accentColor, 1));
        scrollPane.getViewport().setBackground(darkBackground.brighter().brighter().brighter());

        JButton playAgainButton = new JButton("Play Again");
        applyButtonStyle(playAgainButton);
        playAgainButton.addActionListener(e -> resetGame());

        summaryPanel.add(title, BorderLayout.NORTH);
        summaryPanel.add(scrollPane, BorderLayout.CENTER);
        summaryPanel.add(playAgainButton, BorderLayout.SOUTH);
    }

    //action listener for main buttons
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == singlePlayerButton) {
            currentMode = 1; //single player mode
            numPlayersField.setText("1");
            numPlayersField.setEnabled(false); //disable changing num of players
            cardLayout.show(container, "setup");
        }
        else if (e.getSource() == multiPlayerButton) {
            currentMode = 2; //multiplayer mode
            numPlayersField.setEnabled(true); //enable changing num of players
            cardLayout.show(container, "setup");
        }
    }

    //setup handling logic
    private void handleSetupCompletion() {
        //reset feedback color before showing error
        feedbackLabel.setForeground(darkForeground);

        try {
            //get inputs
            int numPlayers = Integer.parseInt(numPlayersField.getText());
            if (numPlayers < 1) {
                throw new NumberFormatException("Must have at least 1 player!");
            }

            //only parse if the field is visible (relevant to the mode)
            if (numQuestionsField.isVisible()) {
                numQuestions = Integer.parseInt(numQuestionsField.getText());
            }
            else {
                numQuestions = 0; //not needed for this mode
            }

            if (timeLimitField.isVisible()) {
                timeLimit = Integer.parseInt(timeLimitField.getText());
            }
            else {
                timeLimit = 0; //not needed for this mode
            }

            selectedGameMode = gameModeDropdown.getSelectedIndex() + 1;

            //player setup
            users = new Player[numPlayers];
            for (int i = 0; i < numPlayers; i++) {
                String initialName = "player " + (i + 1);

                //ask user for the name
                String playerName = JOptionPane.showInputDialog(
                        this,
                        "Enter Player " + (i + 1) + "'s name:",
                        "Player Setup",
                        JOptionPane.QUESTION_MESSAGE
                );

                //check for null (canceled) or empty input using the initial/default name if needed
                if (playerName == null || playerName.trim().isEmpty()) {
                    playerName = initialName;
                }

                users[i] = new Player(playerName);
            }

            //initialize game state
            currentPlayerIndex = 0;
            currentQuestionCount = 0;
            livesRemaining = 3;

            //start the game
            cardLayout.show(container, "game");
            startNewTurn();

        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input error; please check the fields (number of players/questions/time limit) and ensure they are valid numbers", "input error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startNewTurn() {
        if (currentPlayerIndex >= users.length) {
            //all players finished show summary
            showSummary();
            return;
        }

        Player currentPlayer = users[currentPlayerIndex];
        turnLabel.setText("Player: " + currentPlayer.name);
        currentGame = new Game();
        currentGame.score = currentPlayer.score;
        currentGame.summary = currentPlayer.summary;

        currentQuestionCount = 0;

        //score label reflects the players total score before the first question
        scoreLabel.setText("Score: " + currentPlayer.score);

        if (selectedGameMode == 3) { //3 lives mode
            livesRemaining = 3;
            timeOrLivesLabel.setText("Lives: " + livesRemaining);
        }
        else if (selectedGameMode == 4) { //time trial mode
            timeOrLivesLabel.setText("Time: " + timeLimit + "s");
            startTimer();
        }
        else {
            timeOrLivesLabel.setText("");
        }

        generateNextQuestion();
    }

    private void generateNextQuestion() {
        //reset feedback color
        feedbackLabel.setForeground(darkForeground);

        if (selectedGameMode == 1 && currentQuestionCount >= numQuestions) {
            //end of mode 1 (make a wish)
            endTurn();
            return;
        }

        //generate a random operation choice (1-4)
        int opChoice = (int)(Math.random() * 4 + 1);

        //get question string from gamejava
        String question = currentGame.generateQuestionForGUI(opChoice);

        questionLabel.setText(question);
        feedbackLabel.setText("Enter your answer below.");
        answerField.setText("");
        answerField.requestFocusInWindow();
        currentQuestionCount++; //increment question count
    }

    //answer submission handling
    private void handleSubmitAnswer() {
        if (answerField.getText().trim().isEmpty()) {
            feedbackLabel.setForeground(errorColor);
            feedbackLabel.setText("Please enter an answer");
            return;
        }

        try {
            double userAnswer = Double.parseDouble(answerField.getText());
            boolean correct = currentGame.checkAnswer(userAnswer);

            if (correct) {
                feedbackLabel.setForeground(accentColor);
                feedbackLabel.setText("Correct (+1 point)");
                scoreLabel.setText("Score: " + currentGame.score);
                //all modes continue on correct answer
                generateNextQuestion();
            }
            else {
                feedbackLabel.setForeground(errorColor);
                feedbackLabel.setText("False; the correct answer was " + currentGame.getActualAnswer());

                if (selectedGameMode == 2) { //no mistakes mode
                    endTurn(); //end immediately on first mistake
                }
                else if (selectedGameMode == 3) { //take chances mode
                    livesRemaining--;
                    timeOrLivesLabel.setText("Lives: " + livesRemaining);
                    if (livesRemaining <= 0) {
                        endTurn(); //end if no lives remain
                    }
                    else {
                        generateNextQuestion(); //continue if lives remain
                    }
                }
                else {
                    //mode 1 and 4 continue on incorrect answer
                    generateNextQuestion();
                }
            }
        }
        catch (NumberFormatException ex) {
            feedbackLabel.setForeground(errorColor);
            feedbackLabel.setText("Invalid input; please enter a number");
            answerField.setText("");
            answerField.requestFocusInWindow();
        }
    }

    //timer logic for mode 4
    private void startTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new Timer(1000, new ActionListener() {
            private int timeElapsed = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                timeElapsed++;
                int timeLeft = timeLimit - timeElapsed;
                timeOrLivesLabel.setText("Time: " + timeLeft + "s");

                //flash the time label red when less than 10 seconds remain
                if (timeLeft <= 10) {
                    timeOrLivesLabel.setForeground(errorColor);
                }
                else {
                    timeOrLivesLabel.setForeground(darkForeground);
                }


                if (timeLeft <= 0) {
                    ((Timer) e.getSource()).stop();
                    JOptionPane.showMessageDialog(container, "Time's up! Your turn has ended");
                    endTurn(); //end the game
                }
            }
        });
        gameTimer.start();
    }

    private void endTurn() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        //save results to player object
        users[currentPlayerIndex].score = currentGame.score;
        users[currentPlayerIndex].summary = currentGame.summary;

        if (currentMode == 1) { //single player
            showSummary();
        }
        else { //multiplayer
            currentPlayerIndex++;
            if (currentPlayerIndex < users.length) {
                //prompt next player and start new turn
                JOptionPane.showMessageDialog(this, users[currentPlayerIndex].name + " it's your turn!");
                startNewTurn();
            }
            else {
                //all players finished show summary
                showSummary();
            }
        }
    }

    //summary and reset logic
    private void showSummary() {
        //sort the players for the leaderboard
        Arrays.sort(users, Comparator.comparingInt((Player user) -> user.score).reversed());

        StringBuilder summaryText = new StringBuilder();
        summaryText.append("--- Game Over ---\n");
        summaryText.append("Game mode: ").append(gameModeDropdown.getSelectedItem()).append("\n\n");

        if (currentMode == 1) {
            //single player summary
            summaryText.append("Total Score: ").append(users[0].score).append("\n");
            summaryText.append("\n--- Question Summary ---\n").append(users[0].summary);
        }
        else {
            //multiplayer leaderboard
            summaryText.append("--- Leaderboard ---\n");
            for (int i = 0; i < users.length; i++) {
                summaryText.append(String.format("%-3s", (i + 1) + ".")).append(String.format("%-20s", users[i].name)).append(": ").append(users[i].score);
                if (i == 0) summaryText.append(" (winner!)");
                summaryText.append("\n");
            }
            summaryText.append("\n--- Detailed Summary (All Players) ---\n");
            for (Player user : users) {
                summaryText.append("\nPlayer: ").append(user.name).append(" (Score: ").append(user.score).append(")\n");
                summaryText.append("Questions:\n").append(user.summary).append("\n");
            }
        }

        resultsArea.setText(summaryText.toString());
        cardLayout.show(container, "summary");
    }

    private void resetGame() {
        //reset variables
        currentMode = 0;
        selectedGameMode = 0;
        users = null;
        if (gameTimer != null) {
            gameTimer.stop();
        }

        //reset ui
        numPlayersField.setEnabled(true);
        gameModeDropdown.setSelectedIndex(0); //reset to default mode

        //go back to start screen
        cardLayout.show(container, "start");
    }

    //initializes gui on event dispatch thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main()); //run the gui on the event dispatch thread
    }
}