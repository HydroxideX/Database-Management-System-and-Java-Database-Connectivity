package eg.edu.alexu.csd.oop.cs71.jdbc.src;

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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;


public class Gui extends Application {

    public TableView<Object[]> table = new TableView<>();
    public static String success="";
    public static void main (String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
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
        File dbDir = new File("");
        info.put("path", dbDir.getAbsoluteFile());
        Statement statement=null;
        Connection connection=null;
        try{
              connection = SQLDriver.connect("jdbc:xmldb://localhost", info);
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
                    table.getColumns().clear();
                    Resultset temp =(Resultset)object;
                    Object[][] x= convertResultSetTOArray(temp);
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
                } catch (SQLException ex) {
                    success=ex.getMessage();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(success);
                    alert.setContentText("Please try again!");
                    success="";
                    alert.showAndWait();

                }
            }else if(query.contains("create")||query.contains("drop")||query.contains("use"))
            {
                try {
                    object = finalStatement.execute(query);
                    if ((boolean) object) {
                        rowNum.setText("Success");
                    } else {
                        rowNum.setText("Fail");
                    }
                } catch (SQLException ex) {
                    success=ex.getMessage();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(success);
                    alert.setContentText("Please try again!");
                    success="";
                    alert.showAndWait();
                }
            }else if(query.contains("update")||query.contains("insert")||query.contains("delete"))
            {
                try {
                    object = finalStatement.executeUpdate(query);
                    rowNum.setText("Rows Affected: "+object.toString());
                } catch (SQLException ex) {
                    success=ex.getMessage();
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
        addBatch.setOnAction(e->{
            String query=textField.getText();
            query=query.replaceAll(";","");
            query=query.toLowerCase();
            try {
                assert finalStatement != null;
                finalStatement.addBatch(query);
            } catch (SQLException ex) {
                success=ex.getMessage();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(success);
                alert.setContentText("Please try again!");
                success="";
                alert.showAndWait();
            }
        });
        clearBacth.setOnAction(e->{
            try {
                assert finalStatement != null;
                finalStatement.clearBatch();

            } catch (SQLException ignored) {
            }
        });
        executeBacth.setOnAction(event -> {
            try {
                assert finalStatement != null;
                int x[]=finalStatement.executeBatch();
                int sum=0;
                for(int i=0;i<x.length;i++)
                    sum+=x[i];
                rowNum.setText("Rows Affected: "+sum);
            } catch (SQLException e) {
                success=e.getMessage();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(success);
                alert.setContentText("Please try again!");
                success="";
                alert.showAndWait();
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
        Connection finalConnection = connection;
        stage.setOnCloseRequest(e->{
            try {
                finalStatement.close();
                finalConnection.close();
                System.exit(0);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        });
        scene.setOnKeyPressed(ke -> {
            KeyCode kc = ke.getCode();
            if(kc.equals(KeyCode.ENTER))
            {
                button.fire();
            }
        });
    }

    private static Object[][] convertResultSetTOArray(Resultset rs) {
        try {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int cols = resultSetMetaData.getColumnCount();
            int rows = 0;
            while(!rs.isAfterLast())  {
                rows++;
                rs.next();
            }
            String[][] table = new String[rows+1][cols];
            while(!rs.isFirst())  {
                rows--;
                rs.previous();
            }
            for(int i = 0; i < cols; i++){
                table[rows][i] = resultSetMetaData.getColumnLabel(i+1);
            }
            rows++;
            while(true){
                if(rs.isAfterLast()) break;
                for(int i = 0; i < cols;i++){
                    Object temp = rs.getObject(i+1);
                    if(temp.toString().toLowerCase().equals("null")) table[rows][i] = "";
                    table[rows][i] = temp.toString();
                }
                rs.next();
                rows++;
            }
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String[5][5];
    }
}
