import java.awt.*;

import javax.swing.*;

import com.threed.jpct.*;

import java.awt.event.*;
import java.io.File;

public class GameClient implements KeyListener {
    private JFrame pauseFrame, gameFrame;
    FrameBuffer buffer;
    GraphicsDevice device;
    Canvas canvas;
    boolean gameLoop;
    Thread gameThread;

    public GameClient() {

        Font helloHeadline = new Font("", Font.PLAIN, 0);
        try {
            helloHeadline = Font.createFont(Font.TRUETYPE_FONT, new File("resources/HelloHeadline.ttf"))
                    .deriveFont(36f);
        } catch (Exception e) {
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(helloHeadline);

        pauseFrame = new JFrame("Campus Camouflage");

        pauseFrame.setUndecorated(true);
        pauseFrame.setResizable(false);

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        pauseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pauseFrame.setVisible(true);

        // // Enter full-screen mode
        // device.setFullScreenWindow(frame);
        pauseFrame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());

        gameFrame = new JFrame("Campus Camouflage");

        gameFrame.setUndecorated(true);
        gameFrame.setResizable(false);

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);

        // // Enter full-screen mode
        device.setFullScreenWindow(gameFrame);
        gameFrame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());

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
        buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
        canvas = buffer.enableGLCanvasRenderer();

        gameFrame.add(canvas, BorderLayout.CENTER);
        canvas.requestFocus();
        canvas.addKeyListener(this);
        gameLoop = true;

        gameThread = new Thread(new GameLoop());
        gameThread.start();
    }

    World w = new World();

    private void gameLoop() {
        while (gameLoop) {
            buffer.clear(java.awt.Color.ORANGE);
            w.renderScene(buffer);
            w.draw(buffer);
            buffer.update();
            buffer.display(canvas.getGraphics());
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
            }
        }
    }

    public class GameLoop implements Runnable {

        @Override
        public void run() {
            gameLoop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'p':
                device.setFullScreenWindow(null);
                device.setFullScreenWindow(pauseFrame);

                System.out.println("afoijfa");
                break;
            default:
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}