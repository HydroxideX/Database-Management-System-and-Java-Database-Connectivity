package eg.edu.alexu.csd.oop.cs71.db;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public interface FileManagementInterface {
    void writeInFile(String tableName, HashMap<String, String> tableColumns, ArrayList<HashMap<String, Object>> tableData, String currentDatabase);
    void  readFile(String tableName, HashMap<String, String> tableColumns, ArrayList<HashMap<String, Object>> tableData, String currentDatabase, ArrayList<String> cNames, ArrayList<String> cTypes) throws FileNotFoundException;
    String getTableName(String query);
}
