import java.io.*;
import java.net.*;
import java.util.Date;

import javax.swing.JFrame;

public class Server {
    private ServerSocket server;

    private JFrame frame;

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

    }

    public void loop() {

        while (true) {
            Socket socket = null;
            try {
                socket = server.accept();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println("New client connected");

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