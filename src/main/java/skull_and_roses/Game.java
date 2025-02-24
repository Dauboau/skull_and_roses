package skull_and_roses;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import skull_and_roses.Actions.Actions1;

public class Game {

    public enum Type {
        ONE_vs_ZERO, ZERO_vs_ZERO, PLAYER_vs_ONE, PLAYER_vs_ZERO
    }

    public static final boolean AUTO_MODE = false;
    public static final int N_ROUNDS = 1000;

    // 2 players setup
    public ArrayList<Player> players = new ArrayList<Player>(2);
    
    public Integer bid = 0;
    public int nTicks = 0;
    public int nRounds = 0;
    public Type type;

    public AtomicBoolean resetFlag = new AtomicBoolean(false);

    public Game(Type type) {
        this.type = type;
        setPlayers(type,"p1","p2","pink","blue");
    }

    public Game(Type type, String p1, String p2, String c1, String c2) {
        this.type = type;
        setPlayers(type, p1, p2, c1, c2);
    }

    public Game(Game oldGame){
        this.type = oldGame.type;
        for (Player player : oldGame.players) {
            players.add(player);
        }
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

    public Runnable start() {
        return () -> {

            resetFlag.set(false);

            System.out.println("Game Started: " + players.get(0).name + " vs " + players.get(1).name);
            App.gameController.setPlayerLabels(players.get(0).name,players.get(1).name,players.get(0).type.toString(),players.get(1).type.toString());

            int firstPlayer = ThreadLocalRandom.current().nextInt(0, 2);

            System.out.println("First Player: " + players.get(firstPlayer).name);
            updateLabels(bid,"0",players.get(firstPlayer),"FIRST_PLAYER");

            this.stage_1(players.get(firstPlayer),players.get(1 - firstPlayer));
        };
    }

    public synchronized void reset() {

        System.out.println("Game Restarting: " + players.get(0).name + " vs " + players.get(1).name);

        for (Player player : players) {
            player.reset();
        }

        bid = 0;
        nTicks = 0;

        resetFlag.set(true);
        tick();

    }

    public synchronized void tick() {
        nTicks++;
        notifyAll();
    }

    private String keyboardCharacter = "";
    public synchronized void tick_keyboard(String character) {
        keyboardCharacter = character;

        if(character.equals(" ")){
            nTicks++;
        }

        notifyAll();
        
    }

    public synchronized String wait_action(String[] actions){

        java.util.function.Predicate<String> isActionInActions = act -> {
            for (String a : actions) {
                if (a.equals(act)) {
                    return true;
                }
            }
            return false;
        };

        keyboardCharacter = null;
        while (!isActionInActions.test(keyboardCharacter)) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return keyboardCharacter;

    }

    private synchronized void wait_tick(){

        int nTicksAux = this.nTicks;
        while (nTicksAux == this.nTicks) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    private void updateLabels(int bid, String stage, Player player, String action){

        switch (type) {
            case ZERO_vs_ZERO:
            case ONE_vs_ZERO:
                App.gameController.updateLabels(bid, stage, player.name, action);
                break;

            case PLAYER_vs_ONE:
            case PLAYER_vs_ZERO:

                if(player.type == Player.Type.ZERO || player.type == Player.Type.ONE){
                    
                    if(
                        action == Actions1.PLACE_FLOWER.toString() 
                        || action == Actions1.PLACE_SKULL.toString()
                    ){
                        action = "PLACE_TOKEN";
                    }

                }

                App.gameController.updateLabels(bid, stage, player.name, action);

                break;

            default:
                break;
        }

    }

    private void updateInstructions(String stage, Player player){

        String instructions = "Instructions: click or press enter";

        switch (type) {
            case ZERO_vs_ZERO:
            case ONE_vs_ZERO:
                break;

            case PLAYER_vs_ONE:
            case PLAYER_vs_ZERO:

                switch (player.type) {
                    case ZERO:
                    case ONE:
                        break;

                    case PLAYER:

                        switch (stage) {
                            case "1":
                                instructions = "Instructions: press S to place a skull, F to place a flower or B to bid";
                                break;
                
                            case "2":
                                instructions = "Instructions: press I to increase the bid or C to challenge";
                                break;

                            case "3":
                                instructions = "Instructions: click or press to turn a token";
                                break;
                        
                            default:
                                break;
                
                        }

                        break;
                
                    default:
                        break;
                }

                break;

            default:
                break;

        }

        App.gameController.updateInstructions(instructions);

    }

    public void stage_1(Player player, Player opponent) {

        updateInstructions("1",player);

        if(!AUTO_MODE && player.type != Player.Type.PLAYER){
            wait_tick();
            if(resetFlag.get()){return;}
        }

        Player.Actions1 action = player.play_1(opponent.tokenStack.size());

        System.out.println("Stage 1: " + player.name + " " + action);
        System.out.println("Player's token stack: " + player.tokenStack);
        System.out.println("Opponent's token stack: " + opponent.tokenStack);
        updateLabels(bid,"1",player,action.toString());

        if(action == Player.Actions1.BID){
            stage_2(opponent,player);
        }else{
            stage_1(opponent,player);
        }

    }

    public void stage_2(Player player, Player opponent){

        updateInstructions("2",player);

        System.out.println("*** ------ ***");

        if(!AUTO_MODE && player.type != Player.Type.PLAYER){
            wait_tick();
            if(resetFlag.get()){return;}
        }

        Player.Actions2 action = player.play_2(opponent.tokenStack.size(),bid);

        System.out.println("Stage 2: " + player.name + "(" + player.type + ") " + action + " " + bid);
        System.out.println("Player's token stack: " + player.tokenStack);
        System.out.println("Opponent's token stack: " + opponent.tokenStack);
        updateLabels(bid,"2",player,action.toString());

        if(action == Player.Actions2.CHALLENGE){
            opponent.updateBelief_0(new State(bid - 1, player.tokenStack.size(), opponent.tokenStack), action);
            player.updateBelief_1(new State(bid, player.tokenStack.size(), opponent.tokenStack), action);
            stage_3(opponent,player);
        }else{
            opponent.updateBelief_0(new State(bid - 2, player.tokenStack.size(), opponent.tokenStack), action);
            player.updateBelief_1(new State(bid-1, player.tokenStack.size(), opponent.tokenStack), action);
            stage_2(opponent,player);
        }

    }

    public void stage_3(Player player, Player opponent){

        updateInstructions("3",player);

        if(!AUTO_MODE){
            wait_tick();
            if(resetFlag.get()){return;}
        }

        Player.Actions3 action = player.play_3(opponent);

        if(action == Player.Actions3.TURN_ROSE){
            // if a rose is turned, reduce the bid accordingly
            bid--;
        }

        System.out.println("Stage 3: " + player.name + " " + action + " " + bid);
        System.out.println("Player's token stack: " + player.tokenStack);
        System.out.println("Opponent's token stack: " + opponent.tokenStack);
        updateLabels(bid,"3",player,action.toString());

        if(action != Player.Actions3.TURN_ROSE){
            game_over(opponent,player);
            return;
        }else if(bid <= 0){
            game_over(player,opponent);
            return;
        }

        stage_3(player,opponent);

    }

    public void game_over(Player winner, Player loser){

        if(!AUTO_MODE){
            wait_tick();
            if(resetFlag.get()){return;}
        }

        System.out.println("Winner: " + winner.name);
        System.out.println("Looser: " + loser.name);
        updateLabels(bid,"4",winner,"WINNER");

        winner.nWins++;
        nRounds++;

        winner.storeBeliefs();
        loser.storeBeliefs();

        if(AUTO_MODE && nRounds < N_ROUNDS){
            App.gameController.reset();
        }

    }

}
