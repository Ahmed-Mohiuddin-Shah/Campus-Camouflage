import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class GameState {
    ConcurrentHashMap<String, ArrayList<String>> playersInfo;

    GameState() {
        playersInfo = new ConcurrentHashMap<>();
    }

    public void addNewPlayer(String name, String position, String status, String hitWhat, String currentModel) {
        ArrayList<String> playerInfo = new ArrayList<>(5);
        playerInfo.add(name);
        playerInfo.add(position);
        playerInfo.add(status);
        playerInfo.add(hitWhat);
        playerInfo.add(currentModel);
        playersInfo.put(name, playerInfo);
    }

    public void removePlayer(String name) {
        playersInfo.remove(name);
    }

    public void updatePosition(String name, String position) {
        playersInfo.get(name).set(1, position);
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
}