import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class Server implements  ActionListener, Runnable {
    private ServerSocket server;

    private JFrame frame;

    private JTextArea textArea;

    ServerRunning runServer;

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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                server = new ServerSocket(Integer.parseInt(port), 0, ipAddress);
            } catch (NumberFormatException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                server = new ServerSocket(Integer.parseInt(port));
            } catch (NumberFormatException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        System.out.println("Server is listening on port " + port);

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

        runServer = new ServerRunning();

        runServer.setRunning(true);

        Thread thread = new Thread(this);
        thread.start();

        frame = new JFrame("Server Running");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton closeButton = new JButton("Close Server");
        closeButton.addActionListener(e -> {
            frame.dispose();
            runServer.setRunning(false);
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
        frame.setSize(1280, 720);
        frame.setVisible(true);

    }

    private class ServerRunning {
        private boolean running;

        public void setRunning(boolean a) {
            running = a;
        }

        public boolean getRunning() {
            return running;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    @Override
    public void run() {
        while (runServer.getRunning()) {
            Socket socket = null;
            try {
                socket = server.accept();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println("New client connected");
            textArea.append("\n" + "New client connected");

            OutputStream output = null;
            try {
                output = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            PrintWriter writer = new PrintWriter(output, true);

            writer.println(new Date().toString());
        }
    }

}