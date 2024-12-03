package skull_and_roses;

import java.util.Stack;

public class Player {

    public enum Colour {
        BLUE, PINK
    }

    public enum Type {
        ZERO, ONE, PLAYER
    }

    public Stack<Token> tokenStack;
    public String name;
    public Colour colour;
    public Type type;

    public Player(String name, String colour, Type type) throws Exception {
        this.name = name;
        this.colour = stringToColour(colour);
        this.type = type;
    }

    /**
     * Converts a string representation of a colour to its corresponding Colour enum.
     * @param colour the string representation of the colour
     * @return the Colour enum corresponding to the given string
     * @throws Exception if the given string does not match any valid Colour enum
     */
    private static Colour stringToColour(String colour) throws Exception {

        switch (colour.toUpperCase()) {
            case "BLUE":
                return Colour.BLUE;
            case "PINK":
                return Colour.PINK;
            default:
                throw new Exception("Invalid Colour");
        }

    }

}
