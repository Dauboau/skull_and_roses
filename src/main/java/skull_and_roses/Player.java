package skull_and_roses;

import java.util.Stack;

public class Player {

    public enum Colour {
        BLUE, PINK
    }

    public Stack<Token> tokenStack;
    public String name;
    public Colour colour;

    public Player(String name, Colour colour) {
        this.name = name;
        this.colour = colour;   
    }

}
