import java.awt.*;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.File;

public class GameClient {
    private JPanel panel;
    private JFrame frame;

    public GameClient() throws Exception {
        Font helloHeadline = new Font("", Font.PLAIN, 0);
        try {
            helloHeadline = Font.createFont(Font.TRUETYPE_FONT, new File("resources/HelloHeadline.ttf"))
                    .deriveFont(36f);
        } catch (Exception e) {
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(helloHeadline);

        frame = new JFrame("Log Screen");

        frame.setUndecorated(true);
        frame.setResizable(false);

        
        

        JButton btnTest = new JButton("Left");
        btnTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                panel.setVisible(false);
            }
        });

        JButton btnRight = new JButton("Right");
        btnRight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                repaint();
            }
        });

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                gameWindow.gameLoop = false;
                frame.dispose();
                new Game();
            }
        });

        panel.setLayout(new GridLayout(3, 1));
        panel.add(btnTest);
        panel.add(btnRight);
        panel.add(btnClose);
        panel.addKeyListener(this);

        add(panel, BorderLayout.WEST);

        gameWindow = new RenderPanel();
        gameWindow.addKeyListener(this);
        add(gameWindow, BorderLayout.CENTER);
        add(panel, BorderLayout.EAST);

    }

    public static void makeFrame() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    frame = new GameClient();
                    frame.setVisible(true);
                    frame.setUndecorated(true);
                    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    device.setFullScreenWindow(frame);
                    frame.setSize(1920, 1080);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'p') {
            panel.setVisible(panel.isVisible() ? false : true);
            System.out.println("Hello");

        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}