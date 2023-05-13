package TEST;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.threed.jpct.*;
import com.threed.jpct.util.*;

public class CollisionDemoSoftware extends JFrame implements ComponentListener {
    private JPanel panel;

    private static final long serialVersionUID = 1L;

    private static final float DAMPING = 0.1f;

    private static final float SPEED = 1f;

    private static final float MAXSPEED = 1f;

    private Graphics g = null;

    private KeyMapper keyMapper = null;

    private FrameBuffer fb = null;

    private World world = null;

    private Object3D plane = null;

    private Object3D ramp = null;

    private Object3D cube = null;

    private Object3D cube2 = null;

    private Object3D sphere = null;

    private boolean up = false;

    private boolean down = false;

    private boolean left = false;

    private boolean right = false;

    private boolean doloop = true;

    private SimpleVector moveRes = new SimpleVector(0, 0, 0);

    private SimpleVector ellipsoid = new SimpleVector(2, 2, 2);

    public CollisionDemoSoftware() {

        int numberOfProcs = Runtime.getRuntime().availableProcessors();

        Config.useMultipleThreads = numberOfProcs > 1;
        Config.useMultiThreadedBlitting = numberOfProcs > 1;
        Config.loadBalancingStrategy = 1;
        Config.maxNumberOfCores = numberOfProcs;
        Config.lightMul = 1;
        Config.mtDebug = true;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setVisible(true);
        g = getGraphics();

        panel = new JPanel();

        JButton btnTest = new JButton("Left");
        btnTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                panel.setVisible(false);
            }
        });

        JButton btnRight = new JButton("Right");
        btnRight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                repaint();
            }
        });

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            }
        });

        panel.setLayout(new GridLayout(3, 1));
        panel.add(btnTest);
        panel.add(btnRight);
        panel.add(btnClose);

        add(panel);
    }

    private void initStuff() {
        fb = new FrameBuffer(1024, 768, FrameBuffer.SAMPLINGMODE_HARDWARE_ONLY);
        world = new World();
        fb.enableRenderer(IRenderer.RENDERER_SOFTWARE);
        keyMapper = new KeyMapper(this);

        plane = Primitives.getPlane(20, 10);
        plane.rotateX((float) Math.PI / 2f);

        ramp = Primitives.getCube(20);
        ramp.rotateX((float) Math.PI / 2f);

        sphere = Primitives.getSphere(30);
        sphere.translate(-50, 10, 50);

        cube2 = Primitives.getCube(20);
        cube2.translate(60, -20, 60);

        cube = Primitives.getCube(2);
        cube.translate(-50, -10, -50);

        plane.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
        ramp.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
        sphere.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
        cube2.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
        cube.setCollisionMode(Object3D.COLLISION_CHECK_SELF);

        world.addObject(plane);
        world.addObject(ramp);
        world.addObject(cube);
        world.addObject(sphere);
        world.addObject(cube2);

        Light light = new Light(world);
        light.setPosition(new SimpleVector(0, -80, 0));
        light.setIntensity(140, 120, 120);
        light.setAttenuation(-1);

        world.setAmbientLight(20, 20, 20);

        world.buildAllObjects();
    }

    private void move() {
        KeyState ks = null;
        while ((ks = keyMapper.poll()) != KeyState.NONE) {
            if (ks.getKeyCode() == KeyEvent.VK_UP) {
                up = ks.getState();
            }
            if (ks.getKeyCode() == KeyEvent.VK_DOWN) {
                down = ks.getState();
            }
            if (ks.getKeyCode() == KeyEvent.VK_LEFT) {
                left = ks.getState();
            }
            if (ks.getKeyCode() == KeyEvent.VK_RIGHT) {
                right = ks.getState();
            }

            if (ks.getKeyCode() == KeyEvent.VK_ESCAPE) {
                doloop = false;
            }
        }

        // move the cube
        if (up) {
            SimpleVector t = cube.getZAxis();
            t.scalarMul(SPEED);
            moveRes.add(t);
        }

        if (down) {
            SimpleVector t = cube.getZAxis();
            t.scalarMul(-SPEED);
            moveRes.add(t);
        }

        if (left) {
            cube.rotateY((float) Math.toRadians(-1));
        }

        if (right) {
            cube.rotateY((float) Math.toRadians(1));
        }

        // avoid high speeds
        if (moveRes.length() > MAXSPEED) {
            moveRes.makeEqualLength(new SimpleVector(0, 0, MAXSPEED));
        }

        cube.translate(0, -0.02f, 0);

        moveRes = cube.checkForCollisionEllipsoid(moveRes, ellipsoid, 8);
        cube.translate(moveRes);

        // finally apply the gravity:
        SimpleVector t = new SimpleVector(0, 1, 0);
        t = cube.checkForCollisionEllipsoid(t, ellipsoid, 1);
        cube.translate(t);

        // damping
        if (moveRes.length() > DAMPING) {
            moveRes.makeEqualLength(new SimpleVector(0, 0, DAMPING));
        } else {
            moveRes = new SimpleVector(0, 0, 0);
        }
    }

    private void doIt() throws Exception {

        Camera cam = world.getCamera();
        cam.moveCamera(Camera.CAMERA_MOVEOUT, 100);
        cam.moveCamera(Camera.CAMERA_MOVEUP, 100);
        cam.lookAt(ramp.getTransformedCenter());

        long start = System.currentTimeMillis();
        long fps = 0;

        while (doloop) {
            move();

            cam.setPositionToCenter(cube);
            cam.align(cube);
            cam.rotateCameraX((float) Math.toRadians(30));
            cam.moveCamera(Camera.CAMERA_MOVEOUT, 100);

            fb.clear(Color.RED);
            world.renderScene(fb);
            world.draw(fb);

            fb.update();
            fb.display(g);
            fps++;
            if (System.currentTimeMillis() - start >= 1000) {
                start = System.currentTimeMillis();
                System.out.println(fps);
                fps = 0;
            }

        }
        fb.disableRenderer(IRenderer.RENDERER_SOFTWARE);
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        CollisionDemoSoftware cd = new CollisionDemoSoftware();
        cd.initStuff();
        cd.doIt();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        fb.resize(getWidth(), getHeight());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
