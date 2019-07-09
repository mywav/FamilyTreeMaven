package com.ryannewbold.familytreemaven;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MainApp extends Application 
{
    
    public static Stage primaryStage = null;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/FamilyTreeMaven.fxml"));
        stage.setTitle("Family Tree Display");
        stage.setScene(new Scene(root));
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }

    public static void main(String[] args) 
    {
        launch(args);
    }

}
