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
        } catch (NumberFormatException | IOException e) {
        }
        this.ip = ip;
        this.port = port;
        this.name = name;

        try (InputStream input = socket.getInputStream()) {
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException e1) {
        }
        try (OutputStream output = socket.getOutputStream()) {
            writer = new PrintWriter(output, true);
        } catch (IOException e) {
        }

        gameState = gson.fromJson(readGameStateFromServer(), GameState.class);
    }

    public void writeGameStateToServer(String gameStateString) {
        writer.print(gameStateString);
        writer.flush();
    }

    public String readGameStateFromServer() {
        String s = null;
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