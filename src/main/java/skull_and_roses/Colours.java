package skull_and_roses;

public interface Colours {

    public enum Colour {
        BLUE, PINK
    }

    /**
     * Converts a string representation of a colour to its corresponding Colour enum.
     * @param colour the string representation of the colour
     * @return the Colour enum corresponding to the given string
     * @throws Exception if the given string does not match any valid Colour enum
     */
    public static Colour stringToColour(String colour) throws Exception {

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
