package com.ryannewbold.familytreemaven;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FXMLController implements Initializable {

    private double xOffSet = 0.0;
    private double yOffSet = 0.0;

    TreeItem<String>[] treeItemArray;
    @FXML
    private Button importFileButton;
    @FXML
    private TreeView<String> treeView;
    @FXML
    private Label titleLabel;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private FontAwesomeIcon minimizeButton;
    @FXML
    private FontAwesomeIcon closeButton;
    @FXML
    private TextArea textArea;

    final FileChooser fileChooser = new FileChooser();
    Stage stage;
    private Desktop desktop = Desktop.getDesktop();
    private ObservableList<TreeItem<String>> setAsideList = null;

    @FXML
    private void handleButtonAction(ActionEvent event) {

    }

    @FXML
    void handleCloseButton(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    void handleDisplayFamilyTree(ActionEvent event) throws IOException {
        String integerIndicator = "";
        int numberOfPeople;
        Scanner sc = new Scanner(textArea.getText());
        Scanner scanner2;
        
        System.out.println("Scanner1:  " + sc.toString());

        treeView.setVisible(true);

        integerIndicator = sc.nextLine();
        numberOfPeople = Integer.parseInt(integerIndicator);
        String[] peopleArray = new String[numberOfPeople];
        ObservableList<TreeItem<String>> peopleArrayList = null;
      
       
        TreeItem<String> treeItem = null;
        boolean wasInserted = false;

        for (int index = 0; index < numberOfPeople; index++) {
            
             peopleArrayList = FXCollections.observableArrayList();
            peopleArray[index] = sc.nextLine();
            scanner2 = new Scanner(peopleArray[index]);

            while (scanner2.hasNext()) {
                peopleArrayList.add(new TreeItem<>(scanner2.next()));
            }
            
            System.out.println("PeopleArrayList:  " + peopleArrayList.toString());

            treeItem = peopleArrayList.get(0);

            for (int i = 1; i < numberOfPeople; i++) {
                treeItem.getChildren().add(peopleArrayList.get(i));
            }

            if (treeView.getRoot() == null) {
                treeView.setRoot(treeItem);
                treeView.refresh();
            } else if (checkIfIsAncestorOfRoot(treeItem)) {
                treeView.setRoot(treeItem);
                treeView.refresh();
            } else {
                System.out.println("Build Tree Default:  " + treeItem.toString());
                System.out.println("Tree Item Children:  " + treeItem.getChildren().toString());
                insertTreeItem(treeView.getRoot(), treeItem);
                if ((checkToSeeIfWasInserted(treeItem, treeView.getRoot()) == false)) {
                    setAsideTreeItemForLaterInsertion(treeItem);
                }
            }
            
           
        }
        tryToInsertThoseThatWereSetAside();
    }

    @FXML
    void handleImportFileButton(MouseEvent event) {
        importFileButton.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File file = fileChooser.showOpenDialog(stage);
                String line = "";
                if (file != null) {
                    FileReader reader = null;
                    try {
                        reader = new FileReader(file);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    try {
                        line = bufferedReader.readLine();
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    while (line != null && line != "") {
                        try {
                            textArea.appendText((line) + "\n");
                            line = bufferedReader.readLine();
                        } catch (IOException ex) {
                            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
    }

    private void makeStageDraggable() {
        anchorPane.setOnMousePressed((event)
                -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });

        anchorPane.setOnMouseDragged((event)
                -> {
            MainApp.primaryStage.setX(event.getScreenX() - xOffSet);
            MainApp.primaryStage.setY(event.getScreenY() - yOffSet);
            MainApp.primaryStage.setOpacity(0.8f);
        });
        anchorPane.setOnDragDone((event)
                -> {
            MainApp.primaryStage.setOpacity(1.0f);
        });
        anchorPane.setOnMouseReleased((event)
                -> {
            MainApp.primaryStage.setOpacity(1.0f);
        });
    }

    public boolean checkIfIsAncestorOfRoot(TreeItem treeItem) {
        boolean isAncestorOfRoot = false;
        if (treeView.isVisible()) {
            if (rootIsDescendantOfTreeItem(treeView.getRoot().getValue(), treeItem)) {
                isAncestorOfRoot = true;
            }
        }
        return isAncestorOfRoot;
    }

    public boolean rootIsDescendantOfTreeItem(String string, TreeItem treeItem) {
        boolean isDescendant = false;
        if (treeItem.getChildren().contains(string)) {
            isDescendant = true;
        }
        return isDescendant;
    }

    @FXML
    void handleMinimizeButton(MouseEvent event) {
        MainApp.primaryStage.setIconified(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        makeStageDraggable();
    }

    private void insertTreeItem(TreeItem<String> root, TreeItem<String> treeItemToAdd) {
        for (TreeItem<String> personToCheck : root.getChildren()) {
            if (treeItemToAdd.getValue().equalsIgnoreCase(personToCheck.getValue())) {
                System.out.println("1st for loop");
                System.out.println("personToCheck:  " + personToCheck.toString());
                personToCheck.getChildren().addAll(treeItemToAdd.getChildren());
            } else {
                    if (checkToSeeIfWasInserted(treeItemToAdd, treeView.getRoot()) == false) {
                        insertTreeItem(personToCheck, treeItemToAdd);
                    } 
            }
        }
    }

    private boolean checkToSeeIfWasInserted(TreeItem<String> personToCheckIfWasInserted, TreeItem<String> root) {
        if (root.getValue().equalsIgnoreCase(personToCheckIfWasInserted.getValue())) {
            return true;
        } else {
            for (TreeItem<String> personToCheck : (ObservableList<TreeItem<String>>) root.getChildren()) {
                if (personToCheckIfWasInserted.getValue().equalsIgnoreCase(personToCheck.getValue())) {
                    return true;
                } else {
                    checkToSeeIfWasInserted(personToCheckIfWasInserted, personToCheck);
                }
            }
        }
        return false;
    }

    private void setAsideTreeItemForLaterInsertion(TreeItem<String> personToSetAside) {
        if (personToSetAside != null) {
            System.out.println(personToSetAside.getValue());
        }
        setAsideList.add(personToSetAside);
    }

    private void tryToInsertThoseThatWereSetAside() {
        int numberOfTimesToRunThroughList = setAsideList.size() * setAsideList.size();

        while (numberOfTimesToRunThroughList >= 0) {
            runThroughTheList();
            numberOfTimesToRunThroughList -= numberOfTimesToRunThroughList;
        }
    }

    private void runThroughTheList() {
        for (TreeItem<String> personToCheck : setAsideList) {
            insertTreeItem(treeView.getRoot(), personToCheck);
        }
    }
}
