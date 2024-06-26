package it.polimi.ingsw.am49.client.view.gui.controllers;

import it.polimi.ingsw.am49.client.ClientApp;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.common.enumerations.Color;
import it.polimi.ingsw.am49.common.util.BiMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
    private final Map<Tab, ScrollPane> tabToScrollPane = new HashMap<>();
    private final Map<Tab, Integer> readMessageCount = new HashMap<>();
    private final Map<Tab, String> initialTabTitles = new HashMap<>();

    /**
     * Initializes the chat controller.
     */
    @Override
    public void init() {
        this.game = this.manager.getVirtualGame();
        this.myVirtualPlayer = game.getPlayerByUsername(ClientApp.getUsername());
        removeUnusedTabs();
        initializeTabs();

        for (Tab tab : chatTabs) {
            TextField textField = tabToTextField.getValue(tab);

            textField.setOnAction(e -> sendMessage(textField.getText(), myVirtualPlayer, tab));
            tabToButton.getValue(tab).setOnAction(e -> sendMessage(textField.getText(), myVirtualPlayer, tab));
        }

        chatTabpane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                updateSelectedTab(newTab);
            }
        });

        // Update the chat whenever it changes
        this.myVirtualPlayer.addObserver(() -> Platform.runLater(() -> {
            updateSelectedTab(chatTabpane.getSelectionModel().getSelectedItem());
            for (Tab tab : chatTabs) {
                if (!tab.isSelected()) {
                    updateUnreadMessagesCount(tab, tab == chatglobalTab ? myVirtualPlayer.getGlobalChat().size() : myVirtualPlayer.getPrivateChat(playerToChatTab.getKey(tab)).size());
                }
            }
        }));
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
     * Displays the conversation in the given tab and marks the tab as read.
     *
     * @param conversation the list of messages to display
     * @param selectedTab the tab to display the conversation in
     */
    private void displayConversation(List<String> conversation, Tab selectedTab) {
        VBox vBox = tabToVBox.get(selectedTab);
        vBox.getChildren().clear();
        ScrollPane scrollPane = tabToScrollPane.get(selectedTab);

        for (String text : conversation) {
            Text message = new Text(text);
            message.setStyle("-fx-padding: 3px;");
            TextFlow textFlow = new TextFlow(message);
            textFlow.setMaxWidth(vBox.getPrefWidth() - 10);
            vBox.getChildren().add(textFlow);
            Separator separator = new Separator();
            vBox.getChildren().add(separator);
        }

        readMessageCount.put(selectedTab, conversation.size());
        updateTabTitle(selectedTab, 0);

        // Ensure the layout is updated and bind the scrollPane's vvalue to the VBox height
        if (scrollPane != null) {
            vBox.heightProperty().addListener((observable, oldValue, newValue) -> {
                scrollPane.setVvalue(1.0);
            });
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
        this.manager.executorService.submit(() -> {
            this.gameController.chatMessage(message, recipient);
            tabToTextField.getValue(recipientTab).clear(); // Clear the text field after sending
        });
    }

    /**
     * Initializes the remaining chat tabs with the necessary components.
     */
    private void initializeTabs() {
        for (Tab tab : chatTabs) {
            AnchorPane anchorInTab = new AnchorPane();
            anchorInTab.setStyle("-fx-background-color: white;");
            anchorInTab.setPrefWidth(300);
            anchorInTab.setPrefHeight(333);

                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setPrefWidth(300);
                scrollPane.setPrefHeight(278);

                    VBox vBox = new VBox();
                    vBox.setLayoutX(5);
                    vBox.setSpacing(1);
                    vBox.setPrefWidth(282);
                    vBox.setPrefHeight(278);
                    scrollPane.setContent(vBox);

                HBox hBox = new HBox();
                hBox.setLayoutY(278);

                    TextField textField = new TextField();
                    textField.setPrefWidth(265);
                    textField.setPrefHeight(31);
                    hBox.getChildren().add(textField);

                    Button sendButton = new Button("↵");
                    sendButton.setPrefWidth(35);
                    sendButton.setPrefHeight(30);
                hBox.getChildren().add(sendButton);

            anchorInTab.getChildren().addAll(scrollPane, hBox);

            tab.setContent(anchorInTab);
            fillChildrenMap(tab, vBox, textField, sendButton, scrollPane);

            // Initialize unread messages count
            readMessageCount.put(tab, 0);

            // Set initial tab titles to empty values
            initialTabTitles.put(tab, "");
        }
    }

    /**
     * Fills the internal maps with the components of the tab.
     *
     * @param tab the tab to map components for
     * @param vBox the VBox for the tab
     * @param textField the TextField for the tab
     * @param button the Button for the tab
     * @param scrollPane the ScrollPane for the tab
     */
    private void fillChildrenMap(Tab tab, VBox vBox, TextField textField, Button button, ScrollPane scrollPane) {
        tabToTextField.put(tab, textField);
        tabToButton.put(tab, button);
        tabToVBox.put(tab, vBox);
        tabToScrollPane.put(tab, scrollPane);
    }

    /**
     * Removes unused tabs based on the players' colors.
     */
    private void removeUnusedTabs() {
        chatTabs.add(chatglobalTab);

        List<Color> chatColors = game.getPlayers().stream()
                .filter(player -> !player.equals(game.getPlayerByUsername(ClientApp.getUsername())))
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
        //Leaves only the global chat if there are only two player in the game
        if(game.getPlayers().size()<3){
            for (Tab tab : chatTabs)
                if(tab != chatglobalTab)
                    chatTabpane.getTabs().remove(tab);
        }
    }

    /**
     * Updates the unread messages count for a given tab.
     *
     * @param tab the tab to update the count for
     * @param currentSize the current count of messages
     */
    private void updateUnreadMessagesCount(Tab tab, int currentSize) {
        if (!tab.isSelected()) {
            int unread = currentSize - readMessageCount.get(tab);
            if (unread > 0) {
                updateTabTitle(tab, unread);
            }
        }
    }

    /**
     * Updates the title of a tab to reflect the number of unread messages.
     *
     * @param tab the tab to update the title for
     * @param unread the number of unread messages
     */
    private void updateTabTitle(Tab tab, int unread) {
        String tabTitle = initialTabTitles.get(tab);
        if (unread > 0) {
            tab.setText(tabTitle + " (" + unread + ")");
        } else {
            tab.setText(tabTitle);
        }
    }
}
