package skull_and_roses;

import java.util.ArrayList;

public class Game {

    public ArrayList<Player> players = new ArrayList<Player>(2);
    public Integer bid = null;

    public Game() {
        players.add(new Player("p1", Player.Colour.BLUE));
        players.add(new Player("p2", Player.Colour.PINK));
    }

}
