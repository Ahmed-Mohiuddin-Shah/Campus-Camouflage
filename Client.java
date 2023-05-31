import java.io.*;
import java.net.*;

import com.google.gson.Gson;

public class Client extends Thread {
    private String ip;
    private String port;
    private String name;

    Gson gson;
    GameState gameState;

    private BufferedReader reader;
    private PrintWriter writer;

    private Socket socket;

    Client(String ip, String port, String name) {
        super();

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

        gameState = gson.fromJson(readGameStateFromServer(), GameState.class);
    }

    public void writeGameStateToServer(String gameStateString) {
        writer.print(gameStateString);
        writer.flush();
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
        writer.print("bye");
        writer.flush();
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    public void run() {

        gameState = gson.fromJson(readGameStateFromServer(), GameState.class);

        writeGameStateToServer(gson.toJson(gameState));
    }
}