import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.threed.jpct.util.KeyMapper;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;

public class GameClient extends JFrame implements KeyListener {
    private JPanel panel;
    static GameClient frame;
    static MainGamePanel gameWindow;

    public GameClient() throws Exception {
        panel = new JPanel();

        setLayout(new BorderLayout(5, 5));
        setSize(1280, 720);
        setResizable(false);

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
                gameWindow.stop();
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

        gameWindow = new MainGamePanel();
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