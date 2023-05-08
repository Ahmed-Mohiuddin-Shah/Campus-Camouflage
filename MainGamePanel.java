import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Quaternion;

import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.KeyMapper;
import com.threed.jpct.util.KeyState;
import com.threed.jpct.util.Light;

public class MainGamePanel extends JPanel implements Runnable, MouseMotionListener {
    private static final long serialVersionUID = 1L;

    private String[] texturesJPG = { "dome", "grass", "monk", "wall" };

    private String[] texturesPNG = { "cop", "banana", "frnchr" };

    private static final float DAMPING = 0.1f;

    private static final float SPEED = 3f;

    private static final float MAXSPEED = 4f;

    private float xAngle = 0;

    private KeyMapper keyMapper = null;
    private Mouse mouse;

    private Graphics g;

    private Object3D player = null;

    private Object3D mouseCube;

    private Object3D[] map;

    private boolean forward = false;
    private boolean backward = false;

    private boolean up = false;

    private boolean down = false;

    private boolean left = false;

    private boolean right = false;

    private boolean gameLoop = true;

    private boolean doLoop = true;
    private Camera cam;

    private int mouseX;
    private int mouseY;

    private SimpleVector moveRes = new SimpleVector(0, 0, 0);

    private SimpleVector ellipsoid = new SimpleVector(2, 2, 2);

    private Ticker ticker = new Ticker(15);

    boolean runGame;

    private SimpleVector mousePositionInWorld;

    static World world;
    static FrameBuffer buffer;
    Object3D box;
    Object3D plane, plane1;
    {

        mouseX = 0;
        mouseY = 0;

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
        buffer = new FrameBuffer(1280, 720, FrameBuffer.SAMPLINGMODE_NORMAL);
        buffer.enableRenderer(IRenderer.RENDERER_SOFTWARE);

        mousePositionInWorld = getMouseWorldPosition();
        mouseCube = Primitives.getSphere(2f);
    }

    public MainGamePanel() {
        int numberOfProcs = Runtime.getRuntime().availableProcessors();

        Config.useMultipleThreads = numberOfProcs > 1;
        Config.useMultiThreadedBlitting = numberOfProcs > 1;
        Config.loadBalancingStrategy = 1;
        Config.maxNumberOfCores = numberOfProcs;
        Config.lightMul = 1;
        Config.mtDebug = true;
        setVisible(true);

        runGame = true;

        setPreferredSize(new Dimension(buffer.getOutputWidth(), buffer.getOutputHeight()));
        setFocusable(true);
        setLayout(null);

        addMouseMotionListener(this);
        Thread renderThread = new Thread(this);
        renderThread.start();
    }

