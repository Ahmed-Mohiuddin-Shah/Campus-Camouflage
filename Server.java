import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class Server implements Runnable {
    private ServerSocket server;

    private JFrame frame;

    private JTextArea textArea;

    Server() {
        this("6000");
    }

    Server(String port) {
        this("", port);
    }

    Server(String ip, String port) {
        InetAddress ipAddress = null;
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

        Functions.serverIP = ip;

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

        Thread thread = new Thread(this);
        thread.start();

        frame = new JFrame("Server Running");
        
        frame.setUndecorated(true);
        frame.setResizable(false);

        JButton closeButton = new JButton("Close Server");
        closeButton.addActionListener(e -> {
            frame.dispose();
            Functions.isServerRunning = false;
            new Game();
        });
        textArea = new JTextArea(30, 100);
        textArea.setFont(helloHeadline);
        textArea.setEditable(false);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        buttonPanel.add(closeButton);
        frame.setLayout(new GridLayout(2, 1));
        frame.getContentPane().add(textArea);
        frame.getContentPane().add(buttonPanel);
        frame.pack();
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Enter full-screen mode
        device.setFullScreenWindow(frame);
        frame.setSize(device.getFullScreenWindow().getWidth(), device.getFullScreenWindow().getHeight());

    }

    

    @Override
    public void run() {
        while (Functions.isServerRunning) {
            Socket socket = null;
            try {
                socket = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("New client connected");
            textArea.append("\n New client connected");

            new ServerThread(socket).start();
        }
    }

    class ServerThread extends Thread {
        private Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                String text;

                do {
                    text = reader.readLine();
                    String reverseText = new StringBuilder(text).reverse().toString();
                    writer.println("Server: " + reverseText);

                } while (!text.equals("bye"));

                socket.close();
            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

}