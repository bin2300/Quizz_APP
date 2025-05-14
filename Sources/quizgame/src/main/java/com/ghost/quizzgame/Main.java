package com.ghost.quizzgame;

import com.ghost.quizzgame.ui.Menu;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    public static void main(String[] args) {
        launch(args);  // lance JavFx
    }

    @Override
    public void start(Stage primaryStage){
        Menu.display(primaryStage); // afficher le menu cle
    }
}
