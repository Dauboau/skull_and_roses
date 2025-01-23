package skull_and_roses;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
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
    public static final float LEARNING_SPEED = 0.3f;

    public Stack<Token> tokenStack = new Stack<Token>();
    private Stack<ImageView> tokenImageViewStack = new Stack<ImageView>();
    public int nRoses;
    public int nSkulls;
    public String name;
    public Colour colour;
    public Type type;
    public int nWins = 0;

    public String keyboardAction = "";

    /**
     * Beliefs_0 about future behavior of the opponent based on the state.
     * @key The state of the player
     * @value The probability that the opponent will increase the bid (INCREASE_BID)
     */
    public HashMap<State,Float> beliefs_0 = new HashMap<State,Float>();

    /**
     * Beliefs_1 about future behavior of the player based on the state.
     * @key The state of the player
     * @value The probability that the player will increase the bid (INCREASE_BID)
     */
    public HashMap<State,Float> beliefs_1 = new HashMap<State,Float>();

    /**
     * Update the beliefs_0 of the player about the opponent's future behavior
     * after the opponent takes action a.
     * @param s
     * @param a
     */
    public void updateBelief_0(State s, Actions2 a){

        // Initial belief is randon for both possible actions (INCREASE_BID or CHALLENGE)
        beliefs_0.putIfAbsent(s, (float) Math.random());

        //System.out.println(this.name + " belief_0: " + beliefs_0.get(s));

        if(a == Actions2.INCREASE_BID){
            beliefs_0.put(s, (1 - LEARNING_SPEED) * beliefs_0.get(s) + LEARNING_SPEED);
        }else{
            beliefs_0.put(s, (1 - LEARNING_SPEED) * beliefs_0.get(s));
        }

        //System.out.println(this.name + " belief_0 updated: " + beliefs_0.get(s));

    }

    public void updateBelief_1(State s, Actions2 a){

        // Initial belief is randon for both possible actions (INCREASE_BID or CHALLENGE)
        beliefs_1.putIfAbsent(s, (float) Math.random());

        //System.out.println(this.name + " belief_1: " + beliefs_1.get(s));

        if(a == Actions2.INCREASE_BID){
            beliefs_1.put(s, (1 - LEARNING_SPEED) * beliefs_1.get(s) + LEARNING_SPEED);
        }else{
            beliefs_1.put(s, (1 - LEARNING_SPEED) * beliefs_1.get(s));
        }

        //System.out.println(this.name + " belief_1 updated: " + beliefs_1.get(s));

    }

    public Player(String name, String colour, Type type) throws Exception {
        this.name = name;
        this.colour = Colours.stringToColour(colour);
        this.type = type;
        this.reset();
        this.loadBeliefs();
    }

    public void reset(){
        this.nRoses = N_ROSES;
        this.nSkulls = N_SKULLS;
        this.tokenStack.clear();
        for(ImageView token : tokenImageViewStack){
            App.gameController.removeToken(token, App.game.players.indexOf(this));
        }
        this.tokenImageViewStack.clear();
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

        switch(type){

            case ZERO:
            case ONE:

                if(this.tokenStack.size() >= 1 && opponentTokenStackSize >= 1){

                    // If there are no tokens left, the player must bid
                    if(this.nRoses == 0 && this.nSkulls == 0){
        
                        return bid();
        
                    }else if(this.nRoses == 0){
                        
                        // If there are no roses left, the player should bid or play a skull token (randonly)
                        int randon = ThreadLocalRandom.current().nextInt(0, this.nSkulls + 1);
                        if(randon == 0){
                            return bid();
                        }else{
                            return place_skull();
                        }
        
                    }else if(this.nSkulls == 0){
                        
                        // If there are no skulls left, the player should bid or play a flower token (randonly)
                        // It may bid unless the last token is a skull
                        int randon = ThreadLocalRandom.current().nextInt(0, this.nRoses + 1);
                        if(randon == 0 && this.tokenStack.peek() != Token.SKULL){ // rationality
                            return bid();
                        }else{
                            return place_flower();
                        }
        
                    }else{
        
                        // Randomly choose an action with equal probability
                        int randon = ThreadLocalRandom.current().nextInt(0, this.nRoses + this.nSkulls + 1);
                        if(randon == 0){
                            return bid();
                        }else if(randon == 1){
                            return place_skull();
                        }else{
                            return place_flower();
                        }
        
                    }
        
                }else{
                    
                    // If the player has not yet played a token, it must do so (randonly)
                    int randon = ThreadLocalRandom.current().nextInt(0, N_ROSES + N_SKULLS);
                    if(randon == 0){
                        return place_skull();
                    }else{
                        return place_flower();
                    }
        
                }

            case PLAYER:

                Actions1 action;

                while(true){

                    action = Actions.key_to_Actions1(
                        App.game.wait_action(Actions.Keyboard_Actions1)
                    );

                    if(action == Actions1.PLACE_SKULL && this.nSkulls > 0){
                        return place_skull();
                    }

                    if(action == Actions1.PLACE_FLOWER && this.nRoses > 0){
                        return place_flower();
                    }

                    if(action == Actions1.BID){
                        return bid();
                    }

                }

            default:
                break;

        }

        return null;

    }

    private Actions2 increase_bid(){
        App.game.bid++;
        return Actions2.INCREASE_BID;
    }

    private Actions2 challenge(){
        return Actions2.CHALLENGE;
    }

    private float getReward_2(Actions2 a, Actions2 aj, Stack<Token> playerTokenStack, int opponentTokenStackSize, int bid){

        if(a == Actions2.INCREASE_BID && aj == Actions2.INCREASE_BID){
            
            // The probability of winning if the player increases the bid after the opponent increases the bid
            // will be simplified to the probability of winning if the player challenges the opponent
            // when it is the player's turn to play again
            // unless the probability is 0
            float oddsChallenge = getReward_2(Actions2.CHALLENGE, null, playerTokenStack, opponentTokenStackSize, bid + 2);

            if(oddsChallenge > 0){
                return oddsChallenge;
            }
            
            return (float)0.5;

        }else if(a == Actions2.INCREASE_BID && aj == Actions2.CHALLENGE){

            // calculate the probability of winning if the player is challenged after increasing the bid
            float prob = 1;
            int nRoses = 0;
            Stack<Token> stackAux = (Stack<Token>)playerTokenStack.clone();
            while(!stackAux.isEmpty()){

                if(stackAux.pop() == Token.ROSE){
                    // increments the number of roses
                    nRoses++;
                }else{
                    // not enough roses means winning with probability 0
                    //prob = 0;
                    return 0;
                }

                if(nRoses >= bid + 1){
                    // enough roses means winning with probability 1
                    //prob = 1;
                    return 1;
                }

            }

            // the opponent does not have enough tokens remaining for the player to win
            if(bid + 1 - nRoses > opponentTokenStackSize || bid + 1 - nRoses > N_ROSES){
                //prob = 0
                return 0;
            }

            float roses_aux = N_ROSES;
            float skulls_aux = N_SKULLS;
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
            // (i.e: the opponent finds a skull or can not find enough roses)
            float prob = 1;
            int nRoses = 0;

            float roses_aux = N_ROSES;
            float skulls_aux = N_SKULLS;
            int tokens_aux = opponentTokenStackSize;
            while(nRoses < bid && tokens_aux > 0){

                prob = prob * (roses_aux / (roses_aux + skulls_aux));
                roses_aux--;

                nRoses++;
                tokens_aux--;

            }
            // The probability of the opponent finding a skull is the complement of the probability of finding only roses
            prob = 1 - prob;

            Stack<Token> stackAux = (Stack<Token>)playerTokenStack.clone();
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
                //prob = 1;
                return 1;
            }else{
                return prob;
            }

        }

    }

    private float getReward_2(Actions2 a, Actions2 aj, Stack<Token> playerTokenStack, Stack<Token> opponentTokenStack, int bid){

        if(a == Actions2.INCREASE_BID && aj == Actions2.INCREASE_BID){

            float oddsChallenge = getReward_2(Actions2.CHALLENGE, null, playerTokenStack, opponentTokenStack, bid + 2);

            if(oddsChallenge > 0){
                return oddsChallenge;
            }
            
            return (float)0.5;

        }else if(a == Actions2.INCREASE_BID && aj == Actions2.CHALLENGE){

            // *** --- ***
            // Check the player's stack to see if it has enough roses to win
            int nRoses = 0;
            Stack<Token> stackAux = (Stack<Token>)playerTokenStack.clone();
            while(!stackAux.isEmpty()){

                if(stackAux.pop() == Token.ROSE){
                    nRoses++;
                }else{
                    //prob = 0;
                    return 0;
                }

                if(nRoses >= bid + 1){
                    //prob = 1;
                    return 1;
                }

            }
            // *** --- ***

            // *** --- ***
            // Check the opponent's stack to see if it has enough roses to win
            stackAux = (Stack<Token>)opponentTokenStack.clone();
            while(!stackAux.isEmpty()){

                if(stackAux.pop() == Token.ROSE){
                    nRoses++;
                }else{
                    //prob = 0;
                    return 0;
                }

                if(nRoses >= bid + 1){
                    //prob = 1;
                    return 1;
                }

            }
            // *** --- ***

            //prob = 0;
            return 0;
            
        }else{

            // *** --- ***
            // Check the opponent's stack to see if the opponent has enough roses to win
            int nRoses = 0;
            Stack<Token> stackAux = (Stack<Token>)opponentTokenStack.clone();
            while(!stackAux.isEmpty()){

                if(stackAux.pop() == Token.ROSE){
                    nRoses++;
                }else{
                    //prob = 1;
                    return 1;
                }

                if(nRoses >= bid){
                    //prob = 0;
                    return 0;
                }

            }
            // *** --- ***

            // *** --- ***
            // Check the player's stack to see if the opponent has enough roses to win
            stackAux = (Stack<Token>)playerTokenStack.clone();
            while(!stackAux.isEmpty()){

                if(stackAux.pop() == Token.ROSE){
                    nRoses++;
                }else{
                    //prob = 1;
                    return 1;
                }

                if(nRoses >= bid){
                    //prob = 0;
                    return 0;
                }

            }
            // *** --- ***

            //prob = 0;
            return 0;

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
                    getReward_2(Actions2.INCREASE_BID,Actions2.INCREASE_BID,this.tokenStack,opponentTokenStackSize,bid) * (beliefs_0.get(state))
                    + getReward_2(Actions2.INCREASE_BID,Actions2.CHALLENGE,this.tokenStack,opponentTokenStackSize,bid) * (1 - beliefs_0.get(state));

                float value_CHALLENGE = 
                    getReward_2(Actions2.CHALLENGE,null,this.tokenStack,opponentTokenStackSize,bid);

                System.out.println(this.name + " value_INCREASE_BID: " + value_INCREASE_BID);
                System.out.println(this.name + " value_CHALLENGE: " + value_CHALLENGE);

                if(value_INCREASE_BID > value_CHALLENGE){
                    return increase_bid();
                }else{
                    return challenge();
                }

            // If the player uses first-order theory of mind
            // it forms beliefs_1 about future behavior
            // putting itslef in the place of the opponent
            case ONE:

                state = new State(bid, opponentTokenStackSize, (Stack<Token>) this.tokenStack.clone());

                // Initial belief is randon for both possible actions (INCREASE_BID or CHALLENGE)
                beliefs_1.putIfAbsent(state, (float) Math.random());

                // *** --- ***
                // Predict opponent's behavior

                // Interpretative theory of mind
                // Considering the opponent is rational,
                // it would only have increased the bid if it has enough roses to win if challenged
                
                LinkedList<Token> interpretedStackList = new LinkedList<Token>();
                int nRosesLeft = N_ROSES;
                int nSkullsLeft = N_SKULLS;
                int bidLeft = bid;
                for(int i=0; i<opponentTokenStackSize; i++){
                    if(nRosesLeft > 0){
                        if(bidLeft > 0){    
                            interpretedStackList.addFirst(Token.ROSE);
                            nRosesLeft--;
                        }else{
                            int uknownToken = ThreadLocalRandom.current().nextInt(0, nRosesLeft + nSkullsLeft);
                            if(uknownToken < nRosesLeft){
                                interpretedStackList.addFirst(Token.ROSE);
                                nRosesLeft--;
                            }else{
                                interpretedStackList.addFirst(Token.SKULL);
                                nSkullsLeft--;
                            }
                        }
                    }else{
                        interpretedStackList.addFirst(Token.SKULL);
                    }
                    bidLeft--;
                }

                Stack<Token> interpretedStack = new Stack<Token>();
                for(Token token : interpretedStackList){
                    interpretedStack.push(token);
                }

                // Predictive theory of mind
                // The player puts itself in the place of the opponent
                // Obs: bid + 1 is used, because the opponent will only 
                // play if the player increases the bid
                value_INCREASE_BID = 
                    getReward_2(Actions2.INCREASE_BID,Actions2.INCREASE_BID,interpretedStack,this.tokenStack.size(),bid+1) * (beliefs_1.get(state))
                    + getReward_2(Actions2.INCREASE_BID,Actions2.CHALLENGE,interpretedStack,this.tokenStack.size(),bid+1) * (1 - beliefs_1.get(state));
                value_CHALLENGE = 
                    getReward_2(Actions2.CHALLENGE,null,interpretedStack,this.tokenStack.size(),bid+1);

                Actions2 action_opponent;
                if(value_INCREASE_BID > value_CHALLENGE){
                    action_opponent = Actions2.INCREASE_BID;
                }else{
                    action_opponent = Actions2.CHALLENGE;
                }

                System.out.println("Opponent's predicted Stack: " + interpretedStack);
                System.out.println("Opponent will: " + action_opponent);
                // *** --- ***

                // *** --- ***
                // Decide player's behavior (confidence c1 is 1)
                value_INCREASE_BID = 
                    getReward_2(Actions2.INCREASE_BID,action_opponent,this.tokenStack,interpretedStack,bid);
                    
                value_CHALLENGE = 
                    getReward_2(Actions2.CHALLENGE,null,this.tokenStack,interpretedStack,bid);

                System.out.println(this.name + " value_INCREASE_BID: " + value_INCREASE_BID);
                System.out.println(this.name + " value_CHALLENGE: " + value_CHALLENGE);

                if(value_INCREASE_BID > value_CHALLENGE){
                    return increase_bid();
                }else{
                    return challenge();
                }
                // *** --- ***

            case PLAYER:

                Actions2 action = Actions.key_to_Actions2(
                    App.game.wait_action(Actions.Keyboard_Actions2)
                );

                if(action == Actions2.INCREASE_BID){
                    return increase_bid();
                }

                if(action == Actions2.CHALLENGE){
                    return challenge();
                }

                break;

        
            default:
                break;

        }

        return null;

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

    public void storeBeliefs(){
            
        switch (type) {

            case ZERO:
            case ONE:

                try{
                    FileOutputStream fileOut = new FileOutputStream(this.name + "_beliefs_0");
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(beliefs_0);
                    out.close();
                }catch(IOException i){
                    i.printStackTrace();
                }

                try{
                    FileOutputStream fileOut = new FileOutputStream(this.name + "_beliefs_1");
                    ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(beliefs_1);
                    out.close();
                }catch(IOException i){
                    i.printStackTrace();
                }

                break;

            default:
                break;

        }
    }

    public void loadBeliefs() {

        switch (type) {

            case ZERO:
            case ONE:

                try{
                    FileInputStream fileIn = new FileInputStream(this.name + "_beliefs_0");
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    beliefs_0 = (HashMap<State, Float>) in.readObject();
                    in.close();
                }catch(IOException i){
                    i.printStackTrace();
                }catch (ClassNotFoundException c) {
                    c.printStackTrace();
                }

                try{
                    FileInputStream fileIn = new FileInputStream(this.name + "_beliefs_1");
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    beliefs_1 = (HashMap<State, Float>) in.readObject();
                    in.close();
                }catch(IOException i){
                    i.printStackTrace();
                }catch (ClassNotFoundException c) {
                    c.printStackTrace();
                }

                break;

            default:
                break;

        }
    }

}
