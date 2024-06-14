package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.client.virtualmodel.VirtualGame;
import it.polimi.ingsw.am49.client.virtualmodel.VirtualPlayer;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.util.BiMap;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class ChatController extends GuiController{

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
    @FXML
    private

    VirtualGame game;
    private final BiMap<VirtualPlayer, Tab> playerToChatTab = new BiMap<>();
    private String message;




    @Override
    public void init(){
        this.game = app.getVirtualGame();


        configureTabs();
    }

    private void drawChat (Tab selectedTab){

    }

    private void sendMessage(String message, VirtualPlayer sender, Tab recipient){

    }


    private void configureTabs(){
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
                    case BLUE -> playerToChatTab.put(game.getVirtualPlayerByColor(color), chatblueTab);
                    case RED -> playerToChatTab.put(game.getVirtualPlayerByColor(color), chatredTab);
                    case GREEN -> playerToChatTab.put(game.getVirtualPlayerByColor(color), chatgreenTab);
                    case YELLOW -> playerToChatTab.put(game.getVirtualPlayerByColor(color), chatyellowTab);
                }
            }
        }
    }
}
