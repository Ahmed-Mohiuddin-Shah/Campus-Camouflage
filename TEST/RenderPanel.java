package TEST;

import java.awt.*;
import javax.swing.*;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;

public class RenderPanel extends JPanel {
    private Canvas canvas;

    private FrameBuffer buffer;

    public RenderPanel() {
        super();
        buffer = new FrameBuffer(1920, 1080, FrameBuffer.SAMPLINGMODE_HARDWARE_ONLY);
        buffer.enableRenderer(IRenderer.RENDERER_OPENGL, IRenderer.MODE_OPENGL);
        buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
        canvas = buffer.enableGLCanvasRenderer();
        this.add(canvas);
    }

}