import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class Game implements ActionListener {
    JFrame frame;

    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

    JButton clientButton, serverButton, settingsButton, exitButton;

    Game() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        Font helloHeadline = new Font("", Font.PLAIN, 0);
        try {
            helloHeadline = Font.createFont(Font.TRUETYPE_FONT, new File("resources/HelloHeadline.ttf"))
                    .deriveFont(120f);
        } catch (Exception e) {
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(helloHeadline);

        frame = new JFrame("Campus Camouflage");
        frame.setUndecorated(true);
        frame.setResizable(false);
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        buttonPanel.setFont(helloHeadline);
        JLabel l = new JLabel("Campus Camouflage", JLabel.CENTER);
        l.setFont(helloHeadline);
        l.setBackground(Color.BLACK);

        try {
            helloHeadline = Font.createFont(Font.TRUETYPE_FONT, new File("resources/HelloHeadline.ttf"))
                    .deriveFont(36f);
        } catch (Exception e) {
        }

        ge.registerFont(helloHeadline);

        clientButton = new JButton("Connect to Server");
        if (Functions.isServerRunning) {
            serverButton = new JButton("Server on IP: " + Functions.serverIP + "    Port: " + Functions.serverPort);
        } else {
            serverButton = new JButton("Create Server");
        }
        settingsButton = new JButton("Settings");
        exitButton = new JButton("Exit");

        clientButton.addActionListener(this);
        serverButton.addActionListener(this);
        settingsButton.addActionListener(this);
        exitButton.addActionListener(this);

        clientButton.setFont(helloHeadline);
        serverButton.setFont(helloHeadline);
        settingsButton.setFont(helloHeadline);
        exitButton.setFont(helloHeadline);

        buttonPanel.add(clientButton);
        buttonPanel.add(serverButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(exitButton);

       
        
        frame.setLayout(new GridLayout(2, 1, 10, 10));

        frame.add(l);
        frame.add(buttonPanel);

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Enter full-screen mode
        device.setFullScreenWindow(frame);
        frame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());

    }

    public static void main(String[] args) {
        new Game();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Connect to Server":
                frame.dispose();
                new ClientCreationGUI();
                break;
            case "Create Server":
                new ServerCreationGUI();
                frame.dispose();
                break;
            case "Settings":
                new Settings();
                frame.dispose();
                System.exit(0);
                break;
            case "Exit":
                System.exit(0);
                break;
            default:
        }

        if (e.getActionCommand().contains("Server on")) {
            frame.dispose();
            device.setFullScreenWindow(Server.frame);
            Server.frame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());
        }

    }
}
