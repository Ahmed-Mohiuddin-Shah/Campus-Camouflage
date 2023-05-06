package TEST;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

public class RenderPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    static World world;
    static FrameBuffer buffer;
    Object3D box;
    Object3D plane, plane1;
    {

        world = new World();

        world.setAmbientLight(255, 255, 255);

        TextureManager.getInstance().addTexture("boxt", new Texture(128, 128, Color.PINK));

        box = Primitives.getBox(3f, 2f);

        box.setTexture("boxt");
        box.setTransparency(0);
        box.setEnvmapped(Object3D.ENVMAP_ENABLED);
        box.build();

        world.addObject(box);

        world.getCamera().setPosition(50, -20, -5);
        world.getCamera().lookAt(box.getTransformedCenter());
        buffer = new FrameBuffer(660, 530, FrameBuffer.SAMPLINGMODE_NORMAL);
    }

    public RenderPanel() {
        setPreferredSize(new Dimension(buffer.getOutputWidth(), buffer.getOutputHeight()));
        setFocusable(true);
        setLayout(null);

        buffer.clear(java.awt.Color.DARK_GRAY);
        world.renderScene(buffer);
        // world.drawWireframe(buffer, Color.WHITE);
        world.draw(buffer);
        buffer.update();
    }

    public static void rotateleftright(float v) {
        SimpleVector temp = world.getCamera().getPosition();
        float rotation = v * (float) Math.PI / 30;
        temp.rotateY(-rotation);

        world.getCamera().setPosition(temp);
        world.getCamera().lookAt(new SimpleVector(0, 0, 0));

        buffer.clear(java.awt.Color.DARK_GRAY);
        world.renderScene(buffer);
        // world.drawWireframe(buffer, Color.WHITE);
        world.draw(buffer);
        buffer.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        buffer.display(g);
    }

}