import com.threed.jpct.*;
import com.threed.jpct.util.*;
import java.awt.*;
import java.awt.event.*;

public class Functions {
    private static KeyMapper keyMapper = new KeyMapper();

    private static boolean up = false;

    private static boolean down = false;

    private static boolean left = false;

    private static boolean right = false;

    private static String[] texturesJPG = {};

    private static String[] texturesPNG = {};

    static boolean isServerRunning = false;

    static String serverIP = "";

    static String serverPort = "";

    public static void loadMenu(World world) {
        Object3D look = null;
        for (int i = 0; i < texturesPNG.length; ++i) {
            TextureManager.getInstance().addTexture(texturesPNG[i] + ".png",
                    new Texture("assets/textures/" + texturesPNG[i] + ".png"));
        }

        Object3D[] menu = Loader.load3DS("assets/menu.3ds", 1f);
        for (Object3D object3d : menu) {
            object3d.setCenter(SimpleVector.ORIGIN);
            object3d.rotateX((float) -Math.PI / 2);
            object3d.rotateMesh();
            object3d.setRotationMatrix(new Matrix());
            if(object3d.getName().contains("Back")) {
                look = object3d;
            }
            else if (object3d.getName().contains("cam")) {
                world.getCamera().setPosition(object3d.getTransformedCenter());
                world.getCamera().setClippingPlanes(0.1f, 1000000000f);
                world.getCamera().lookAt(look.getTransformedCenter());
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

    
}
