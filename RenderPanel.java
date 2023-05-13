

import java.awt.*;
import javax.swing.*;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;

public class RenderPanel extends JPanel implements Runnable {
    private Canvas canvas;

    private FrameBuffer buffer;

    public boolean gameLoop = false;

    public RenderPanel() {
        super();
        buffer = new FrameBuffer(1920, 1080, FrameBuffer.SAMPLINGMODE_HARDWARE_ONLY);
        buffer.enableRenderer(IRenderer.RENDERER_OPENGL, IRenderer.MODE_OPENGL);
        buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
        canvas = buffer.enableGLCanvasRenderer();
        this.add(canvas);
        gameLoop = true;
    }

    @Override
    public void run() {

        while (gameLoop) {
            buffer.clear();
            buffer.update();
            buffer.display();
        }
        buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
    }

}