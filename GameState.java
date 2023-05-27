import java.util.*;

import com.threed.jpct.*;
import com.threed.jpct.util.*;

public class GameState {
    ArrayList<String> players;

    ArrayList<String> hiders;

    ArrayList<String> seekers;

    GameState() {
        players = new ArrayList<String>(5);
        hiders = new ArrayList<String>(5);
        seekers = new ArrayList<String>(5);
    }
}
