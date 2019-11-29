package eg.edu.alexu.csd.oop.cs71.db;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public interface FileManagementInterface {
    void writeInFile(String tableName, HashMap<String, String> tableColumns, ArrayList<HashMap<String, Object>> tableData, String currentDatabase);
    void  readFile(String tableName, HashMap<String, String> tableColumns, ArrayList<HashMap<String, Object>> tableData, String currentDatabase, ArrayList<String> cNames, ArrayList<String> cTypes) throws IOException, ParserConfigurationException, SAXException;
    String getTableName(String query);
}
