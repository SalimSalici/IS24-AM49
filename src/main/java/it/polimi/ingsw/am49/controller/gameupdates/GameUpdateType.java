package it.polimi.ingsw.am49.controller.gameupdates;

/**
 * Enum representing the different types of game updates that can occur in the application.
 */
public enum GameUpdateType {
    /** Update for when a player joins the game */
    PLAYER_JOINED_UPDATE,

    /** Update for when a player leaves the game */
    PLAYER_LEFT_UPDATE,

    /** Update for when a starter card is assigned */
    STARTER_CARD_ASSIGNED_UPDATE,

    /** Update for when the game starts */
    GAME_STARTED_UPDATE,

    /** Update for when the player order is set */
    PLAYER_ORDER_UPDATE,

    /** Update for when the choosable objectives are assigned */
    CHOOSABLE_OBJETIVES_UPDATE,

    /** Update for when the player's hand changes */
    HAND_UPDATE,

    /** Update for when a player's hidden hand changes */
    HIDDEN_HAND_UPDATE,

    /** Update for when a card is placed */
    CARD_PLACED_UPDATE,

    /** Update for changes in the draw area */
    DRAW_AREA_UPDATE,

    /** Update for when the game state changes */
    GAME_STATE_UPDATE,

    /** Update for when the personal objective is chosen */
    PERSONAL_OBJECTIVE_CHOSEN_UPDATE,

    /** Update for the end of the game */
    END_GAME_UPDATE,
}
