package it.polimi.ingsw.am49.common.enumerations;

public enum Symbol {
    WOLVES,
    LEAVES,
    MUSHROOMS,
    BUGS,
    QUILL,
    INKWELL,
    MANUSCRIPT,
    EMPTY,
    FORBIDDEN;

    /**
     * Converts the current Symbol to its corresponding Resource.
     *
     * @return the corresponding Resource if the Symbol is one of BUGS, LEAVES, WOLVES, or MUSHROOMS; otherwise, null.
     */
    public Resource toResource() {
        Resource resource = null;
        switch (this) {
            case BUGS -> resource = Resource.BUGS;
            case LEAVES -> resource = Resource.LEAVES;
            case WOLVES -> resource = Resource.WOLVES;
            case MUSHROOMS -> resource = Resource.MUSHROOMS;
        }
        return resource;
    }

    /**
     * Converts the current Symbol to its corresponding Item.
     *
     * @return the corresponding Item if the Symbol is one of INKWELL, MANUSCRIPT, or QUILL; otherwise, null.
     */
    public Item toItem() {
        Item item = null;
        switch (this) {
            case INKWELL -> item = Item.INKWELL;
            case MANUSCRIPT -> item = Item.MANUSCRIPT;
            case QUILL -> item = Item.QUILL;
        }
        return item;
    }


}