    @Override
    public void run() {
        initStuff();

        cam = world.getCamera();
        cam.moveCamera(Camera.CAMERA_MOVEOUT, 100);
        cam.lookAt(player.getTransformedCenter());

        long start = System.currentTimeMillis();
        long fps = 0;

        g = getGraphics();

        long ticks = 0;

        keyMapper = new KeyMapper(this);

        while (gameLoop) {

            mouseCube.clearTranslation();
            mouseCube.translate(getMouseWorldPosition());

            cam.setPositionToCenter(player);

            // cam.align(player);
            cam.moveCamera(Camera.CAMERA_MOVEOUT, 200);
            cam.moveCamera(Camera.CAMERA_MOVEUP, 100);

            buffer.clear(java.awt.Color.DARK_GRAY);
            world.renderScene(buffer);
            world.draw(buffer);
            buffer.update();
            buffer.display(g);
            fps++;
            if (System.currentTimeMillis() - start >= 1000) {
                start = System.currentTimeMillis();
                System.out.println(fps);
                fps = 0;
            }

        }
        buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);

    }

    private void initStuff() {

        for (int i = 0; i < texturesJPG.length; ++i) {

            TextureManager.getInstance().addTexture(texturesJPG[i] + ".jpg",
                    new Texture("resources/" + texturesJPG[i] + ".jpg"));
        }

        for (int i = 0; i < texturesPNG.length; ++i) {

            TextureManager.getInstance().addTexture(texturesPNG[i] + ".png",
                    new Texture("resources/" + texturesPNG[i] + ".png"));
        }

        map = Loader.load3DS("resources\\testMap.3ds", 1f);

        for (Object3D object3d : map) {

            object3d.setCenter(SimpleVector.ORIGIN);
            object3d.rotateX((float) -Math.PI / 2);
            object3d.rotateMesh();
            object3d.setRotationMatrix(new Matrix());
            if (object3d.getName().contains("Player")) {
                player = object3d;
            } else {
                object3d.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
                world.addObject(object3d);
            }
        }

        player.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
        player.invertCulling(true);

        ellipsoid = new SimpleVector(5, 25, 5);

        world.addObject(player);
        world.addObject(mouseCube);

        Light light = new Light(world);
        light.setPosition(new SimpleVector(0, -80, 0));
        light.setIntensity(140, 120, 120);
        light.setAttenuation(-1);

        world.setAmbientLight(20, 20, 20);

        world.buildAllObjects();
    }

    public void stop() {
        gameLoop = false;
    }

    private static class Ticker {

        private int rate;
        private long s2;

        public static long getTime() {
            return System.currentTimeMillis();
        }

        public Ticker(int tickrateMS) {
            rate = tickrateMS;
            s2 = Ticker.getTime();
        }

        public int getTicks() {
            long i = Ticker.getTime();
            if (i - s2 > rate) {
                int ticks = (int) ((i - s2) / (long) rate);
                s2 += (long) rate * ticks;
                return ticks;
            }
            return 0;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {

        int dx = e.getX() - mouseX;
        int dy = e.getY() - mouseY;

        rotateCamera(dx, dy);
        mouseX = e.getX();
        mouseY = e.getY();

        // Rotate the camera based on the mouse movement
    }

    private float xRotation = 0;
    private float desiredX;

    private void rotateCamera(int dx, int dy) {
        float MOUSE_SENSITIVITY = 0.02f;
        // Calculate the rotation angles based on the mouse movement
        float yaw = dx * MOUSE_SENSITIVITY;
        float pitch = -dy * MOUSE_SENSITIVITY;

        // float dx = Input.GetAxis("Mouse X") * sensitivity * Time.fixedDeltaTime *
        // sensMultiplier;
        // float mouseY = Input.GetAxis("Mouse Y") * sensitivity * Time.fixedDeltaTime *
        // sensMultiplier;

        // Find current look rotation
        SimpleVector rot = cam.getDirection();
        desiredX = rot.y + dx;

        // Rotate, and also make sure we dont over- or under-rotate.
        xRotation -= mouseY;

        if (xRotation > 90) {
            xRotation = 90;
        }
        if (xRotation < -90) {
            xRotation = -90;
        }

        // Perform the rotations
        cam.rotateX(xRotation);
        cam.rotateY(desiredX);

    }

    private SimpleVector getMouseWorldPosition() {
        SimpleVector pos = new SimpleVector(2, -2, 2);
        SimpleVector ray = Interact2D.reproject2D3DWS(world.getCamera(), buffer, mouseX, mouseY);
        if (ray != null) {
            SimpleVector norm = ray.normalize(); // Just to be sure...

            float f = world.calcMinDistance(world.getCamera().getPosition(), norm, 1000);
            if (f != Object3D.COLLISION_NONE) {
                SimpleVector offset = new SimpleVector(norm);
                norm.scalarMul(f);
                norm = norm.calcSub(offset);
                pos = new SimpleVector(norm);
                pos.add(world.getCamera().getPosition());
                pos.add(new SimpleVector(2, -2, 2));
            } else {
                pos.add(new SimpleVector(0, 0, 0));
            }
        }
        return pos;
    }

    // private void rotateCamera(int dx, int dy) {

    // float MOUSE_SENSITIVITY = 1;
    // // Calculate the rotation angles based on the mouse movement
    // float yaw = dx * MOUSE_SENSITIVITY;
    // float pitch = -dy * MOUSE_SENSITIVITY;

    // // Create quaternions for the rotation angles
    // Quaternion yawQuat = new Quaternion();
    // ((Object) yawQuat).fromAxisAngle(new SimpleVector(0, 1, 0), yaw);
    // Quaternion pitchQuat = new Quaternion();
    // ((Object) pitchQuat).fromAxisAngle(new SimpleVector(1, 0, 0), pitch);

    // // Multiply the quaternions by the current orientation of the camera
    // Quaternion orientation = cam.getOrientation();
    // orientation = ((Object) yawQuat).multiply(orientation);
    // orientation = ((Object) orientation).multiply(pitchQuat);

    // // Set the new orientation of the cam
    // cam.setOrientation(orientation);
    // }
}