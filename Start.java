import com.threed.jpct.*;
import com.threed.jpct.util.Light;

public class Start {
    World world;
    Object3D[] box;
    Object3D test;

    public static void main(String[] args) {
        new Start();
    }

    public Start() {

        world = new World();

        Light l = new Light(world);
        l.setPosition(new SimpleVector(50, 50, -5));

        TextureManager.getInstance().addTexture("plane", new Texture("tp\\expo_steel.png"));

        TextureManager.getInstance().addTexture("box", new Texture("tp\\expo_bamboo.png"));

        box = Loader.load3DS("C:\\Users\\Ahmed\\Documents\\Java Game Project\\Hide and SEECS\\untitled.3ds", 2f);
        int i = 0;
        for (Object3D object3d : box) {
            i = i + 1;
            if (i == 0)
                object3d.setTexture("plane");
            else
                object3d.setTexture("box");
            object3d.setEnvmapped(true);
            object3d.rotateX((float) (90 * 3.14 / 180));
            object3d.build();

            world.addObject(object3d);
        }

        world.getCamera().setPosition(50, -50, -5);
        world.getCamera().lookAt(box[0].getTransformedCenter());

        FrameBuffer buffer = new FrameBuffer(800, 600, FrameBuffer.SAMPLINGMODE_NORMAL);
        buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
        buffer.enableRenderer(IRenderer.RENDERER_OPENGL);

        while (!org.lwjgl.opengl.Display.isCloseRequested()) {
            buffer.clear(java.awt.Color.white);
            i += 0.0001;
            world.getCamera().rotateCameraX(i);
            world.renderScene(buffer);
            world.draw(buffer);
            buffer.update();
            buffer.displayGLOnly();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
        buffer.dispose();
        System.exit(0);
    }
}