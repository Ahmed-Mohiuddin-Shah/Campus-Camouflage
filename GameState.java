import java.util.*;

import com.threed.jpct.*;
import com.threed.jpct.util.*;

public class GameState {
    ArrayList<ArrayList<String>> playersInfo;

    GameState() {
        playersInfo = new ArrayList<ArrayList<String>>(5);
    }

    public void addNewPlayer(String name, String position, String status, String hitWhat, String currentModel) {
        ArrayList<String> playerInfo = new ArrayList<String>(5);
        playerInfo.add(name);
        playerInfo.add(position);
        playerInfo.add(status);
        playerInfo.add(hitWhat);
        playerInfo.add(currentModel);
        playersInfo.add(playerInfo);
    }

    public void removePlayer(String name) {
        for (int i = 0; i < playersInfo.size(); i++) {
            if (playersInfo.get(i).get(0).equals(name)) {
                playersInfo.remove(i);
            }
        }
    }

    public void updatePosition(String name, String position) {
        for (int i = 0; i < playersInfo.size(); i++) {
            if (playersInfo.get(i).get(0).equals(name)) {
                playersInfo.get(i).remove(1);
                playersInfo.get(i).add(1, position);
            }
        }
    }

    public void updateStatus(String name, String status) {
        for (int i = 0; i < playersInfo.size(); i++) {
            if (playersInfo.get(i).get(0).equals(name)) {
                playersInfo.get(i).remove(2);
                playersInfo.get(i).add(2, status);
            }
        }
    }
    
    public void updateHitWhat(String name, String hitWhat) {
        for (int i = 0; i < playersInfo.size(); i++) {
            if (playersInfo.get(i).get(0).equals(name)) {
                playersInfo.get(i).remove(3);
                playersInfo.get(i).add(3, hitWhat);
            }
        }
    }

    public void updateCurrentModel(String name, String currentModel) {
        for (int i = 0; i < playersInfo.size(); i++) {
            if (playersInfo.get(i).get(0).equals(name)) {
                playersInfo.get(i).remove(4);
                playersInfo.get(i).add(4, currentModel);
            }
        }
    }

}
