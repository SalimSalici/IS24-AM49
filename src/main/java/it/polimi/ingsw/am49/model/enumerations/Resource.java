package it.polimi.ingsw.am49.model.enumerations;

/**
 * Enum representing different types of resources.
 */
public enum Resource {
    WOLVES,
    LEAVES,
    MUSHROOMS,
    BUGS;

    /**
     * Converts the resource to its corresponding symbol.
     * 
     * @return the symbol associated with the resource, or FORBIDDEN if no association exists.
     */
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
