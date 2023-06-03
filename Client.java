import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

public class Client implements Runnable {
    private String ip;
    private String port;
    private String name;

    Gson gson;
    GameState gameState;
    ConcurrentHashMap<String, Boolean> clientStates;

    boolean stopClient;

    private BufferedReader reader;
    private PrintWriter writer;

    private Socket socket;

    Client(String ip, String port, String name) {
        clientStates = new ConcurrentHashMap<>();
        clientStates.put("stopClient", true);
        clientStates.put("shouldSend", false);
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

        writer.println(name);
    }

    public void writeGameStateToServer(String gameStateString) {
        writer.println(gameStateString);
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
        clientStates.put("stopClient", false);
        writer.println("bye");
        writer.flush();
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        writer.println(gson.toJson(gameState));
        gameState = gson.fromJson(readGameStateFromServer(), GameState.class);
        while (!clientStates.get("stopClient")) {
            if (clientStates.get("shouldSend")) {
                writeGameStateToServer(gson.toJson(gameState));
                gameState = gson.fromJson(readGameStateFromServer(), GameState.class);
                clientStates.put("shouldSend", false);
            }
        }
    }

    public void sendGameState() {
        clientStates.put("shouldSend", true);
    }
}