package skull_and_roses;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@SuppressWarnings("unchecked")

public class Player implements Actions, Colours{

    public enum Type {
        ZERO, ONE, PLAYER
    }

    public static final int N_ROSES = 3;
    public static final int N_SKULLS = 1;
    public static final float LEARNING_SPEED = 0.6f;

    public Stack<Token> tokenStack = new Stack<Token>();
    private Stack<ImageView> tokenImageViewStack = new Stack<ImageView>();
    public int nRoses;
    public int nSkulls;
    public String name;
    public Colour colour;
    public Type type;

    /**
     * Beliefs_0 about future behavior of the opponent based on the state.
     * @key The state of the player
     * @value The probability that the opponent will increase the bid (INCREASE_BID)
     */
    public HashMap<State,Float> beliefs_0 = new HashMap<State,Float>();

    /**
     * Update the beliefs_0 of the player about the opponent's future behavior
     * after the opponent takes action a.
     * @param s
     * @param a
     */
    public void updateBelief(State s, Actions2 a){

        if(!beliefs_0.containsKey(s)){
            return;
        }

        System.out.println(this.name + " belief: " + s + " " + beliefs_0.get(s));

        if(a == Actions2.INCREASE_BID){
            beliefs_0.put(s, (1 - LEARNING_SPEED) * beliefs_0.get(s) + LEARNING_SPEED);
        }else{
            beliefs_0.put(s, (1 - LEARNING_SPEED) * beliefs_0.get(s));
        }

        System.out.println(this.name + " belief updated: " + s + " " + beliefs_0.get(s));

    }

    public Player(String name, String colour, Type type) throws Exception {
        this.name = name;
        this.colour = Colours.stringToColour(colour);
        this.type = type;
        this.reset();
    }

    public void reset(){
        this.nRoses = N_ROSES;
        this.nSkulls = N_SKULLS;
        this.tokenStack.clear();
    }

    private void pushToken(Token token){

        this.tokenStack.push(token);

        String url = "/skull_and_roses/" + this.colour + "_";
        switch (App.game.type) {

            case ZERO_vs_ZERO:
            case ONE_vs_ZERO:

                switch (token) {
                    case ROSE:
                        url += "F";
                        break;
                    case SKULL:
                        url += "S";
                        break;
                }
                break;

            case PLAYER_vs_ZERO:
            case PLAYER_vs_ONE:

                if(this.type == Type.PLAYER){
                    switch (token) {
                        case ROSE:
                            url += "F";
                            break;
                        case SKULL:
                            url += "S";
                            break;
                    }
                }else{
                    url += "N";
                }
                break;

        }
        url += ".png";

        Image tokenImage = new Image(getClass().getResource(url).toExternalForm());
        ImageView tokenImageView = new ImageView(tokenImage);

        tokenImageView.setPreserveRatio(true);
        tokenImageView.setFitWidth(200);
        tokenImageView.setTranslateX(-100);

        this.tokenImageViewStack.push(tokenImageView);
        App.gameController.addToken(tokenImageView, App.game.players.indexOf(this), tokenImageViewStack.size());

    }

    private Token popToken(){

        Token token = this.tokenStack.pop();

        ImageView tokenImageView = this.tokenImageViewStack.pop();
        App.gameController.removeToken(tokenImageView, App.game.players.indexOf(this));

        return token;

    }

    private Actions1 place_skull(){
        this.pushToken(Token.SKULL);
        this.nSkulls--;
        return Actions1.PLACE_SKULL;
    }

    private Actions1 place_flower(){
        this.pushToken(Token.ROSE);
        this.nRoses--;
        return Actions1.PLACE_FLOWER;
    }

    private Actions1 bid(){
        App.game.bid = 1;
        return Actions1.BID;
    }

    public Actions1 play_1(int opponentTokenStackSize) {

        if(this.tokenStack.size() >= 1 && opponentTokenStackSize >= 1){

            // If there are no tokens left, the player must bid
            if(this.nRoses == 0 && this.nSkulls == 0){

                return bid();

            }else if(this.nRoses == 0){
                
                // If there are no roses left, the player should bid or play a skull token (randonly)
                int randon = ThreadLocalRandom.current().nextInt(0, 2);
                if(randon == 0){
                    return place_skull();
                }else{
                    return bid();
                }

            }else if(this.nSkulls == 0){
                
                // If there are no skulls left, the player should bid or play a flower token (randonly)
                // It may bid unless the last token is a skull
                int randon = ThreadLocalRandom.current().nextInt(0, 3);
                if(randon <= 1 || this.tokenStack.peek() == Token.SKULL){
                    return place_flower();
                }else{
                    return bid();
                }

            }else{

                // Randomly choose an action with equal probability
                int randon = ThreadLocalRandom.current().nextInt(0, 4);
                if(randon == 0){
                    return place_skull();
                }else if(randon == 1 || randon == 2){
                    return place_flower();
                }else{
                    return bid();
                }

            }

        }else{
            
            // If the player has not yet played a token, it must do so (randonly)
            int randon = ThreadLocalRandom.current().nextInt(0, 2);
            if(randon == 0){
                return place_skull();
            }else{
                return place_flower();
            }

        }

    }

