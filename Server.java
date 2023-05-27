import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Server implements Runnable {
    Gson gson;

    GameState gs;

    String gameState;

    private ServerSocket server;

    static JFrame frame;

    private JTextArea textArea;

    static JButton backButton = new JButton("Main Menu");

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
            Socket socket = null;
            try {
                socket = server.accept();
            } catch (IOException e) {
            }
            textArea.append("\n New client connected");

            new ServerThread(socket).start();
        }
    }

    class ServerThread extends Thread {
        private Socket socket;
        private boolean readName;

        public ServerThread(Socket socket) {
            this.socket = socket;
            readName = false;
        }

        public void run() {
            try {

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                String recievedString;

                recievedString = reader.readLine();
                gs.players.add(recievedString);

                gameState = gson.toJson(gs);

                System.out.println(gameState);

                do {

                } while (!recievedString.equals("bye"));

                socket.close();
            } catch (IOException ex) {
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

        gson = new Gson();

        gs = new GameState();

        gameState = gson.toJson(gs);

        System.out.println(gameState);

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

        textArea = new JTextArea(30, 100);
        textArea.setText("      ");
        textArea.setAutoscrolls(true);
        textArea.setFont(helloHeadline);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setAutoscrolls(true);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        buttonPanel.add(backButton);
        buttonPanel.add(closeServer);
        frame.setLayout(new GridLayout(2, 1));
        frame.add(scrollPane);
        frame.add(buttonPanel);
        frame.pack();
        frame.setVisible(true);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Enter full-screen mode
        device.setFullScreenWindow(frame);
        frame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());
    }

}