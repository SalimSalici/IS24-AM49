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

/**
 * Controller for managing chat functionality in the GUI.
 */
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
    private final BiMap<Tab, TextField> tabToTextField = new BiMap<>();
    private final BiMap<Tab, Button> tabToButton = new BiMap<>();
    private final Map<Tab, VBox> tabToVBox = new HashMap<>();

    /**
     * Initializes the chat controller.
     */
    @Override
    public void init() {
        this.game = app.getVirtualGame();
        this.myVirtualPlayer = game.getPlayerByUsername(this.app.getUsername());
        configureTabs();

        for (Tab tab : chatTabs) {
            handleChat(tab);
        }

        chatTabpane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                updateSelectedTab(newTab);
            }
        });

        // Update the chat whenever it changes
        this.myVirtualPlayer.addObserver(() -> Platform.runLater(this::updateCurrentTab));
    }

    /**
     * Updates the conversation display based on the selected tab.
     *
     * @param selectedTab the currently selected tab
     */
    private void updateSelectedTab(Tab selectedTab) {
        if (selectedTab == chatglobalTab) {
            displayConversation(myVirtualPlayer.getGlobalChat(), selectedTab);
        } else {
            VirtualPlayer player = playerToChatTab.getKey(selectedTab);
            if (player != null) {
                displayConversation(myVirtualPlayer.getPrivateChat(player), selectedTab);
            }
        }
    }

    /**
     * Updates the conversation display for the currently selected tab.
     */
    private void updateCurrentTab() {
        Tab selectedTab = chatTabpane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            updateSelectedTab(selectedTab);
        }
    }

    /**
     * Sets up the chat functionality for a given tab.
     *
     * @param selectedTab the tab to handle chat for
     */
    private void handleChat(Tab selectedTab) {
        TextField textField = tabToTextField.getValue(selectedTab);

        textField.setOnAction(e -> sendMessage(textField.getText(), myVirtualPlayer, selectedTab));
        tabToButton.getValue(selectedTab).setOnAction(e -> sendMessage(textField.getText(), myVirtualPlayer, selectedTab));
    }

    /**
     * Displays the conversation in the given tab.
     *
     * @param conversation the list of messages to display
     * @param selectedTab the tab to display the conversation in
     */
    private void displayConversation(List<String> conversation, Tab selectedTab) {
        VBox vBox = tabToVBox.get(selectedTab);
        vBox.getChildren().clear();
        for (String text : conversation) {
            Label message = new Label(text);
            message.setWrapText(true);
            message.setMaxWidth(vBox.getPrefWidth());
            vBox.getChildren().add(message);
        }
    }

    /**
     * Sends a message from the sender to the recipient tab.
     *
     * @param message the message to send
     * @param sender the player sending the message
     * @param recipientTab the tab of the recipient
     */
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

    /**
     * Configures the chat tabs by removing unused tabs and initializing the remaining ones.
     */
    private void configureTabs() {
        removeUnusedTabs();
        initializeTabs();
    }

    /**
     * Initializes the remaining chat tabs with the necessary components.
     */
    private void initializeTabs() {
        for (Tab tab : chatTabs) {
            AnchorPane anchorInTab = new AnchorPane();
            anchorInTab.setStyle("-fx-background-color: white;");
            anchorInTab.setPrefWidth(293);
            anchorInTab.setPrefHeight(333);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setPrefWidth(293);
            scrollPane.setPrefHeight(275);

            VBox vBox = new VBox();
            vBox.setSpacing(2);
            vBox.setPrefWidth(275);
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

            anchorInTab.getChildren().addAll(scrollPane, hBox);

            tab.setContent(anchorInTab);
            fillChildrenMap(tab, vBox, textField, sendButton);
        }
    }

    /**
     * Fills the internal maps with the components of the tab.
     *
     * @param tab the tab to map components for
     * @param vBox the VBox for the tab
     * @param textField the TextField for the tab
     * @param button the Button for the tab
     */
    private void fillChildrenMap(Tab tab, VBox vBox, TextField textField, Button button) {
        tabToTextField.put(tab, textField);
        tabToButton.put(tab, button);
        tabToVBox.put(tab, vBox);
    }

    /**
     * Removes unused tabs based on the players' colors.
     */
    private void removeUnusedTabs() {
        chatTabs.add(chatglobalTab);

        List<Color> chatColors = game.getPlayers().stream()
                .filter(player -> !player.equals(game.getPlayerByUsername(this.app.getUsername())))
                .map(VirtualPlayer::getColor)
                .toList();

        for (Color color : Color.values()) {
            if (!chatColors.contains(color)) {
                switch (color) {
                    case BLUE -> chatTabpane.getTabs().remove(chatblueTab);
                    case RED -> chatTabpane.getTabs().remove(chatredTab);
                    case GREEN -> chatTabpane.getTabs().remove(chatgreenTab);
                    case YELLOW -> chatTabpane.getTabs().remove(chatyellowTab);
                }
            } else {
                switch (color) {
                    case BLUE -> {
                        playerToChatTab.put(game.getVirtualPlayerByColor(color), chatblueTab);
                        chatTabs.add(chatblueTab);
                    }
                    case RED -> {
                        playerToChatTab.put(game.getVirtualPlayerByColor(color), chatredTab);
                        chatTabs.add(chatredTab);
                    }
                    case GREEN -> {
                        playerToChatTab.put(game.getVirtualPlayerByColor(color), chatgreenTab);
                        chatTabs.add(chatgreenTab);
                    }
                    case YELLOW -> {
                        playerToChatTab.put(game.getVirtualPlayerByColor(color), chatyellowTab);
                        chatTabs.add(chatyellowTab);
                    }
                }
            }
        }
    }
}
