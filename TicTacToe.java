package com.mycompany.tictactoe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToe extends Frame implements ActionListener {
    private Button[] buttons = new Button[9];
    private char currentPlayer = 'X';
    private int xWins = 0, oWins = 0, draws = 0;
    private Label scoreLabel;
    private String playerXName = "Player X";
    private String playerOName = "Player O";
    private Dialog startDialog;
    private TextField playerXNameField;
    private TextField playerONameField;
    private Button startButton;
    private Timer animationTimer;
    private int animationStep = 0;

    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setLayout(new BorderLayout());

        initializeStartDialog();
        initializeGamePanel();
        initializeScorePanel();
        initializeFooterPanel();

        setSize(300, 350);
        setVisible(true);

        startDialog.setVisible(true);
    }

    private void initializeStartDialog() {
        startDialog = new Dialog(this, "Enter Player Names", true);
        startDialog.setLayout(new FlowLayout());
        startDialog.setSize(300, 150);

        startDialog.add(new Label("Player X Name:"));
        playerXNameField = new TextField(10);
        startDialog.add(playerXNameField);

        startDialog.add(new Label("Player O Name:"));
        playerONameField = new TextField(10);
        startDialog.add(playerONameField);

        startButton = new Button("Start Now");
        startButton.addActionListener(e -> startGame());
        startDialog.add(startButton);
    }

    private void initializeGamePanel() {
        Panel gamePanel = new Panel();
        gamePanel.setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 9; i++) {
            buttons[i] = new Button("");
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 60));
            buttons[i].setBackground(Color.WHITE);
            buttons[i].addActionListener(this);
            gamePanel.add(buttons[i]);
        }
        add(gamePanel, BorderLayout.CENTER);
    }

    private void initializeScorePanel() {
        Panel scorePanel = new Panel();
        scorePanel.setLayout(new FlowLayout());
        scorePanel.setBackground(Color.DARK_GRAY);
        scoreLabel = new Label("X Wins: 0 | O Wins: 0 | Draws: 0");
        scoreLabel.setForeground(Color.WHITE);
        scorePanel.add(scoreLabel);
        add(scorePanel, BorderLayout.NORTH);
    }

    private void initializeFooterPanel() {
        Panel footerPanel = new Panel();
        footerPanel.setLayout(new FlowLayout());
        footerPanel.setBackground(Color.DARK_GRAY);

        Button resetButton = new Button("Reset Game");
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(Color.BLACK);
        resetButton.addActionListener(e -> resetGame());
        footerPanel.add(resetButton);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void startGame() {
        playerXName = playerXNameField.getText().isEmpty() ? "Player X" : playerXNameField.getText();
        playerOName = playerONameField.getText().isEmpty() ? "Player O" : playerONameField.getText();
        scoreLabel.setText(playerXName + " (X) Wins: 0 | " + playerOName + " (O) Wins: 0 | Draws: 0");
        startDialog.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Button buttonClicked = (Button) e.getSource();
        if (buttonClicked.getLabel().equals("")) {
            buttonClicked.setLabel(String.valueOf(currentPlayer));
            buttonClicked.setForeground(currentPlayer == 'X' ? Color.ORANGE : Color.BLACK);
            if (checkWinner()) {
                showWinnerAnimation();
                updateScore();
                resetBoard();
            } else if (isDraw()) {
                showDrawDialog();
                draws++;
                resetBoard();
            } else {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            }
        }
    }

    private boolean checkWinner() {

        for (int i = 0; i < 3; i++) {
            if (checkLine(buttons[i * 3], buttons[i * 3 + 1], buttons[i * 3 + 2]) ||
                checkLine(buttons[i], buttons[i + 3], buttons[i + 6])) {
                return true;
            }
        }
        return checkLine(buttons[0], buttons[4], buttons[8]) || checkLine(buttons[2], buttons[4], buttons[6]);
    }

    private boolean checkLine(Button b1, Button b2, Button b3) {
        return !b1.getLabel().equals("") &&
               b1.getLabel().equals(b2.getLabel()) &&
               b2.getLabel().equals(b3.getLabel());
    }

    private boolean isDraw() {
        for (Button button : buttons) {
            if (button.getLabel().equals("")) {
                return false;
            }
        }
        return true;
    }

    private void showWinnerAnimation() {
        Button[] winningLine = getWinningLine();
        if (winningLine == null) return;

        animationStep = 0;
        animationTimer = new Timer(200, new ActionListener() {
            private boolean colorFlag = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (animationStep < 10) {
                    for (Button button : winningLine) {
                        button.setBackground(colorFlag ? Color.ORANGE : Color.BLACK);
                    }
                    colorFlag = !colorFlag;
                    animationStep++;
                } else {
                    animationTimer.stop();
                    for (Button button : winningLine) {
                        button.setBackground(null);
                    }
                    showWinnerDialog();
                }
            }
        });
        animationTimer.start();
    }

    private Button[] getWinningLine() {
        for (int i = 0; i < 3; i++) {
            if (checkLine(buttons[i * 3], buttons[i * 3 + 1], buttons[i * 3 + 2])) {
                return new Button[]{buttons[i * 3], buttons[i * 3 + 1], buttons[i * 3 + 2]};
            }
            if (checkLine(buttons[i], buttons[i + 3], buttons[i + 6])) {
                return new Button[]{buttons[i], buttons[i + 3], buttons[i + 6]};
            }
        }
        if (checkLine(buttons[0], buttons[4], buttons[8])) {
            return new Button[]{buttons[0], buttons[4], buttons[8]};
        }
        if (checkLine(buttons[2], buttons[4], buttons[6])) {
            return new Button[]{buttons[2], buttons[4], buttons[6]};
        }
        return null;
    }

    private void showWinnerDialog() {
        Dialog dialog = new Dialog(this, "Winner", true);
        dialog.setLayout(new FlowLayout());
        String winnerName = (currentPlayer == 'X') ? playerXName : playerOName;
        Label messageLabel = new Label(winnerName + " (" + currentPlayer + ") wins!");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dialog.add(messageLabel);
        Button okButton = new Button("OK");
        okButton.addActionListener(e -> dialog.setVisible(false));
        dialog.add(okButton);
        dialog.setSize(300, 150);

        Timer messageTimer = new Timer(100, new ActionListener() {
            private int fontSize = 16;
            private boolean growing = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (growing) {
                    fontSize++;
                    if (fontSize > 24) {
                        growing = false;
                    }
                } else {
                    fontSize--;
                    if (fontSize < 16) {
                        growing = true;
                    }
                }
                messageLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
            }
        });
        messageTimer.start();

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                messageTimer.stop();
                dialog.setVisible(false);
            }
        });

        dialog.setVisible(true);
    }

    private void showDrawDialog() {
        Dialog dialog = new Dialog(this, "Draw", true);
        dialog.setLayout(new FlowLayout());
        dialog.add(new Label("NO WINNER"));
        Button okButton = new Button("OK");
        okButton.addActionListener(e -> dialog.setVisible(false));
        dialog.add(okButton);
        dialog.setSize(200, 100);
        dialog.setVisible(true);
    }

    private void updateScore() {
        if (currentPlayer == 'X') {
            xWins++;
        } else {
            oWins++;
        }
        updateScoreLabel();
    }

    private void updateScoreLabel() {
        scoreLabel.setText(playerXName + " (X) Wins: " + xWins + " | " + playerOName + " (O) Wins: " + oWins + " | Draws: " + draws);
    }

    private void resetBoard() {
        for (Button button : buttons) {
            button.setLabel("");
            button.setBackground(null);
        }
        currentPlayer = 'X';
    }

    private void resetGame() {
        resetBoard();
        xWins = 0;
        oWins = 0;
        draws = 0;
        updateScoreLabel();
    }

    public static void main(String[] args) {
        new TicTacToe();
    }
}