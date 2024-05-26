package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualCard;
import javafx.scene.layout.StackPane;

import java.util.Map;

record CardPane(StackPane stackPane, VirtualCard card, int row, int col) { }
