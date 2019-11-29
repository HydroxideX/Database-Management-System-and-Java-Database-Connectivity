package eg.edu.alexu.csd.oop.cs71.db;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
        stage.setTitle("Table View Sample");
        stage.setWidth(300);
        stage.setHeight(500);
        TextField textField =new TextField();
        Button button =new Button("Run");
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
                    data.addAll(Arrays.asList(x));
                    data.remove(0);
                    for (int i = 0; i < x[0].length; i++)
                    {
                        TableColumn tc = new TableColumn(x[0][i].toString());
                        final int colNo = i;
                        tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object[], String>, ObservableValue<String>>() {
                            @Override
                            public ObservableValue<String> call(TableColumn.CellDataFeatures<Object[], String> p) {
                                return new SimpleStringProperty((String) p.getValue()[colNo]);
                            }
                        });
                        tc.setPrefWidth(90);
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
        hBox.setSpacing(100);
        hBox.getChildren().addAll(currentDb,rowNum);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(hBox,textField,button, table);

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