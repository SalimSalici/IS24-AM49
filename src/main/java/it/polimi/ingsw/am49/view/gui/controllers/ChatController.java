package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.chat.ChatMSG;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.util.BiMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController extends GuiController {

    @FXML
    private TabPane chatTabpane;
    @FXML
    private Tab chatglobalTab;
    @FXML
    private Tab chatredTab;
    @FXML
    private Tab chatblueTab;
    @FXML
    private Tab chatgreenTab;
    @FXML
    private Tab chatyellowTab;

    private VirtualGame game;
    private VirtualPlayer myVirtualPlayer;
    private final BiMap<VirtualPlayer, Tab> playerToChatTab = new BiMap<>();
    private final List<Tab> chatTabs = new ArrayList<>();
    //These maps are neaded because javafx doesn't allow easy access to children without type casts
    private final BiMap<Tab, TextField> tabToTextField = new BiMap<>();
    private final BiMap<Tab, Button> tabToButton = new BiMap<>();
    private final Map<Tab, VBox> tabToVBox = new HashMap<>();


    @Override
    public void init(){
        this.game = app.getVirtualGame();
        this.myVirtualPlayer = game.getPlayerByUsername(this.app.getUsername());
        configureTabs();

        for (Tab tab : chatTabs){
            handleChat(tab);
        }

    }

    private void handleChat(Tab selectedTab) {
        TextField textField = tabToTextField.getValue(selectedTab);

        textField.setOnAction(e -> sendMessage(textField.getText(), myVirtualPlayer, selectedTab));
        tabToButton.getValue(selectedTab).setOnAction(e -> sendMessage(textField.getText(), myVirtualPlayer, selectedTab));

        this.myVirtualPlayer.addObserver(() -> {
            Platform.runLater(() -> {
                if (selectedTab == chatglobalTab) {
                    displayConversation(myVirtualPlayer.getGlobalChat(), selectedTab);
                } else {
                    displayConversation(myVirtualPlayer.getPrivateChat(playerToChatTab.getKey(selectedTab)), selectedTab);
                }
            });
        });
    }

    private void displayConversation(List<String> conversation, Tab selectedTab){
        VBox vBox = tabToVBox.get(selectedTab);
        vBox.getChildren().clear();
        for (String text : conversation){
            Label message = new Label(text);
            vBox.getChildren().add(message);
        }
    }

    private void sendMessage(String message, VirtualPlayer sender, Tab recipientTab) {
        if (message.trim().isEmpty()) {
            return;
        }
        String senderUsername = sender.getUsername();
        String recipient;
        if (recipientTab == chatglobalTab) {
            recipient = "*";
        } else {
            recipient = playerToChatTab.getKey(recipientTab).getUsername();
        }
        try {
            this.app.getServer().chatMessage(this.app, new ChatMSG(message, senderUsername, recipient));
            tabToTextField.getValue(recipientTab).clear(); // Clear the text field after sending
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void configureTabs(){
        removeUnusedTabs();
        initializeTabs();
    }

    private void initializeTabs(){
        for(Tab tab : chatTabs){
            AnchorPane anchorInTab = new AnchorPane();
            anchorInTab.setStyle("-fx-background-color: white;");
            anchorInTab.setPrefWidth(293);
            anchorInTab.setPrefHeight(333);

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setPrefWidth(293);
                scrollPane.setPrefHeight(275);

                    VBox vBox = new VBox();
                    vBox.setPrefWidth(285);
                    vBox.setPrefHeight(275);
                    scrollPane.setContent(vBox);

                HBox hBox = new HBox();
                hBox.setLayoutY(275);

                    TextField textField = new TextField();
                    textField.setPrefWidth(265);
                    textField.setPrefHeight(26);
                    hBox.getChildren().add(textField);

                    Button sendButton = new Button("↵");
                    sendButton.setPrefWidth(25);
                    sendButton.setPrefHeight(26);
                hBox.getChildren().add(sendButton);

            // Aggiungi ScrollPane e HBox all'AnchorPane
            anchorInTab.getChildren().addAll(scrollPane, hBox);
            // Imposta l'AnchorPane come contenuto del tab
            tab.setContent(anchorInTab);
            fillChildrenMap(tab, vBox, textField, sendButton);
        }
    }

    private void fillChildrenMap(Tab tab, VBox vBox, TextField textField, Button button){
        tabToTextField.put(tab, textField);
        tabToButton.put(tab, button);
        tabToVBox.put(tab, vBox);
    }

    private void removeUnusedTabs(){
        chatTabs.add(chatglobalTab);

        List<Color> chatColors = game.getPlayers().stream()
                .filter(player -> !player.equals(game.getPlayerByUsername(this.app.getUsername())))
                .map(VirtualPlayer::getColor)
                .toList();

        for(it.polimi.ingsw.am49.model.enumerations.Color color : it.polimi.ingsw.am49.model.enumerations.Color.values()){
            if(!chatColors.contains(color)){
                switch (color){
                    case BLUE -> chatTabpane.getTabs().remove(chatblueTab);
                    case RED -> chatTabpane.getTabs().remove(chatredTab);
                    case GREEN -> chatTabpane.getTabs().remove(chatgreenTab);
                    case YELLOW -> chatTabpane.getTabs().remove(chatyellowTab);
                }
            }else{
                switch (color){
                    case BLUE -> {playerToChatTab.put(game.getVirtualPlayerByColor(color), chatblueTab); chatTabs.add(chatblueTab);}
                    case RED -> {playerToChatTab.put(game.getVirtualPlayerByColor(color), chatredTab); chatTabs.add(chatredTab);}
                    case GREEN -> {playerToChatTab.put(game.getVirtualPlayerByColor(color), chatgreenTab); chatTabs.add(chatgreenTab);}
                    case YELLOW -> {playerToChatTab.put(game.getVirtualPlayerByColor(color), chatyellowTab); chatTabs.add(chatyellowTab);}
                }
            }
        }
    }
}
