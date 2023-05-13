import com.threed.jpct.*;

public class test {
    static FrameBuffer buffer;
    public static void main(String[] args) throws InterruptedException {
        World world = new World();
        Functions.loadMenu(world);
        buffer = new FrameBuffer(1920, 1080, FrameBuffer.SAMPLINGMODE_NORMAL);
        buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
        buffer.enableRenderer(IRenderer.RENDERER_OPENGL);
        while (!org.lwjgl.opengl.Display.isCloseRequested()) {
            Functions.moveCamera(world);
            buffer.clear(java.awt.Color.TRANSLUCENT);
            world.renderScene(buffer);
            world.draw(buffer);
            buffer.update();
            buffer.displayGLOnly();
            Thread.sleep(10);
        }
        buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
        buffer.dispose();
        System.exit(0);

    }
    
}
