package it.polimi.ingsw.am49.view.gui.controllers;

import it.polimi.ingsw.am49.controller.room.RoomInfo;
import it.polimi.ingsw.am49.model.enumerations.Color;
import it.polimi.ingsw.am49.server.Server;
import it.polimi.ingsw.am49.server.exceptions.AlreadyInRoomException;
import it.polimi.ingsw.am49.server.exceptions.CreateRoomException;
import it.polimi.ingsw.am49.view.gui.SceneTitle;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.rmi.RemoteException;
import java.util.HashMap;

public class CreateRoomController extends GuiController {

    @FXML
    private Label createroomtitleLable;

    @FXML
    private Label nameLable;

    @FXML
    private Label numplayerLable;

    @FXML
    private TextField nameTextfield;

    @FXML
    private Spinner<Integer> numplayerSpinner;

    @FXML
    private Button createButton;

    private Server server;

    @Override
    public void init() {
        this.server = this.app.getServer();

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 4, 2);
        numplayerSpinner.setValueFactory( valueFactory);

        createButton.setOnAction(x-> execute());
        nameTextfield.setOnAction(x-> execute());
    }

    private boolean isRoomNameValid(String name){ return name.length()>=2 && name.length()<=15;}

    private boolean isNumPlayerValid(int numplayer){ return numplayer >= 2 && numplayer <= 4;}

    private void execute(){
        if(this.isRoomNameValid(nameTextfield.getText()) && this.isNumPlayerValid(numplayerSpinner.getValue())){
            try {
                RoomInfo roomInfo = this.server.createRoom(this.app, nameTextfield.getText(), numplayerSpinner.getValue(), this.app.getUsername());
                this.manager.setRoomInfo(roomInfo);
                this.manager.changeScene(SceneTitle.ROOM);
            } catch (CreateRoomException | RemoteException | AlreadyInRoomException e){
                System.out.println(e.getMessage());
            }

        }
    }

}
