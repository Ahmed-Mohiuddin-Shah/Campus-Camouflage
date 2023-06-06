import java.awt.*;

import javax.swing.*;

import com.threed.jpct.*;
import com.threed.jpct.util.*;

import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class GameClient implements KeyListener, MouseListener, MouseMotionListener, CollisionListener {
    String name;

    GLFont glFont;

    Client client;

    int deltaX;
    int deltaY;

    int screenCenterX;
    int screenCenterY;

    Robot robot;

    float toRadians = (float) Math.PI / 180;

    private static final float DAMPING = 0.1f;

    private static final float SPEED = 3f;

    private static final float MAXSPEED = 4f;

    private SimpleVector moveRes = new SimpleVector(0, 0, 0);

    private SimpleVector ellipsoid = new SimpleVector(5, 15, 5);
    float playerHeight;

    private JFrame pauseFrame, gameFrame;
    JPanel panel1, panel2, panel3, panel4;
    JTextField messageField;
    JTextArea serverLog, messageArea;

    Object3D player = null;
    Object3D mouseTarget = Primitives.getSphere(3f);
    String mouseWasOn = "non";
    Object3D[] map;
    Object3D[] props;
    World world;
    FrameBuffer buffer;
    GraphicsDevice device;
    Canvas canvas;
    boolean gameLoop;
    Thread gameThread, clientThread;

    boolean up = false;
    boolean down = false;
    boolean left = false;
    boolean right = false;

    KeyMapper keyMapper;

    int mouseX = 0;
    int mouseY = 0;

    String ip;
    String port;

    ArrayList<Object3D> serverPlayersModels;
    ArrayList<String> serverPlayerPrevModels;

    public GameClient(String ip, String port, String name) {
        Config.collideOffset = 500f;

        world = new World();

        serverPlayersModels = new ArrayList<>();

        this.name = name;
        this.ip = ip;
        this.port = port;

        deltaX = 0;
        deltaY = 0;

        Font helloHeadline = new Font("", Font.PLAIN, 0);
        try {
            helloHeadline = Font.createFont(Font.TRUETYPE_FONT, new File("resources/HelloHeadline.ttf"))
                    .deriveFont(36f);
        } catch (Exception e) {
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(helloHeadline);

        glFont = new GLFont(helloHeadline);

        pauseFrame = new JFrame("Campus Camouflage Paused");
        pauseFrame.setLayout(new GridLayout(1, 2));
        pauseFrame.addKeyListener(this);

        pauseFrame.setUndecorated(true);
        pauseFrame.setResizable(false);

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        pauseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pauseFrame.setVisible(true);

        // // Enter full-screen mode
        device.setFullScreenWindow(pauseFrame);

        pauseFrame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());

        gameFrame = new JFrame("Campus Camouflage");

        gameFrame.setUndecorated(true);
        gameFrame.setResizable(false);

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);
        gameFrame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());

        screenCenterX = device.getFullScreenWindow().getWidth() / 2;
        screenCenterY = device.getFullScreenWindow().getHeight() / 2;

        try {
            robot = new Robot(device);
        } catch (AWTException e) {
        }

        int maxWidth = 800;
        int maxHeight = 600;
        for (VideoMode vMode : FrameBuffer.getVideoModes(IRenderer.RENDERER_OPENGL)) {

            if (maxWidth < vMode.width) {
                maxWidth = vMode.width;
                maxHeight = vMode.height;
                System.out.println("" + vMode.width + ", " + vMode.height);
            }

        }

        buffer = new FrameBuffer(maxWidth, maxHeight, FrameBuffer.SAMPLINGMODE_HARDWARE_ONLY);
        buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
        canvas = buffer.enableGLCanvasRenderer();

        gameFrame.add(canvas, BorderLayout.CENTER);
        canvas.requestFocus();
        canvas.addMouseListener(this);
        canvas.addKeyListener(this);
        canvas.addMouseMotionListener(this);
        keyMapper = new KeyMapper(canvas);
        gameLoop = true;

        // TODO improve pauseFrame
        init();

        serverLog = new JTextArea();
        serverLog.setText("      ");
        serverLog.setAutoscrolls(true);
        serverLog.setFont(helloHeadline);
        serverLog.setEditable(false);

        panel1 = new JPanel(new GridLayout(2, 1));

        panel4 = new JPanel(new GridLayout(2, 1));
        JButton resumeButton = new JButton("Resume");
        resumeButton.setFont(helloHeadline);
        resumeButton.addActionListener(e -> {
            device.setFullScreenWindow(gameFrame);
            gameFrame.setSize(device.getFullScreenWindow().getWidth(),
                    device.getFullScreenWindow().getHeight());
        });
        JButton leaveServer = new JButton("Leave");
        leaveServer.setFont(helloHeadline);
        leaveServer.addActionListener(e -> {
            if (e.getActionCommand().equals("Leave")) {
                leaveServer.setText("Are you Sure?");
                int delay = 0750; // 3 seconds
                Timer timer = new Timer(delay, ae -> {
                    leaveServer.setText("Leave");
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                gameLoop = false;
                try {
                    client.closeClient();
                } catch (Exception e1) {
                }
                gameFrame.dispose();
                pauseFrame.dispose();
                new Game();
            }
        });

        panel1.add(resumeButton);
        panel1.add(leaveServer);

        pauseFrame.add(serverLog);
        pauseFrame.add(panel1);

        // Collision Listeners
        mouseTarget.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
        mouseTarget.addCollisionListener(this);
        mouseTarget.enableCollisionListeners();

        gameThread = new Thread(new GameLoop());
        gameThread.start();
    }

    private void init() {
        mouseTarget.build();
        world.addObject(mouseTarget);
        loadMap(Functions.mapName);
    }

    public void updateServerPlayerModel(Object3D model) {
        client.gameState.updateCurrentModel(name, mouseWasOn);
        world.removeObject(player);
        player.clearTranslation();
        player = model.cloneObject();
        playerHeight = player.getMesh().getBoundingBox()[3] -
                player.getMesh().getBoundingBox()[2];
        player.translate(0, -playerHeight, 0);
        player.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
        player.setCollisionOptimization(true);
        player.build();
        world.addObject(player);
        world.buildAllObjects();
        reEvaluateEllipsoid();
    }

    private void gameLoop() {
        Object3D cop = null;
        for (Object3D object3d : map) {
            if (object3d.getName().contains("cop")) {
                cop = object3d;
            }
        }
        client = new Client(ip, port, name);
        client.gameState.addNewPlayer(name, player.getTransformedCenter(), "hider", "non",
                player.getName(), "100");
        clientThread = new Thread(client);
        clientThread.start();

        world.getCamera().setEllipsoidMode(Camera.ELLIPSOID_TRANSFORMED);

        String tempString = "nil";
        Object3D cube = Primitives.getPyramide(20f);

        while (!client.serverReady.equals("end")) {

            while (client.serverReady.equals("no")) {
                client.sendGameState();
            }

            client.gameState.removePlayer(name);
            client.gameState.addNewPlayer(name, player.getTransformedCenter(), "hider", "non",
                    player.getName(), "100");

            client.sendGameState();
            client.sendGameState();

            // Initialize code
            world = new World();

            serverPlayersModels = new ArrayList<>(client.gameState.getNumOfPlayersOnServer());
            serverPlayerPrevModels = new ArrayList<>(client.gameState.getNumOfPlayersOnServer());

            for (String keyID : client.gameState.playersInfo.keySet()) {
                ArrayList<String> playerServerInfo = client.gameState.playersInfo.get(keyID);
                Object3D assignModel = cop.cloneObject();
                assignModel.setName(assignModel.getName().split("_jPCT")[0] + "\u00B1" + playerServerInfo.get(0));

                serverPlayerPrevModels.add(assignModel.getName());

                assignModel.clearTranslation();
                assignModel.translate(Functions.stringToSimpleVector(playerServerInfo.get(1)));
                assignModel.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
                assignModel.setCollisionOptimization(true);
                assignModel.build();
                world.addObject(assignModel);
                serverPlayersModels.add(assignModel);
                playerServerInfo.set(4, assignModel.getName());
            }

            init();

            while (client.serverReady.equals("yes")) {

                for (String keyID : client.gameState.playersInfo.keySet()) {
                    if (keyID.equals(name)) {
                        continue;
                    }

                    ArrayList<String> playerServerInfo = client.gameState.playersInfo.get(keyID);

                        for (Object3D object3d : serverPlayersModels) {
                            if (object3d.getName().contains(keyID)) {
                                object3d.clearTranslation();
                                object3d.translate(Functions.stringToSimpleVector(playerServerInfo.get(1)));
                            }
                        }
                    
                    
                    }

                mouseTarget.clearTranslation();
                mouseTarget.translate(Functions.getMouseWorldPosition(buffer, world, mouseX, mouseY));
                mouseTarget.translate(1, 1, -1);
                mouseTarget.checkForCollision(world.getCamera().getDirection(), 20f);
                mouseTarget.checkForCollision(world.getCamera().getDirection(), -20f);
                moveCamera();

                client.gameState.updatePosition(name, player.getTranslation());

                world.getCamera().align(player);
                world.getCamera().setPosition(player.getTransformedCenter());
                // world.getCamera().moveCamera(Camera.CAMERA_MOVEOUT, -10f);

                // playerHeight = player.getMesh().getBoundingBox()[3] -
                // player.getMesh().getBoundingBox()[2];

                // world.getCamera().moveCamera(Camera.CAMERA_MOVEUP, playerHeight / 4);
                world.getCamera().moveCamera(Camera.CAMERA_MOVEOUT, 100f);
                world.getCamera().moveCamera(Camera.CAMERA_MOVEUP, 20f);

                buffer.clear(java.awt.Color.ORANGE);
                world.renderScene(buffer);
                world.draw(buffer);
                buffer.update();
                try {
                    {
                        glFont.blitString(buffer,
                                "Status: " + client.serverGameState.playersInfo.get(name).get(2) + "\n" + "Health: "
                                        + client.serverGameState.playersInfo.get(name).get(5) + "\n" + tempString,
                                10,
                                30, 150, Color.BLACK);
                    }

                } catch (Exception e) {
                }
                buffer.display(canvas.getGraphics());
                canvas.repaint();
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {

                }

                client.sendGameState();
            }
        }
        client.closeClient();
    }

    public class Init implements Runnable {

        @Override
        public void run() {
            init();
        }
    }

    public class GameLoop implements Runnable {

        @Override
        public void run() {
            gameLoop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'p':
                if (!device.getFullScreenWindow().toString().contains("Paused")) {
                    device.setFullScreenWindow(null);
                    device.setFullScreenWindow(pauseFrame);

                    pauseFrame.setSize(device.getFullScreenWindow().getWidth(),
                            device.getFullScreenWindow().getHeight());
                } else {
                    device.setFullScreenWindow(gameFrame);
                    gameFrame.setSize(device.getFullScreenWindow().getWidth(),
                            device.getFullScreenWindow().getHeight());
                }
                break;
            default:
                break;
        }
    }

    public void loadMap(String mapName) {
        ArrayList<Object3D> propArrayList = new ArrayList<>();
        for (int i = 0; i < Functions.texturesJPG.length; ++i) {

            TextureManager.getInstance().addTexture(Functions.texturesJPG[i] + ".jpg",
                    new Texture("assets/textures/" + Functions.texturesJPG[i] + ".jpg"));
        }

        for (int i = 0; i < Functions.texturesPNG.length; ++i) {
            TextureManager.getInstance().addTexture(Functions.texturesPNG[i] + ".png",
                    new Texture("assets/textures/" + Functions.texturesPNG[i] + ".png"));
        }

        map = Loader.load3DS("assets/map/" + mapName + ".3ds", 1f);

        for (Object3D object3d : map) {
            object3d.setCenter(SimpleVector.ORIGIN);
            object3d.rotateX((float) -Math.PI / 2);
            object3d.rotateMesh();
            object3d.setRotationMatrix(new Matrix());
            object3d.addCollisionListener(this);
            object3d.enableCollisionListeners();
            object3d.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
            if (object3d.getName().contains("prp")) {
                propArrayList.add(object3d);
            }
            if (object3d.getName().contains("cop")) {
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

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    int X;
    int Y;

    @Override
    public void mouseMoved(MouseEvent e) {
        X = e.getX();
        Y = e.getY();
        deltaX = ((mouseX / 2) - X) * 2;
        deltaY = ((mouseY / 2) - Y) * 2;
        mouseX = (int) (X * 2);
        mouseY = (int) (Y * 2);
        robot.mouseMove(screenCenterX, Y);
    }

    public void moveCamera() {
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

            if (ks.getKeyCode() == KeyEvent.VK_ESCAPE) {
                gameLoop = false;
            }
        }

        // move the cube
        if (up) {
            SimpleVector t = player.getZAxis();
            t.scalarMul(SPEED);
            moveRes.add(t);
        }

        if (down) {
            SimpleVector t = player.getZAxis();
            t.scalarMul(-SPEED);
            moveRes.add(t);
        }

        if (left) {
            SimpleVector t = player.getXAxis();
            t.scalarMul(-SPEED);
            moveRes.add(t);
        }

        if (right) {
            SimpleVector t = player.getXAxis();
            t.scalarMul(SPEED);
            moveRes.add(t);
        }

        player.rotateY(deltaX * toRadians * 0.75f);
        deltaX = 0;
        // deltaY = 0;

        // avoid high speeds
        if (moveRes.length() > MAXSPEED) {
            moveRes.makeEqualLength(new SimpleVector(0, 0, MAXSPEED));
        }

        player.translate(0, -0.02f, 0);

        moveRes = player.checkForCollisionEllipsoid(moveRes, ellipsoid, 8);
        player.translate(moveRes);

        // finally apply the gravity:
        SimpleVector t = new SimpleVector(0, 3, 0);
        t = player.checkForCollisionEllipsoid(t, ellipsoid, 1);
        player.translate(t);

        // damping
        if (moveRes.length() > DAMPING) {
            moveRes.makeEqualLength(new SimpleVector(0, 0, DAMPING));
        } else {
            moveRes = new SimpleVector(0, 0, 0);
        }
    }

    @Override
    public void collision(CollisionEvent ce) {
        if (ce.getObject().equals(mouseTarget)) {
            mouseWasOn = ce.getTargets()[0].getName();
            if (mouseWasOn.contains("prp")) {
                mouseTarget.setAdditionalColor(Color.RED);
            } else {
                mouseTarget.setAdditionalColor(Color.BLUE);
            }
        } else {

        }
    }

    @Override
    public boolean requiresPolygonIDs() {
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (client.serverGameState.playersInfo.get(name).get(2).equals("hider")) {
                if (mouseWasOn.contains("prp")) {
                    for (Object3D object3d : props) {
                        if (object3d.getName().contains(mouseWasOn)) {
                            changePlayerModel(object3d);
                            break;
                        }
                    }
                }
            } else {
                client.serverGameState.updateHitWhat(name,
                        mouseWasOn);
                client.serverGameState.updateCurrentHealth(name,
                        Integer.toString(Integer.parseInt(client.gameState.playersInfo.get(name).get(5)) - 1));
                client.gameState.updateCurrentHealth(name,
                        Integer.toString(Integer.parseInt(client.gameState.playersInfo.get(name).get(5)) - 1));

            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void changePlayerModel(Object3D model) {
        client.gameState.updateCurrentModel(name, mouseWasOn);
        world.removeObject(player);
        player.clearTranslation();
        player = model.cloneObject();
        playerHeight = player.getMesh().getBoundingBox()[3] -
                player.getMesh().getBoundingBox()[2];
        player.translate(0, -playerHeight, 0);
        player.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
        player.setCollisionOptimization(true);
        player.build();
        world.addObject(player);
        world.buildAllObjects();
        reEvaluateEllipsoid();
    }

    public void reEvaluateEllipsoid() {
        float[] BoundingBox = player.getMesh().getBoundingBox();
        ellipsoid = new SimpleVector((BoundingBox[1] - BoundingBox[0]) / 2, (BoundingBox[1] - BoundingBox[0]) - 5,
                (BoundingBox[1] - BoundingBox[0]) / 2);
    }
}