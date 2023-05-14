import java.awt.*;

import javax.swing.*;

import com.threed.jpct.*;

import java.awt.event.*;
import java.io.File;

public class GameClient {
    private JPanel panel;
    private JFrame frame;
    FrameBuffer buffer;
    GraphicsDevice device;

    public GameClient() {
        Font helloHeadline = new Font("", Font.PLAIN, 0);
        try {
            helloHeadline = Font.createFont(Font.TRUETYPE_FONT, new File("resources/HelloHeadline.ttf"))
                    .deriveFont(36f);
        } catch (Exception e) {
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(helloHeadline);

        frame = new JFrame("Campus Camouflage");

        frame.setUndecorated(true);
        frame.setResizable(false);

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // // Enter full-screen mode
        // device.setFullScreenWindow(frame);
        frame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());

        int maxWidth = 800;
        int maxHeight = 600;
        for (VideoMode vMode : FrameBuffer.getVideoModes(IRenderer.RENDERER_OPENGL)) {
            

            if (maxWidth < vMode.width) {
                maxWidth = vMode.width;
                maxHeight = vMode.height;
                System.out.println("" + vMode.width + ", " + vMode.height);
            }

        }

        buffer = new FrameBuffer(maxWidth, maxHeight, FrameBuffer.SAMPLINGMODE_HARDWARE_ONLY);
        buffer.enableRenderer(IRenderer.RENDERER_OPENGL, IRenderer.MODE_OPENGL);
        buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);

    }

}