    private Actions2 increase_bid(){
        App.game.bid++;
        return Actions2.INCREASE_BID;
    }

    private Actions2 challenge(){
        return Actions2.CHALLENGE;
    }

    /**
    * π(a,aj) -> Reward function for the player if it takes action a and the opponent takes action aj
    * @return the odds of winning based on the player's state
    */
    private float getReward2(Actions2 a, Actions2 aj, int opponentTokenStackSize, int bid){

        if(a == Actions2.INCREASE_BID && aj == Actions2.INCREASE_BID){
            
            // maybe it will win
            return 0.5f;

        }else if(a == Actions2.INCREASE_BID && aj == Actions2.CHALLENGE){

            // calculate the probability of winning if the player is challenged
            float prob = 1;
            int nRoses = 0;
            Stack<Token> stackAux = (Stack<Token>)tokenStack.clone();
            while(!stackAux.isEmpty()){

                if(stackAux.pop() == Token.ROSE){
                    // increments the number of roses
                    nRoses++;
                }else{
                    // not enough roses means winning with probability 0
                    //prob = 0;
                    return -1;
                }

                if(nRoses >= bid + 1){
                    // enough roses means winning with probability 1
                    //prob = 1;
                    return 1;
                }

            }

            // the opponent does not have enough tokens remaining for the player to win
            if(bid + 1 - nRoses >= opponentTokenStackSize){
                //prob = 0
                return -1;
            }

            int roses_aux = N_ROSES;
            int skulls_aux = N_SKULLS;
            int tokens_aux = opponentTokenStackSize;
            while(nRoses < bid + 1 && tokens_aux > 0){

                prob = prob * (roses_aux / (roses_aux + skulls_aux));
                roses_aux--;

                nRoses++;
                tokens_aux--;

            }

            return prob;
            
        }else{

            // calculate the probability of winning if the player challenges
            float prob = 1;
            int nRoses = 0;

            int roses_aux = N_ROSES;
            int skulls_aux = N_SKULLS;
            int tokens_aux = opponentTokenStackSize;
            while(nRoses < bid + 1 && tokens_aux > 0){

                prob = prob * (roses_aux / (roses_aux + skulls_aux));
                roses_aux--;

                nRoses++;
                tokens_aux--;

            }

            Stack<Token> stackAux = (Stack<Token>)tokenStack.clone();
            while(!stackAux.isEmpty()){

                if(stackAux.pop() == Token.ROSE){
                    // increments the number of roses belonging to the player the opponent can use to win
                    nRoses++;
                }else{
                    break;
                }

            }

            if(nRoses < bid){
                // the game does not have enough roses for the opponent to win
                // the player will win if it challenges the opponent
                return Float.MAX_VALUE;
            }else{
                return prob;
            }

        }

    }

    public Actions2 play_2(int opponentTokenStackSize, int bid){

        switch (type) {

            // If the player uses zero-order theory of mind
            // it forms beliefs_0 about future behavior 
            // based on their state (number of tokens in the opponent's pile, its own stack and the bid)
            case ZERO:

                State state = new State(bid, opponentTokenStackSize, (Stack<Token>) this.tokenStack.clone());

                // Initial belief is randon for both possible actions (INCREASE_BID or CHALLENGE)
                beliefs_0.putIfAbsent(state, (float) Math.random());

                float value_INCREASE_BID = 
                    getReward2(Actions2.INCREASE_BID,Actions2.INCREASE_BID,opponentTokenStackSize,bid) * (beliefs_0.get(state))
                    + getReward2(Actions2.INCREASE_BID,Actions2.CHALLENGE,opponentTokenStackSize,bid) * (1 - beliefs_0.get(state));

                float value_CHALLENGE = 
                    getReward2(Actions2.CHALLENGE,null,opponentTokenStackSize,bid);

                if(value_INCREASE_BID >= value_CHALLENGE){
                    return increase_bid();
                }else{
                    return challenge();
                }
        
            default:
                break;

        }

        return Actions2.CHALLENGE;

    }

    public Actions3 play_3(Player opponent){

        if(!tokenStack.isEmpty()){
            if(this.popToken() == Token.ROSE){
                return Actions3.TURN_ROSE;
            }else{
                return Actions3.TURN_SKULL;
            }
        }

        if(!opponent.tokenStack.isEmpty()){
            if(opponent.popToken() == Token.ROSE){
                return Actions3.TURN_ROSE;
            }else{
                return Actions3.TURN_SKULL;
            }
        }

        return Actions3.TURN_NOTHING;

    }

}
