package eg.edu.alexu.csd.oop.cs71.db;


import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import javafx.util.Pair;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;


public class SQLDatabase implements Database {
    private static SQLDatabase uniqueSQLDatabaseInstance;
    private static ArrayList<String> databases = new ArrayList<>();
    String currentDatabase;
    private SQLParser SQLParser;
    private ArrayList<HashMap<String,Object>> tableData;
    private HashMap<String,String> tableColumns;
    private ArrayList<String> cNames;
    private ArrayList<String> cTypes;
    private FileManagementInterface fileManagement;

    private SQLDatabase() {
        SQLParser = SQLParser.getInstance();
        tableData = new ArrayList<>();
        tableColumns = new HashMap<>();
        cNames= new ArrayList<>();
        cTypes= new ArrayList<>();
        fileManagement=new FileManagement();
        currentDatabase= new String();
    }
    public static synchronized SQLDatabase getInstance() {
        if (uniqueSQLDatabaseInstance == null) {
            uniqueSQLDatabaseInstance = new SQLDatabase();
        }
        return uniqueSQLDatabaseInstance;
    }

    static void startUp()
    {
         File file ;
        try {
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();
            s+="\\Databases";
            file = new File(s);
            file.mkdir();
            file = new File("Databases/");
            for( File child : Objects.requireNonNull(file.listFiles())) {
                databases.add(child.getName());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
       databaseName=databaseName.toLowerCase();
        if (dropIfExists) {
            try {
                executeStructureQuery("drop database "+databaseName);
                Gui.success="";
                executeStructureQuery("create database "+databaseName);
                currentDatabase=databaseName;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        boolean exist = true;
        if (!dropIfExists){
            for (String database : databases) {
                if (database.equals(databaseName)) {
                    currentDatabase = databaseName;
                    exist=true;
                    break;
                }
                exist=false;
            }
        }

        if(exist){
            currentDatabase=databaseName;
            String currentpath="";
            Path currentRelativePath = Paths.get("");
            currentpath = currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + databaseName;
            return currentpath;
        }
        Gui.success="Database doesn't exist";
        return null;
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException {
        query=query.toLowerCase();
        String[] command=query.split(" ");
        String checker = query.substring(0, 8);
        checker = checker.toUpperCase();
        String secondChecker = command[1].toUpperCase();
        if (checker.contains("CREATE")) {
            if (secondChecker.contains("DATABASE")) {
                String s= command[2];
                Path currentRelativePath = Paths.get("");
                s = currentRelativePath.toAbsolutePath().toString();
                s+="\\Databases\\";
                s+=command[2];
                try {
                    File file = new File(s);
                    file.getParentFile().mkdirs();
                    boolean flag = file.mkdir();
                    databases.add(command[2]);
                    System.out.print("Directory created? " + flag);
                    currentDatabase=command[2];
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (checker.contains("DROP")) {
            if (secondChecker.contains("DATABASE")) {
                boolean exist = false;
                int foundat=0;
                for (int i = 0; i < databases.size(); i++) {
                    if (databases.get(i).equals(command[2])) {
                        exist = true;
                        foundat=i;
                    }
                }
                if (exist) {
                    String s11 = new String();
                   /* if (command[2].contains("\\")){
                        s11=command[2];
                    }else{*/
                        Path currentRelativePath = Paths.get("");
                        s11= currentRelativePath.toAbsolutePath().toString();
                        s11+="\\Databases\\";
                        s11+=command[2];
                    //}
                    File dir = new File(s11);
                    File[] listFiles = dir.listFiles();
                    for (File file : listFiles) {
                        file.delete();
                    }
                    boolean flag=dir.delete();
                    databases.remove(foundat);
                    currentDatabase="";
                    if (flag)
                        System.out.println("dir Deleted ");
                }
                else {
                    Gui.success="Database doesn't exist!";
                    return false;
                }
            }
        }
        if (query.contains("(")&&checker.contains("CREATE")&&secondChecker.contains("TABLE")) {
            if(currentDatabase.equals(""))
            {
                Gui.success="Please select the desired database using \"use x\"";
                return false;
            }
            String tableName = command[2];
            if (tableName.contains("(")){
                String[] temp1 = tableName.split("\\(");
                tableName=temp1[0];
                tableName=tableName.toLowerCase();
            }
            String tablePath="";
           /* if (currentDatabase.contains("\\")){
                tablePath=currentDatabase+"\\"+tableName+".xml";
            }else{*/
                Path currentRelativePath = Paths.get("");
                tablePath = currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + currentDatabase +"\\" + tableName + ".xml";
            //}

            String[] allcolumns = query.split("\\(");
            String columns = allcolumns[1];
            columns=columns.trim();
            columns=columns.replace(")","");
            columns=columns.trim();
            columns=columns.replaceAll(" , ",",");
            columns=columns.replaceAll(", ",",");
            columns=columns.replaceAll(" ,",",");
            String[] splitcolumns = columns.split(",");
            String[] columnName = new String[splitcolumns.length];
            String[] columnType = new String[splitcolumns.length];
            for (int i=0; i<splitcolumns.length; i++){
                String[] temp = splitcolumns[i].split(" ");
                columnName[i]=temp[0].toLowerCase();
                columnType[i]=temp[1].toLowerCase();
            }

            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder =docFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();
                Element root = doc.createElement("table");
                for (int i=0; i<columnName.length; i++){
                    Element column =doc.createElement(columnName[i]);
                    column.setAttribute("DataType",columnType[i]);
                    Text columnContent = doc.createTextNode("");
                    column.appendChild(columnContent);
                    root.appendChild(column);
                }
                doc.appendChild(root);
                OutputFormat outputFormat =new OutputFormat(doc);
                outputFormat.setIndenting(true);
                FileOutputStream xmlfile = new FileOutputStream(new File(tablePath));
                XMLSerializer serializer = new XMLSerializer(xmlfile,outputFormat);
                serializer.serialize(doc);
                xmlfile.close();
            } catch (ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
        else if(checker.contains("DROP")&&secondChecker.contains("TABLE")) {
            String tableName = command[2];
            String tablePath="";
            if (currentDatabase.contains("\\")){
                tablePath=currentDatabase+"\\"+tableName+".xml";
            }else{
                Path currentRelativePath = Paths.get("");
                tablePath = currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + currentDatabase +"\\" + tableName + ".xml";
            }
            File file =new File(tablePath);

            if (!file.delete()) {
                Gui.success="Table doesn't exist!";
                return false;
            }
        }
        Gui.success="";
        return true;
    }

    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        //query=query.toLowerCase();
        String tableName=fileManagement.getTableName(query);
        ArrayList<ArrayList<String>> result;
        try {
           fileManagement.readFile(tableName,tableColumns,tableData,currentDatabase,cNames,cTypes);
           result = SQLParser.select(query, cNames, cTypes, tableData);
       }catch (FileNotFoundException e)
        {
            throw new SQLException();
        }
        catch (Exception e)
       {
           Gui.success=e.getMessage();
           return null;
       }
        ArrayList<String> colNames =  new ArrayList<>();
        for(int i = 0;i<cNames.size();i++) colNames.add(cNames.get(i));
        ArrayList<String> colTypes =  new ArrayList<>();
        for(int i = 0;i<cTypes.size();i++) colTypes.add(cTypes.get(i));
        ArrayList<String> printColumns = result.get(0);
        ArrayList<String> selectedRows = result.get(1);
        ArrayList<String> orderColumns = result.get(2);
        ArrayList <ArrayList<Object> >table = new ArrayList<>();
        ArrayList <Object> singleRow = new ArrayList<>();
        int row;
        for(int j = 0;j<selectedRows.size();j++){
            singleRow = new ArrayList<>();
            for (int i = 0;i < cNames.size();i++) {
                singleRow.add(tableData.get(Integer.valueOf(selectedRows.get(j)).intValue()).get(cNames.get(i)));
            }
            table.add(singleRow);
        }

        if(!orderColumns.get(0).equals("noOrder")){
            orderTable(orderColumns, colTypes,colNames , table);
        }
        Object [][] finalTable = transformToTable(colNames,printColumns,selectedRows,table,colTypes);
        return finalTable;
    }

    private void orderTable(ArrayList <String> orderColumns,ArrayList <String> colTypes,ArrayList<String> colNames ,ArrayList<ArrayList<Object>> table){
        ArrayList <Pair<Integer,Integer>> swapped = new ArrayList<>();
        ArrayList <Boolean> orderAscOrDesc = new ArrayList<>();
        for(int i = 0;i<orderColumns.size();i++){
            if(i < orderColumns.size()-1){
                if(orderColumns.get(i+1).toUpperCase().equals("DESC") ){
                    orderAscOrDesc.add(true);
                    i++;
                } else if(orderColumns.get(i+1).toUpperCase().equals("ASC") ){
                    orderAscOrDesc.add(false);
                    i++;
                } else {
                    orderAscOrDesc.add(false);
                }
            } else {
                orderAscOrDesc.add(false);
            }
        }
        int cur = 0;
        for (int i = 0;i<orderColumns.size();i++) {
            if(orderColumns.get(i).toUpperCase().equals("DESC") || orderColumns.get(i).toUpperCase().equals("ASC")) continue;
            swapColumns(orderColumns.get(i),table,colNames,colTypes,cur,swapped,true);
            cur++;
        }
        sort(table,orderAscOrDesc,colTypes);
        for (int i = swapped.size()-1;i>=0;i--) {
            swapColumns(colNames.get(swapped.get(i).getKey().intValue()),table,colNames,colTypes,swapped.get(i).getValue().intValue(),swapped,false);
        }
    }

    void swapColumns (String Name, ArrayList<ArrayList <Object>> table, ArrayList<String> colNames,ArrayList<String> colTypes ,int cur,ArrayList <Pair<Integer,Integer>> swapped,boolean addtoswapped) {
        int index = 0;
        for (int i = 0;i<colNames.size();i++) {
            if (Name.toUpperCase().equals(colNames.get(i).toUpperCase())) {
                index = i;
                colNames.set(i,colNames.get(cur));
                colNames.set(cur,Name);
                String temp = colTypes.get(i);
                colTypes.set(i,colTypes.get(cur));
                colTypes.set(cur,temp);
            }
        }
        ArrayList<Object> temp = new ArrayList<>();
        for (int i = 0;i<table.size();i++) {
            temp.add(table.get(i).get(index));
            table.get(i).set(index,table.get(i).get(cur));
        }
        for (int i = 0;table.size() != 0 && i<table.size();i++) {
            table.get(i).set(cur,temp.get(i));
        }
        Pair <Integer,Integer> p = new Pair<>(index,cur);
        if(addtoswapped)
            swapped.add(p);
    }

    private void  sort(ArrayList <ArrayList <Object> > table, ArrayList<Boolean> orderAscOrDesc,ArrayList <String> colTypes){
        Collections.sort(table, new Comparator<List<Object>> () {
            @Override
            public int compare(List<Object> a, List<Object> b) {
                int comparator = 0;
                for(int i = 0;i<a.size();i++) {
                    if(a.get(i).toString().equals("NULL") || b.get(i).toString().equals("NULL")) continue;
                    if (colTypes.get(i).equals("int")) {
                        Integer x = Integer.valueOf(a.get(i).toString());
                        Integer y = Integer.valueOf(b.get(i).toString());
                        if( x.compareTo(y) > 0 && comparator == 0) {
                            comparator = 1;
                            comparator = getCorrectPolarity(comparator,orderAscOrDesc,i);
                            break;
                        }
                        else if ( x.compareTo(y) < 0 && comparator == 0) {
                            comparator = -1;
                            comparator = getCorrectPolarity(comparator,orderAscOrDesc,i);
                            break;
                        }
                    } else {
                        String s1 = a.get(i).toString();
                        String s2 = b.get(i).toString();
                        if( s1.compareTo(s2)> 0 && comparator == 0) {
                            comparator = 1;
                            comparator = getCorrectPolarity(comparator,orderAscOrDesc,i);
                            break;
                        }
                        else if ( s1.compareTo(s2) < 0 && comparator == 0) {
                            comparator = -1;
                            comparator = getCorrectPolarity(comparator,orderAscOrDesc,i);
                            break;
                        }
                    }
                }
                return comparator;
            }
        });
    }

    int getCorrectPolarity(int comparator, ArrayList <Boolean> orderAscOrDesc, int i){
        if(i < orderAscOrDesc.size()){
            if(orderAscOrDesc.get(i)){
                comparator *= -1;
            }
        }
        return comparator;
    }

    Object[][] transformToTable(ArrayList<String> colNames, ArrayList<String> printColumns, ArrayList <String> selectedRows, ArrayList<ArrayList <Object> > table,ArrayList<String> colTypes){
        Object[][] finalTable =  new Object[selectedRows.size()][printColumns.size()];
        int row = 0;
        int col = 0;
        for (int i = 0;i<colNames.size();i++) {
            for (int j = 0;j<printColumns.size();j++) {
                if (colNames.get(i).toUpperCase().equals(printColumns.get(j).toUpperCase())) {
                    for (int k = 0;k < table.size();k++) {
                        if(colTypes.get(i).toLowerCase().equals("int")){
                            finalTable[row++][col] = Integer.parseInt(table.get(k).get(i).toString());
                            continue;
                        }
                        finalTable[row++][col] = table.get(k).get(i);
                    }
                    col++;
                    row = 0;
                }
            }
        }
        return finalTable;
    }


    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        query= query.toLowerCase();
        String[] commad=query.split(" ",2);
        commad[0]=commad[0].toLowerCase();
        int rowsNum=0;
        String tableName=fileManagement.getTableName(query);
        try {
            fileManagement.readFile(tableName, tableColumns, tableData, currentDatabase, cNames, cTypes);
        }catch (Exception e)
        {
            Gui.success=e.getMessage();
            throw new SQLException();
        }
        switch (commad[0]){
            case "insert":{
                try {
                    rowsNum= SQLParser.insert(query,tableData,cNames,cTypes);
                }catch (Exception e)
                {
                    Gui.success=e.getMessage();
                    return -1;
                }
            }
            break;
            case "update":{
             try{ rowsNum= SQLParser.update(query,tableData,cNames,cTypes);
            }catch (Exception e)
            {
                Gui.success=e.getMessage();
                return -1;
            }
            }
            break;
            case "delete":{
                    try{
                            rowsNum= SQLParser.delete(query,tableData,cNames,cTypes);
                        }catch (Exception e)
                        {
                            Gui.success=e.getMessage();
                            return -1;
                        }
            }
            break;
            case "alter":{
                try{
                rowsNum= SQLParser.alter(query,cNames,cTypes,tableData);
            }catch (Exception e)
            {
                Gui.success=e.getMessage();
                return -1;
            }
        }
            break;
        }
        if(rowsNum!=-1){
            fileManagement.writeInFile(tableName,tableColumns,tableData,currentDatabase);
            Gui.success="";
        }
        return rowsNum;
    }
}

