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
    GameState serverGameState;
    ConcurrentHashMap<String, Boolean> clientStates;

    boolean stopClient;

    String serverReady;

    private BufferedReader reader;
    private PrintWriter writer;

    private Socket socket;

    Client(String ip, String port, String name) {
        clientStates = new ConcurrentHashMap<>();
        clientStates.put("stopClient", false);
        clientStates.put("shouldSend", true);
        stopClient = false;
        gson = new Gson();
        gameState = new GameState();
        serverGameState = new GameState();

        serverReady = "no";

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
    }

    public void writeGameStateToServer(String gameStateString) {
        writer.println(gameStateString);
    }

    public String readGameStateFromServer() {
        String s = gson.toJson(gameState);
        try {
            String[] sArray;
            sArray = reader.readLine().split("\u00B1");
            serverReady = sArray[0];
            s = sArray[1];
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
        // TODO What if server disconnects?
        writer.println(name + "\u00B1" + gson.toJson(gameState));
        do {
            if (clientStates.get("shouldSend")) {
                serverGameState = gson.fromJson(readGameStateFromServer(), GameState.class);
                gameState.syncWithOtherGameState(name, serverGameState);
                writeGameStateToServer(gson.toJson(gameState));
                clientStates.put("shouldSend", false);
            }
        } while (!clientStates.get("stopClient"));
    }

    public void sendGameState() {
        clientStates.put("shouldSend", true);
    }
}