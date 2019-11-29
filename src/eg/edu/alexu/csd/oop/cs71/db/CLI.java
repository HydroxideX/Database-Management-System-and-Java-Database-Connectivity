package eg.edu.alexu.csd.oop.cs71.db;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

public class CLI {

    public static void main(String [] args){
        String query= "";
        ValidationInterface SQLvalidation = new SQLBasicValidation();
        Facade facade = new Facade();
        query = query.replaceAll(";","");
        if(SQLvalidation.validateQuery(query))
        {
            Object object;
            object=facade.parse(query);

        }
    }
}

