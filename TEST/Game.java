package TEST;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class Game implements ActionListener {
    private JFrame frame;

    private JButton clientButton, serverButton, settingsButton, exitButton;

    Game() {
        Font helloHeadline = new Font("", Font.PLAIN, 0);
        try {
            helloHeadline = Font.createFont(Font.TRUETYPE_FONT, new File("resources/HelloHeadline.ttf"))
                    .deriveFont(36f);
        } catch (Exception e) {
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(helloHeadline);

        frame = new JFrame("Campus Camouflage");
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));

        clientButton = new JButton("Connect to Server");
        serverButton = new JButton("Create Server");
        settingsButton = new JButton("Settings");
        exitButton = new JButton("Exit");

        clientButton.addActionListener(this);
        serverButton.addActionListener(this);
        settingsButton.addActionListener(this);
        exitButton.addActionListener(this);

        buttonPanel.add(clientButton);
        buttonPanel.add(serverButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(exitButton);

        JLabel l = new JLabel("Campus Camouflage", JLabel.CENTER);
        l.setFont(helloHeadline);
        l.setBackground(Color.BLACK);
        buttonPanel.setFont(helloHeadline);
        buttonPanel.setFont(helloHeadline);
        buttonPanel.setFont(helloHeadline);
        buttonPanel.setFont(helloHeadline);
        frame.setLayout(new GridLayout(3, 1, 10, 10));

        frame.add(l);
        frame.add(buttonPanel);
        frame.add(new RenderPanel());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        new Game();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Connect to Server":
                try {
                } catch (Exception e1) {
                }
                frame.dispose();
                break;
            case "Create Server":
                frame.dispose();
                break;
            case "Settings":
                // frame.dispose();
                break;
            case "Exit":
                System.exit(0);
                break;
            default:
        }

    }
    // clientButton = new JButton("Connect to Server");
    // serverButton = new JButton("Create Server");
    // settingsButton = new JButton("Settings");
    // exitButton = new JButton("Exit");
}
