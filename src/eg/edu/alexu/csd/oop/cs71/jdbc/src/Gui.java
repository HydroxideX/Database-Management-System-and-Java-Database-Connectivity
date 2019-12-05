package eg.edu.alexu.csd.oop.cs71.jdbc.src;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.util.Properties;


public class Gui extends Application {

    public static void main (String[] args) {
        launch(args);
    }

    TableView<Object[]> table = new TableView<>();
    static String success="";
    @Override
    public void start(Stage stage) {
        table.setEditable(true);
        Scene scene = new Scene(new Group());
        stage.setTitle("SQL Database");
        table.setPrefWidth(1150);
        table.setPrefHeight(550);
        //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        stage.setWidth(1200);
        stage.setHeight(700);
        stage.setResizable(false);
        TextField textField =new TextField();
        Button button =new Button("Execute Query");
        Button addBatch =new Button("Add to Batch");
        Button executeBacth =new Button("Execute Batch");
        Button clearBacth =new Button("Clear Batch");
        Button clear =new Button("Clear");
        clear.setOnAction(e->{
            textField.clear();
            textField.requestFocus();
        });
        SQLDriver SQLDriver =new SQLDriver();
        Properties info = new Properties();
        File dbDir = new File("Databases");
        info.put("path", dbDir.getAbsoluteFile());
        Statement statement=null;
        try{
             Connection connection = SQLDriver.connect("jdbc:xmldb://localhost", info);
            statement=connection.createStatement();

        }catch (Exception e)
        {
            success=e.getMessage();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(success);
            alert.setContentText("Please try again!");
            success="";
            alert.showAndWait();
        }
        Label rowNum=new Label();
        Statement finalStatement = statement;
        button.setOnAction(e->{
            String query=textField.getText();
            query=query.replaceAll(";","");
            Object object;
            query=query.toLowerCase();
            if(query.contains("select"))
            {
                try {
                    assert finalStatement != null;
                    object = finalStatement.executeQuery(query);
                    //method to transfer result set to 2d array of Objects
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }else if(query.contains("create")||query.contains("drop"))
            {
                try {
                    object = finalStatement.execute(query);

                    if ((boolean) object) {
                        rowNum.setText("Success");
                    } else {
                        rowNum.setText("Fail");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }else if(query.contains("update")||query.contains("insert")||query.contains("delete"))
            {
                try {
                    object = finalStatement.executeUpdate(query);
                    rowNum.setText("Rows Affected: "+object.toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }


            /*if(SQLvalidation.validateQuery(query))
            {
                object=facade.parse(query);
                currentDb.setText("Database: "+facade.engine.currentDatabase);
                query=query.toLowerCase();
                if(query.contains("select")&&object != null) {
                    rowNum.setText("");
                    table.getColumns().clear();
                    Object[][] x = facade.getFullTable((Object[][]) object);
                    ObservableList<Object[]> data = FXCollections.observableArrayList();
                    data.addAll(Arrays.asList(x));
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
            }*/
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid query");
                alert.setContentText("Please try again!");
                alert.showAndWait();
            }
        });
        addBatch.setOnAction(e->{
            String query=textField.getText();
            query=query.replaceAll(";","");
            Object object;
            query=query.toLowerCase();
            try {
                assert finalStatement != null;
                finalStatement.addBatch(query);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        clearBacth.setOnAction(e->{
            try {
                assert finalStatement != null;
                finalStatement.clearBatch();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        executeBacth.setOnAction(event -> {
            try {
                assert finalStatement != null;
                finalStatement.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        HBox hBox=new HBox();
        HBox buttons=new HBox();
        hBox.setSpacing(100);
        buttons.setSpacing(5);
        buttons.getChildren().addAll(button,clear,addBatch,clearBacth,executeBacth);
        hBox.getChildren().addAll(rowNum);
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