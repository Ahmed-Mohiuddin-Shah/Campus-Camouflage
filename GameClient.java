import java.awt.*;

import javax.swing.*;

import com.threed.jpct.FrameBuffer;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.File;

public class GameClient {
    private JPanel panel;
    private JFrame frame;
    FrameBuffer buffer;

    public GameClient() {
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