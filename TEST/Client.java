package TEST;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.RGBColor;
import com.threed.jpct.util.AWTGLRenderer;

public class JpctFullscreenExample {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private static final int FPS = 60;

    private static JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create a JFrame with full-screen settings
                frame = new JFrame();
                frame.setUndecorated(true);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);

                // Create a hardware renderer using the AWTGLRenderer
                AWTGLRenderer renderer = new AWTGLRenderer(frame, AWTGLRenderer.RENDERER_HWA);

                // Create a jPCT framebuffer
                FrameBuffer buffer = new FrameBuffer(WIDTH, HEIGHT, FrameBuffer.SAMPLINGMODE_NORMAL);

                // Create a dummy object to display in the framebuffer
                // Here we just set the color to red
                RGBColor red = new RGBColor(255, 0, 0);
                buffer.clear(red);

                // Add the framebuffer to the renderer
                renderer.addFrameBuffer(buffer);

                // Start the rendering loop
                while (true) {
                    buffer.clear(red);
                    // Render the object to the framebuffer
                    // Here you can render your own scene using jPCT
                    buffer.display();

                    // Update the frame
                    renderer.paint(frame.getContentPane().getGraphics());

                    // Wait for the specified FPS
                    try {
                        Thread.sleep(1000 / FPS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
