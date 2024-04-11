package it.polimi.ingsw.am49;

import it.polimi.ingsw.am49.model.cards.objectives.ObjectiveCard;
import it.polimi.ingsw.am49.model.cards.placeables.GoldCard;
import it.polimi.ingsw.am49.model.cards.placeables.ResourceCard;
import it.polimi.ingsw.am49.model.cards.placeables.StarterCard;
import it.polimi.ingsw.am49.model.decks.DeckLoader;
import it.polimi.ingsw.am49.model.decks.GameDeck;

public class Main {
    public static void main(String[] args) {
        GameDeck<ResourceCard> resourceGameDeck = DeckLoader.getInstance().getNewResourceDeck();
        GameDeck<GoldCard> goldGameDeck = DeckLoader.getInstance().getNewGoldDeck();
        GameDeck<StarterCard> starterGameDeck = DeckLoader.getInstance().getNewStarterDeck();
        GameDeck<ObjectiveCard> objectiveCardGameDeck = DeckLoader.getInstance().getNewObjectiveDeck();

        while (resourceGameDeck.size() > 0)
            System.out.println(resourceGameDeck.draw());

        while (goldGameDeck.size() > 0)
            System.out.println(goldGameDeck.draw());

        while (starterGameDeck.size() > 0)
            System.out.println(starterGameDeck.draw());

        while (objectiveCardGameDeck.size() > 0)
            System.out.println(objectiveCardGameDeck.draw());

    }
}
