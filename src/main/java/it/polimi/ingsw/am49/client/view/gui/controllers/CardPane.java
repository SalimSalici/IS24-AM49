package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import javafx.scene.layout.StackPane;

import java.util.Map;


/**
 * Represents a pane in the GUI that holds a card along with its position in a grid layout.
 * This is a record class that holds a {@link StackPane}, a {@link VirtualCard}, and the row and column indices.
 */
record CardPane(StackPane stackPane, VirtualCard card, int row, int col) { }
