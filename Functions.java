import com.threed.jpct.*;
import com.threed.jpct.util.*;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Functions {
    static KeyMapper keyMapper = new KeyMapper();

    static boolean up = false;

    static boolean down = false;

    static boolean left = false;

    static boolean right = false;

    static String[] texturesJPG = { "dome", "grass", "monk", "wall", "bckdrp", "sky" };

    static String[] texturesPNG = { "cop", "banana", "frnchr" };

    static boolean isServerRunning = false;

    static String serverIP = "";

    static String serverPort = "";

    static String mapName = "testMap";

    static SimpleVector homePosition = new SimpleVector(150, 15, -180);

    public static String simpleVectorToString(SimpleVector simpleVector) {
        return simpleVector.x + "," + simpleVector.y + "," + simpleVector.z;
    }

    public static SimpleVector stringToSimpleVector(String string) {
        String[] tokens = string.split(",");
        return new SimpleVector(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]),
                Double.parseDouble(tokens[2]));
    }

    public static void loadMap(World world, String mapName, Object3D player, Object3D[] props) {
        ArrayList<Object3D> propArrayList = new ArrayList<>();
        for (int i = 0; i < texturesJPG.length; ++i) {

            TextureManager.getInstance().addTexture(texturesJPG[i] + ".jpg",
                    new Texture("assets/textures/" + texturesJPG[i] + ".jpg"));
        }

        for (int i = 0; i < texturesPNG.length; ++i) {
            TextureManager.getInstance().addTexture(texturesPNG[i] + ".png",
                    new Texture("assets/textures/" + texturesPNG[i] + ".png"));
        }

        Object3D[] map = Loader.load3DS("assets/" + mapName + ".3ds", 1f);

        for (Object3D object3d : map) {
            object3d.setCenter(SimpleVector.ORIGIN);
            object3d.rotateX((float) -Math.PI / 2);
            object3d.rotateMesh();
            object3d.setRotationMatrix(new Matrix());
            if (object3d.getName().contains("prp")) {
                propArrayList.add(object3d);
            }
            if (object3d.getName().contains("player")) {
                player = object3d;
                player.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
                world.addObject(player);
                Camera cam = world.getCamera();
                cam.moveCamera(Camera.CAMERA_MOVEOUT, 100);
                cam.lookAt(player.getTransformedCenter());
                cam.setFovAngle((float) (Math.PI * 120 / 180));
                player.build();
            } else if (object3d.getName().contains("light")) {
                Light light = new Light(world);
                light.setIntensity(140, 120, 120);
                light.setAttenuation(-1);
                light.setPosition(object3d.getTransformedCenter());
            } else {
                object3d.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
                world.addObject(object3d);
            }
            object3d.build();
        }

        world.setAmbientLight(20, 20, 20);
        world.buildAllObjects();

        props = propArrayList.toArray(new Object3D[propArrayList.size()]);
    }

    public static void moveCamera(World world) {
        KeyState ks = null;
        while ((ks = keyMapper.poll()) != KeyState.NONE) {
            if (ks.getKeyCode() == KeyEvent.VK_W) {
                up = ks.getState();
            }
            if (ks.getKeyCode() == KeyEvent.VK_S) {
                down = ks.getState();
            }
            if (ks.getKeyCode() == KeyEvent.VK_A) {
                left = ks.getState();
            }
            if (ks.getKeyCode() == KeyEvent.VK_D) {
                right = ks.getState();
            }
        }
        int SPEED = 2;
        SimpleVector moveRes = new SimpleVector(0, 0, 0);
        // move the cube
        if (up) {
            SimpleVector t = world.getCamera().getZAxis();
            t.scalarMul(SPEED);
            moveRes.add(t);
        }

        if (down) {
            SimpleVector t = world.getCamera().getZAxis();
            t.scalarMul(-SPEED);
            moveRes.add(t);
        }

        if (left) {
            world.getCamera().rotateY((float) Math.toRadians(-1));
        }

        if (right) {
            world.getCamera().rotateY((float) Math.toRadians(1));
        }
        world.getCamera().moveCamera(moveRes, SPEED);
    }

    public static SimpleVector getMouseWorldPosition(FrameBuffer buffer, World world, int mouseX, int mouseY) {
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

    static class Ticker {

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

    public static void hideMouseCursor(JFrame frame) {
        // Create a blank cursor
        Cursor blankCursor = frame.getToolkit().createCustomCursor(
                new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB),
                new Point(),
                "Blank Cursor");

        // Set the blank cursor to the frame's content pane
        frame.getContentPane().setCursor(blankCursor);
    }

}
