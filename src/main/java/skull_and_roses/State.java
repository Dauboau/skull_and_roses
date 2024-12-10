package skull_and_roses;

import java.util.Objects;
import java.util.Stack;

public class State {

    public int bid;
    public int opponentTokenStackSize;
    public Stack<Token> tokenStack = new Stack<Token>();

    /**
     * Zero-order theory of mind
     * @param bid
     * @param opponentTokenStackSize
     * @param tokenStack
     */
    public State(int bid, int opponentTokenStackSize, Stack<Token> tokenStack) {
        this.bid = bid;
        this.opponentTokenStackSize = opponentTokenStackSize;
        for(Token token : tokenStack){
            this.tokenStack.push(token);
        }
    }

    @Override
    public String toString() {
        return "State [bid=" + bid + ", opponentTokenStackSize=" + opponentTokenStackSize + ", tokenStack=" + tokenStack
                + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return bid == state.bid &&
                opponentTokenStackSize == state.opponentTokenStackSize &&
                Objects.equals(tokenStack, state.tokenStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bid, opponentTokenStackSize, tokenStack);
    }

}
