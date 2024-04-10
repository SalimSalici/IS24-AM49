package it.polimi.ingsw.am49.model.enumerations;

public enum Resource {
    WOLVES,
    LEAVES,
    MUSHROOMS,
    BUGS;

    public Symbol toSymbol() {
        Symbol symbol = Symbol.FORBIDDEN;
        switch (this) {
            case BUGS -> symbol = Symbol.BUGS;
            case LEAVES -> symbol = Symbol.LEAVES;
            case WOLVES -> symbol = Symbol.WOLVES;
            case MUSHROOMS -> symbol = Symbol.MUSHROOMS;
        }
        return symbol;
    }
}
