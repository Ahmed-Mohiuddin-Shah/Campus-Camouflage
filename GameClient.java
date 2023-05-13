import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;

public class GameClient extends JFrame implements KeyListener {
    private JPanel panel;
    static GameClient frame;
    static RenderPanel gameWindow;

    public GameClient() throws Exception {
        panel = new JPanel();
        setSize(1920, 1080);
        

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

                    // Get the default graphics device
                    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

                    // Enter full-screen mode
                    device.setFullScreenWindow(frame);

                    // Exit full-screen mode
                    // device.setFullScreenWindow(null);

                    // Set the size of the frame to match the screen size
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