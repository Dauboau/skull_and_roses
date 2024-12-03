package skull_and_roses;

import java.util.ArrayList;

public class Game {

    public enum Type {
        ONE_vs_ZERO, ZERO_vs_ZERO, PLAYER_vs_ONE, PLAYER_vs_ZERO
    }

    public ArrayList<Player> players = new ArrayList<Player>(2);
    public Integer bid = null;

    public Game(Type type) {
        setPlayers(type,"p1","p2","pink","blue");
    }

    public Game(Type type, String p1, String p2, String c1, String c2) {
        setPlayers(type, p1, p2, c1, c2);
    }

    private void setPlayers(Type type, String p1, String p2, String c1, String c2) {

        try{

            switch (type) {

                case ZERO_vs_ZERO:
                    players.add(new Player(p1, c1, Player.Type.ZERO));
                    players.add(new Player(p2, c2, Player.Type.ZERO));
                    break;

                case ONE_vs_ZERO:
                    players.add(new Player(p1, c1, Player.Type.ONE));
                    players.add(new Player(p2, c2, Player.Type.ZERO));
                    break;

                case PLAYER_vs_ONE:
                    players.add(new Player(p1, c1, Player.Type.PLAYER));
                    players.add(new Player(p2, c2, Player.Type.ONE));
                    break;

                case PLAYER_vs_ZERO:
                    players.add(new Player(p1, c1, Player.Type.PLAYER));
                    players.add(new Player(p2, c2, Player.Type.ZERO));
                    break;

                default:
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
