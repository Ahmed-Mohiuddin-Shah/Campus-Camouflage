import java.io.*;
import java.net.*;

import com.google.gson.Gson;

public class Client implements Runnable {
    private String ip;
    private String port;
    private String name;

    Gson gson;
    GameState gameState;

    boolean stopClient;

    private BufferedReader reader;
    private PrintWriter writer;

    private Socket socket;

    Client(String ip, String port, String name) {
        super();

        stopClient = false;
        gson = new Gson();
        gameState = new GameState();

        try {
            socket = new Socket(ip, Integer.parseInt(port));
            this.ip = ip;
            this.port = port;
            this.name = name;
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (NumberFormatException | IOException e) {

        }

        writer.print(name);
    }

    public void writeGameStateToServer(String gameStateString) {
        writer.print(gameStateString);
    }

    public String readGameStateFromServer() {
        String s = gson.toJson(gameState);
        try {
            s = reader.readLine();
        } catch (IOException e) {
        }
        return s;
    }

    public void closeClient() {
        stopClient = true;
        writer.print("bye");
        writer.flush();
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        while (!stopClient) {
            System.out.println("i am in thread");
            String s = readGameStateFromServer();
            System.out.println(s);
            gameState = gson.fromJson(s, GameState.class);
            writeGameStateToServer(gson.toJson(gameState));
            System.out.println("I ran");
        }
    }
}