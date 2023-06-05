import java.util.concurrent.ConcurrentHashMap;

import com.threed.jpct.SimpleVector;

import java.util.ArrayList;

public class GameState {
    ConcurrentHashMap<String, ArrayList<String>> playersInfo;

    GameState() {
        playersInfo = new ConcurrentHashMap<>();
    }

    public void addNewPlayer(String name, SimpleVector playerPosition, String status, String hitWhat,
            String currentModel, String currentHealth) {
        ArrayList<String> playerInfo = new ArrayList<>(5);
        playerInfo.add(name);
        playerInfo.add(Functions.simpleVectorToString(playerPosition));
        playerInfo.add(status);
        playerInfo.add(hitWhat);
        playerInfo.add(currentModel);
        playerInfo.add(currentHealth);
        playersInfo.put(name, playerInfo);
    }

    public void addNewPlayer(String name, GameState gameState) {
        playersInfo.put(name, gameState.playersInfo.get(name));
    }

    public void removePlayer(String name) {
        playersInfo.remove(name);
    }

    public void updatePosition(String name, SimpleVector playerPosition) {
        playersInfo.get(name).set(1, Functions.simpleVectorToString(playerPosition));

    }

    public void updateStatus(String name, String status) {
        playersInfo.get(name).set(2, status);
    }

    public void updateHitWhat(String name, String hitWhat) {
        playersInfo.get(name).set(3, hitWhat);
    }

    public void updateCurrentModel(String name, String currentModel) {
        playersInfo.get(name).set(4, currentModel);
    }

    public void updateCurrentHealth(String name, String currentHealth) {
        playersInfo.get(name).set(6, currentHealth);
    }

    public void updatePlayer(String name, GameState clientGameState) {
        playersInfo.put(name, clientGameState.playersInfo.get(name));
    }

    public void resetPlayer(String name, String status) {
        ArrayList<String> playerInfo = new ArrayList<>(5);
        playerInfo.add(name);
        playerInfo.add(Functions.simpleVectorToString(Functions.homePosition));
        playerInfo.add(status);
        playerInfo.add("non");
        playerInfo.add(status.equals("hider") ? "hider" : "cop");
        playerInfo.add("100");
        playersInfo.put(name, playerInfo);
    }

    public void syncWithOtherGameState(String excludeName, GameState gameState) {
        for (String keyID : gameState.playersInfo.keySet()) {
            if (!keyID.equals(excludeName)) {
                playersInfo.put(keyID, gameState.playersInfo.get(keyID));
            }
        }
    }

    public void syncWithOtherGameStateExcludeOthers(String name, GameState gameState) {
        for (String keyID : gameState.playersInfo.keySet()) {
            if (keyID.equals(name)) {
                playersInfo.put(keyID, gameState.playersInfo.get(keyID));
                break;
            }
        }
    }
}