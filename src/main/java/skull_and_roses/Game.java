package skull_and_roses;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    public enum Type {
        ONE_vs_ZERO, ZERO_vs_ZERO, PLAYER_vs_ONE, PLAYER_vs_ZERO
    }

    // 2 players setup
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

    public void start() {

        System.out.println("Game Started: " + players.get(0).name + " vs " + players.get(1).name);

        int firstPlayer = ThreadLocalRandom.current().nextInt(0, 2);

        System.out.println("First Player: " + players.get(firstPlayer).name);

        this.stage_1(players.get(firstPlayer),players.get(1 - firstPlayer));

    }

    public void stage_1(Player player, Player opponent) {

        Player.Actions1 action = player.play_1(opponent.tokenStack.size());

        System.out.println("Stage 1: " + player.name + " " + action);
        System.out.println("Player's token stack: " + player.tokenStack);
        System.out.println("Opponent's token stack: " + opponent.tokenStack);

        if(action == Player.Actions1.BID){
            stage_2(opponent,player);
        }else{
            stage_1(opponent,player);
        }

    }

    public void stage_2(Player player, Player opponent){

        Player.Actions2 action = player.play_2(opponent.tokenStack.size(),bid);

        System.out.println("Stage 2: " + player.name + " " + action + " " + bid);
        System.out.println("Player's token stack: " + player.tokenStack);
        System.out.println("Opponent's token stack: " + opponent.tokenStack);

        if(action == Player.Actions2.CHALLENGE){
            stage_3(opponent,player);
        }else{
            stage_2(opponent,player);
        }

    }

    public void stage_3(Player player, Player opponent){

        boolean playerWin = player.play_3(opponent.tokenStack,bid);

        System.out.println("Stage 3: " + player.name + " " + "was challenged(" + bid + ")!");
        System.out.println("Player's token stack: " + player.tokenStack);
        System.out.println("Opponent's token stack: " + opponent.tokenStack);

        if(playerWin){
            System.out.println("Winner: " + player.name);
        }else{
            System.out.println("Winner: " + opponent.name);
        }

    }

    /**
     * First stage: randonly selecting the tokens
     * 
     * Second stage: I need to implement q-learning feature, so the agents beliefs are refined based on the opponents actions.
     * So, based on the number of tokens in the opponent's pile, its own stack and the bid, the agent will decide to bid or not.
     * This is the state 👍🏻
     * The beliefs are whether the opponent is gonna raise or challenge the bid depending on the agent's action.
     * The objective of the agent is to win the game, so it should raise the bid while believing that the opponent won't challenge,
     * because that maximizes the chances of winning.
     * 
     * Third stage: trivial -> removes tokens from its own pile than from the opponents
     */

}
