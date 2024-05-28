package it.polimi.ingsw.am49.model.enumerations;

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
