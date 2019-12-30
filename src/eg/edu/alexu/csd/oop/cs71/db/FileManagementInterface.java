package eg.edu.alexu.csd.oop.cs71.db;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class FileManagementInterface {
    abstract void  writeInFile(String tableName, HashMap<String, String> tableColumns, ArrayList<HashMap<String, Object>> tableData, String currentDatabase,ArrayList <String> cNames, ArrayList<String> cTypes);
    abstract void  readFile(String tableName, HashMap<String, String> tableColumns, ArrayList<HashMap<String, Object>> tableData, String currentDatabase, ArrayList<String> cNames, ArrayList<String> cTypes) throws IOException, ParserConfigurationException, SAXException;
    String getTableName(String query){
        String tableName="";
        String[] parts=query.split(" ");
        parts[0]=parts[0].toLowerCase();
        switch (parts[0])
        {
            case "select":{
                int index=0;
                for (String x:parts) {
                    x=x.toLowerCase();
                    if(x.equals("from")){
                        index++;
                        break;
                    }
                    index++;
                }
                tableName=parts[index];
            }
            break;
            case "insert":
            case "alter":
            case "delete":{
                tableName=parts[2];
                parts=tableName.split("\\(" );
                tableName=parts[0];
            }
            break;
            case "update":{
                tableName=parts[1];
            }
            break;
        }
        return tableName;
    }
}
