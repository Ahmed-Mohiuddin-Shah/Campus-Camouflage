import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory.PaletteCloseIcon;

import com.google.gson.Gson;

public class Server implements Runnable, ItemListener {
    Gson gson;

    GameState serverGameState;

    String gameStateString;

    boolean isServerFull = false;

    Functions.Ticker ticker;

    private ServerSocket server;

    static JFrame frame;

    private JTextArea textArea;

    static JButton backButton = new JButton("Main Menu");

    JPanel playerStatusEditorPanel;

    ArrayList<JCheckBox> playerCheckBoxes;

    Server() {
        this("6000");
    }

    Server(String port) {
        this("", port);
    }

    Server(String ip, String port) {
        init(ip, port);
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        addTextServerLog(textArea, "Server Started");

        while (Functions.isServerRunning) {
            if (serverGameState.playersInfo.size() > 4) {
                if (!isServerFull) {
                    addTextServerLog(textArea, "Server Full!!!!!");
                    isServerFull = true;
                }
            } else {
                if (isServerFull) {
                    isServerFull = false;
                }
                Socket socket = null;
                try {
                    socket = server.accept();

                } catch (IOException e) {
                }

                new ServerThread(socket).start();
            }
        }
    }

    class ServerThread extends Thread {
        private Socket socket;
        private String recievedString;
        private String clientName;
        private BufferedReader reader;
        private PrintWriter writer;
        private GameState clientGameState;

        public ServerThread(Socket socket) {
            this.socket = socket;
            try {
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
            } catch (Exception e) {
            }
        }

        public void run() {
            try {
                {
                    String[] tempStrings = reader.readLine().split("\u00B1");
                    clientName = tempStrings[0];
                    clientGameState = gson.fromJson(tempStrings[1], GameState.class);
                }
                serverGameState.addNewPlayer(clientName, clientGameState);
                addTextServerLog(textArea, clientName + " just joined!");

                addPlayerCheckbox(clientName);

                writer.println(gson.toJson(serverGameState));
                int count = 0;
                do {

                    recievedString = reader.readLine();
                    if (recievedString.equals("bye") || recievedString.equals(null)) {
                        break;
                    }
                    clientGameState = gson.fromJson(recievedString, GameState.class);
                    serverGameState.updatePlayer(clientName, clientGameState);
                    gameStateString = gson.toJson(serverGameState);
                    writer.println(gameStateString);

                    if (count < 30) {
                        addTextServerLog(textArea,
                                serverGameState.playersInfo.get(clientName).get(3) + ", " + serverGameState.playersInfo
                                        .get(clientName).get(4));
                        count++;
                        count = 0;
                    }
                } while (!recievedString.equals("bye"));
                addTextServerLog(textArea,
                        clientName + (recievedString.equals("bye") ? " just left!" : " disconnected!"));
                socket.close();
                removeCheckbox(clientName);
                serverGameState.removePlayer(clientName);
            } catch (IOException | ArrayIndexOutOfBoundsException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public static void addTextServerLog(JTextArea textArea, String string) {
        textArea.append("\n" + string);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    private void init(String ip, String port) {
        playerCheckBoxes = new ArrayList<>();

        gson = new Gson();

        serverGameState = new GameState();

        gameStateString = gson.toJson(serverGameState);

        System.out.println(gameStateString);

        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName("");
        } catch (UnknownHostException e) {
        }
        if (!ip.equals("")) {
            try {
                ipAddress = InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                server = new ServerSocket(Integer.parseInt(port), 0, ipAddress);
            } catch (NumberFormatException | IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                server = new ServerSocket(Integer.parseInt(port));
            } catch (NumberFormatException | IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Server is listening on port " + port);

        Functions.serverIP = ipAddress.getHostAddress();

        Functions.serverPort = port;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        Font helloHeadline = new Font("", Font.PLAIN, 0);
        try {
            helloHeadline = Font.createFont(Font.TRUETYPE_FONT, new File("resources/HelloHeadline.ttf"))
                    .deriveFont(36f);
        } catch (Exception e) {
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(helloHeadline);

        Functions.isServerRunning = true;

        frame = new JFrame("Server Running");

        frame.setUndecorated(true);
        frame.setResizable(false);

        backButton.addActionListener(e -> {
            new Game();
        });

        backButton.setFont(helloHeadline);

        JButton closeServer = new JButton("Close Server");
        closeServer.addActionListener(e -> {
            if (e.getActionCommand().equals("Close Server")) {
                closeServer.setText("Are you Sure?");
                int delay = 0750; // 3 seconds
                Timer timer = new Timer(delay, ae -> {
                    closeServer.setText("Close Server");
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                frame.dispose();
                try {
                    server.close();
                } catch (IOException e1) {
                }
                Functions.isServerRunning = false;
                new Game();

            }
        });

        closeServer.setFont(helloHeadline);

        JButton startButton = new JButton("Start/Reset");
        startButton.setFont(helloHeadline);
        // TODO

        textArea = new JTextArea(30, 100);
        textArea.setText("      ");
        textArea.setAutoscrolls(true);
        textArea.setFont(helloHeadline);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setAutoscrolls(true);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        buttonPanel.add(startButton);
        buttonPanel.add(backButton);
        buttonPanel.add(closeServer);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 2));
        bottomPanel.add(buttonPanel);

        playerStatusEditorPanel = new JPanel();
        FlowLayout fL = new FlowLayout();
        fL.setAlignment(FlowLayout.LEFT);
        playerStatusEditorPanel.setLayout(fL);
        playerStatusEditorPanel.add(new JLabel("Check Players to make Seekers!"));

        bottomPanel.add(playerStatusEditorPanel);

        frame.setLayout(new GridLayout(2, 1));
        frame.add(scrollPane);
        frame.add(bottomPanel);
        frame.pack();
        frame.setVisible(true);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Enter full-screen mode
        device.setFullScreenWindow(frame);
        frame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());
    }

    public void addPlayerCheckbox(String clientName) {
        playerCheckBoxes.add(0, new JCheckBox(clientName));
        playerStatusEditorPanel.add(playerCheckBoxes.get(0));
        playerCheckBoxes.get(0).addItemListener(this);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            for (JCheckBox jCheckBox : playerCheckBoxes) {
                if (e.getSource().equals(jCheckBox)) {
                    serverGameState.updateStatus(jCheckBox.getText(), "seeker");
                }
            }
        } else {
            for (JCheckBox jCheckBox : playerCheckBoxes) {
                if (e.getSource().equals(jCheckBox)) {
                    serverGameState.updateStatus(jCheckBox.getText(), "hider");
                }
            }
        }
    }

    public void removeCheckbox(String clientName) {
        for (JCheckBox jCheckBox : playerCheckBoxes) {
            if (jCheckBox.getText().equals(clientName)) {
                playerStatusEditorPanel.remove(jCheckBox);
            }
        }
    }
}