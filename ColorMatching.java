import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;


public class ColorMatching {
    private static final int APPLICATION_SIZE = 400;
    private static final Color BACKGROUND = new JLabel().getBackground();
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 36);
    private static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 24);
    private int score = 0;
    private JLabel timerLabel;

    public static void main(String[] args) {
        new ColorMatching().runHomeScreen();
    }
    private void runHomeScreen() {
        JFrame homeScreen = new JFrame("Color Matching Game");
        homeScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeScreen.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Color Matching Game", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        homeScreen.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        JButton startButton = new JButton(new AbstractAction("Start") {
            @Override
            public void actionPerformed(ActionEvent e) {
                homeScreen.dispose();
                runGame();
            }
        });
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setFont(BUTTON_FONT);
        buttonsPanel.add(startButton);
        
        JButton exitButton = new JButton(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitButton.setPreferredSize(new Dimension(200, 50));
        exitButton.setFont(BUTTON_FONT);
        buttonsPanel.add(exitButton);
        
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        wrapperPanel.add(buttonsPanel, gbc);
        
        homeScreen.add(wrapperPanel, BorderLayout.CENTER);
        
        homeScreen.setPreferredSize(new Dimension(400, 400));
        homeScreen.pack();
        homeScreen.setLocationRelativeTo(null);
        homeScreen.setVisible(true);
    }
    

    private void runGame() {
        JFrame application = new JFrame("color game");
        application.setTitle("Color Matching");
        JLabel scoreLabel = new JLabel("score: " + score);
        timerLabel = new JLabel("Time: " + 30);

        Color[] colors = { Color.BLUE, Color.CYAN, Color.RED, Color.YELLOW, Color.GREEN, Color.BLACK };
        List<JButton> fieldCells = initializeGame(colors);
        JPanel gameField = initializeView(fieldCells);
        bindViewToModel(colors, fieldCells, scoreLabel);
        JPanel gameControl = setupController(colors, fieldCells, application, scoreLabel);
        application.getContentPane().add(gameField);
        application.getContentPane().add(gameControl, BorderLayout.NORTH);
        application.setSize(APPLICATION_SIZE, 400);
        application.setVisible(true);

        // Add the timer
    Timer timer = new Timer(1000, new AbstractAction() {
        int timeLeft = 30;

        @Override
        public void actionPerformed(ActionEvent e) {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);
            if (timeLeft == 0) {
                endGame(application);
            }
        }
    });
    timer.start();
}


    private JPanel setupController(Color[] colors, List<JButton> fieldCells, JFrame application, JLabel scoreLabel) {
        JPanel gameControl = new JPanel(new GridLayout(1, 0));
        gameControl.add(new JButton(new AbstractAction("Restart") {
            @Override
            public void actionPerformed(ActionEvent e) {
                application.dispose();
                score = 0;
                runGame();
            }
        }));
        gameControl.add(scoreLabel);
        gameControl.add(new JButton(new AbstractAction("Quit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }));
        gameControl.add(timerLabel); // Add the timer label
        return gameControl;
    }


    private void bindViewToModel(Color[] colors, List<JButton> fieldCells, JLabel scoreLabel) {
        Collection<JButton> clickedButtons = new HashSet<>(); // Model
        Collections
        .shuffle(fieldCells);
        Iterator<JButton> randomCells = fieldCells.iterator();
        for (Color color : colors) {
        AbstractAction buttonAction = createButtonAction(clickedButtons, color, scoreLabel);
        bindButton(buttonAction, randomCells.next());
        bindButton(buttonAction, randomCells.next());
    }
    clickedButtons.clear();
    score = 0;
}

private void bindButton(AbstractAction buttonAction, JButton jButton) {
    jButton.setAction(buttonAction);
    jButton.setBackground(BACKGROUND);
}

private JPanel initializeView(List<JButton> fieldCells) {
    JPanel gameField = new JPanel(new GridLayout(4, 0));
    for (JButton fieldCell : fieldCells) {
        fieldCell.setBackground(BACKGROUND);
        fieldCell.setEnabled(true);
        gameField.add(fieldCell);
    }
    return gameField;
}

private List<JButton> initializeGame(Color[] colors) {
    List<JButton> fieldCells = new ArrayList<>();
    for (Color color : colors) {
        fieldCells.add(new JButton()); // two buttons per color
        fieldCells.add(new JButton());
    }
    return fieldCells;
}

private AbstractAction createButtonAction(Collection<JButton> clickedButtons, Color color, JLabel scoreLabel) {
    AbstractAction buttonAction = new AbstractAction() { // Controller
        Collection<JButton> clickedPartners = new HashSet<>(); // also Model

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton thisButton = (JButton) e.getSource();
            clickedPartners.add(thisButton);
            clickedButtons.add(thisButton);
            thisButton.setBackground(color);
            thisButton.setEnabled(false);
            if (2 == clickedButtons.size()) { // is second clicked
                if (2 == clickedPartners.size()) { // user found partner
                    score += 5;
                    if (score == 30) {
                        JOptionPane.showMessageDialog(thisButton, "Congrats!");
                        System.exit(0);
                    }
                } else {
                    JOptionPane.showMessageDialog(thisButton, "Try again");
                    for (JButton partner : clickedButtons) {
                        partner.setBackground(BACKGROUND);
                        partner.setEnabled(true);
                    }
                }
                clickedButtons.clear();
                clickedPartners.clear();
                scoreLabel.setText("Score: " + score);
            }
        }
    };
    return buttonAction;
}

private void endGame(Object parentComponent) {
    JOptionPane.showMessageDialog((JFrame) parentComponent, "Time's up!");
    System.exit(0);
}
}