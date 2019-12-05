package eg.edu.alexu.csd.oop.cs71.db;

import com.sun.xml.internal.ws.policy.EffectiveAlternativeSelector;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Arrays;


public class Gui extends Application {

    public static void main (String[] args) {
        launch(args);
    }

    TableView<Object[]> table = new TableView<>();
    static String success="";
    @Override
    public void start(Stage stage) {
        SQLDatabase.startUp();
        table.setEditable(true);
        ValidationInterface SQLvalidation = new SQLBasicValidation();
        Scene scene = new Scene(new Group());
        stage.setTitle("SQL Database");
        table.setPrefWidth(1150);
        table.setPrefHeight(550);
        //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        stage.setWidth(1200);
        stage.setHeight(700);
        stage.setResizable(false);
        TextField textField =new TextField();
        Button button =new Button("Run");
        Button clear =new Button("Clear");
        clear.setOnAction(e->{
            textField.clear();
            textField.requestFocus();
        });
        Facade facade = new Facade();
        Label currentDb=new Label();
        Label rowNum=new Label();
        button.setOnAction(e->{
            String query=textField.getText();
            query=query.replaceAll(";","");
            if(SQLvalidation.validateQuery(query))
            {
                Object object;
                object=facade.parse(query);
                currentDb.setText("Database: "+facade.engine.currentDatabase);
                query=query.toLowerCase();
                if(query.contains("select")&&object != null) {
                    rowNum.setText("");
                    table.getColumns().clear();
                    Object[][] x = facade.getFullTable((Object[][]) object);
                    ObservableList<Object[]> data = FXCollections.observableArrayList();
                    Object[][] y = new Object[x.length][x[0].length];
                    for(int i=0;i<x.length;i++)
                    {
                        for(int j=0;j<x[i].length;j++)
                        {
                            y[i][j]=x[i][j].toString();
                        }
                    }
                    data.addAll(Arrays.asList(y));
                    data.remove(0);
                    for (int i = 0; i < x[0].length; i++)
                    {
                        TableColumn tc = new TableColumn(x[0][i].toString());
                        tc.setMinWidth(150);
                        final int colNo = i;
                        tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object[], Object>, SimpleStringProperty>() {
                            @Override
                            public SimpleStringProperty call(TableColumn.CellDataFeatures<Object[], Object> p) {
                                return new SimpleStringProperty((String) p.getValue()[colNo]);
                            }
                        });
                        table.getColumns().add(tc);

                    }
                    table.setItems(data);
                }else if(!(query.contains("create")||query.contains("drop")||query.contains("use"))&&object != null)
                {
                    if((int)object!=-1)
                    {
                        rowNum.setText("Rows Number: "+object.toString());
                    }
                }
                else rowNum.setText("");

                if(!(success.equals(""))|| object == null)
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(success);
                    alert.setContentText("Please try again!");
                    success="";
                    alert.showAndWait();
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid query");
                alert.setContentText("Please try again!");
                alert.showAndWait();
            }
        });

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        HBox hBox=new HBox();
        HBox buttons=new HBox();
        hBox.setSpacing(100);
        buttons.setSpacing(5);
        buttons.getChildren().addAll(button,clear);
        hBox.getChildren().addAll(currentDb,rowNum);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(hBox,textField,buttons, table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed(ke -> {
            KeyCode kc = ke.getCode();
            if(kc.equals(KeyCode.ENTER))
            {
                button.fire();
            }
        });
    }
}