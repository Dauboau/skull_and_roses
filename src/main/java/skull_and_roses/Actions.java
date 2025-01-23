package skull_and_roses;

public interface Actions {

    public enum Actions1 {
        PLACE_SKULL, PLACE_FLOWER, BID
    }

    public final String[] Keyboard_Actions1 = {"S","F","B","s","f","b"};
    public static Actions1 key_to_Actions1(String key){
        switch (key) {
            case "S":
            case "s":
                return Actions1.PLACE_SKULL;
            case "F":
            case "f":
                return Actions1.PLACE_FLOWER;
            case "B":
            case "b":
                return Actions1.BID;
            default:
                return null;
        }
    }

    public enum Actions2 {
        INCREASE_BID, CHALLENGE
    }

    public final String[] Keyboard_Actions2 = {"I","C","i","c"};
    public static Actions2 key_to_Actions2(String key){
        switch (key) {
            case "I":
            case "i":
                return Actions2.INCREASE_BID;
            case "C":
            case "c":
                return Actions2.CHALLENGE;
            default:
                return null;
        }
    }

    public enum Actions3 {
        TURN_ROSE, TURN_SKULL, TURN_NOTHING
    }

}