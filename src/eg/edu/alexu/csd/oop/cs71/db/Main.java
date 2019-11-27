package eg.edu.alexu.csd.oop.cs71.db;


import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;


public class Main implements Database {
    private static ArrayList<String> databases=new ArrayList<>();
    String currentDatabase= "";
    private Parser parser = new Parser();
    private ArrayList<HashMap<String,Object>> tableData = new ArrayList<>();
    private HashMap<String,String> tableColumns = new HashMap<>();
    private ArrayList<String> cNames= new ArrayList<>();
    private ArrayList<String> cTypes= new ArrayList<>();
    private FileManagement fileManagement=new FileManagement();


    static void startUp()
    {
         File file ;
        try {
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();
            s+="\\Databases";
            file = new File(s);
            file.mkdir();
            // System.out.print("Directory created? " + flag);
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
        if (dropIfExists) {
            try {
                executeStructureQuery("drop database "+databaseName);
                executeStructureQuery("create database "+databaseName);
                currentDatabase=databaseName;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (!dropIfExists){
            boolean exist = false;
            for (String database : databases) {
                if (database.equals(databaseName)) {
                    currentDatabase = databaseName;
                    break;
                }
            }
        }
        currentDatabase=databaseName;
        Path currentRelativePath = Paths.get("");

        return currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + databaseName;
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException {
        String[] command=query.split(" ");
        String checker = query.substring(0, 8);
        checker = checker.toUpperCase();
        String secondChecker = command[1].toUpperCase();
        if (checker.contains("CREATE")) {
            if (secondChecker.contains("DATABASE")) {
                String s="";
                try {
                    Path currentRelativePath = Paths.get("");
                    s = currentRelativePath.toAbsolutePath().toString();
                    s+="\\Databases\\";
                    s+=command[2];
                    File file = new File(s);
                    boolean flag = file.mkdir();
                    databases.add(command[2]);
                    System.out.print("Directory created? " + flag);
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
                    Path currentRelativePath = Paths.get("");
                    File dir = new File(currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" +command[2]);
                    File[] listFiles = dir.listFiles();
                    for (File file : listFiles) {
                        //System.out.println("Deleting " + file.getName());
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
            Path currentRelativePath = Paths.get("");
            String tablePath = currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + currentDatabase +"\\" + tableName + ".xml";

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
                // doc is created we need to output it and then attach it to the xml file
                OutputFormat outputFormat =new OutputFormat(doc);
                outputFormat.setIndenting(true);
                FileOutputStream xmlfile = new FileOutputStream(new File(tablePath));
                // use xml serializer to serialize the xml data with the output format doc
                XMLSerializer serializer = new XMLSerializer(xmlfile,outputFormat);
                serializer.serialize(doc);
                xmlfile.close();
            } catch (ParserConfigurationException | FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(checker.contains("DROP")&&secondChecker.contains("TABLE")) {
            String tableName = command[2];
            Path currentRelativePath = Paths.get("");
            String tablePath = currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + currentDatabase +"\\" + tableName + ".xml";
            File file =new File(tablePath);

            if(!file.delete()){
                Gui.success="Table doesn't exist!";
                return false;
            }
        }
        Gui.success="";
        return true;
    }

    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        String tableName=fileManagement.getTableName(query);
        fileManagement.readFile(tableName,tableColumns,tableData,currentDatabase,cNames,cTypes);
        ArrayList<ArrayList<String>> result = parser.select(query,cNames,cTypes,tableData);
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
            int cur = 0;
            for (int i = 0;i<orderColumns.size();i++) {
                swapColumns(orderColumns.get(i),table,colNames,colTypes,cur++);
            }
            if(orderColumns.get(orderColumns.size()-1).toUpperCase().equals("DESC")){
                Collections.sort(table, new Comparator<List<Object>> () {
                    @Override
                    public int compare(List<Object> a, List<Object> b) {
                        int comparator = 0;
                        for(int i = 0;i<a.size();i++) {
                            if(a.get(i).toString().equals("NULL") || b.get(i).toString().equals("NULL")) continue;
                            if (cTypes.get(i).equals("int")) {
                                Integer x = Integer.valueOf(a.get(i).toString());
                                Integer y = Integer.valueOf(b.get(i).toString());
                                if( x.compareTo(y) > 0 && comparator == 0) {comparator = -1; break;}
                                else if ( x.compareTo(y) < 0 && comparator == 0) {comparator = 1; break;}
                            } else {
                                String s1 = a.get(i).toString();
                                String s2 = b.get(i).toString();
                                if( s1.compareTo(s2)> 0 && comparator == 0) {comparator = -1; break;}
                                else if ( s1.compareTo(s2) < 0 && comparator == 0) {comparator = 1; break;}
                            }
                        }
                        return comparator;
                    }
                });
            } else {
                Collections.sort(table, new Comparator<List<Object>> () {
                    @Override
                    public int compare(List<Object> a, List<Object> b) {
                        int comparator = 0;
                        for(int i = 0;i<a.size();i++) {
                            if(a.get(i).toString().equals("NULL") || b.get(i).toString().equals("NULL")) continue;
                            if (cTypes.get(i).equals("int")) {
                                Integer x = Integer.valueOf(a.get(i).toString());
                                Integer y = Integer.valueOf(b.get(i).toString());
                                if( x.compareTo(y) > 0 && comparator == 0) { comparator = 1;break;}
                                else if ( x.compareTo(y) < 0 && comparator == 0) {comparator = -1; break;}
                            } else {
                                String s1 = a.get(i).toString();
                                String s2 = b.get(i).toString();
                                if( s1.compareTo(s2)> 0 && comparator == 0) { comparator = 1; break;}
                                else if ( s1.compareTo(s2) < 0 && comparator == 0) {comparator = -1; break;}
                            }
                        }
                        return comparator;
                    }
                });
            }
        }

        Object [][] finalTable = new Object[selectedRows.size()+1][printColumns.size()];
        row = 1;
        int col = 0;
        for (int i = 0;i<colNames.size();i++) {
            for (int j = 0;j<printColumns.size();j++) {
                if (colNames.get(i).toUpperCase().equals(printColumns.get(j).toUpperCase())) {
                    finalTable[0][col]=colNames.get(i);
                    for (int k = 0;k < table.size();k++) {
                        finalTable[row++][col] = table.get(k).get(i);
                    }
                    col++;
                    row = 1;
                }
            }
        }
        fileManagement.writeInFile(tableName,tableColumns,tableData,currentDatabase);
        return finalTable;
    }

    void swapColumns (String Name, ArrayList<ArrayList <Object>> table, ArrayList<String> colNames,ArrayList<String> colTypes ,int cur) {
        int index = 0;
        for (int i = 0;i<colNames.size();i++) {
            if(Name.toUpperCase().equals(colNames.get(i).toUpperCase())){
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
        for (int i = 0;i<table.get(index).size();i++) {
            table.get(i).set(cur,temp.get(i));
        }
    }


    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        String[] commad=query.split(" ",2);
        commad[0]=commad[0].toLowerCase();
        int rowsNum=0;
        //Read file here using table name
        String tableName=fileManagement.getTableName(query);
        fileManagement.readFile(tableName,tableColumns,tableData,currentDatabase,cNames,cTypes);
        switch (commad[0]){
            case "insert":{
               rowsNum= parser.insert(query,tableData,cNames,cTypes);
            }
            break;
            case "update":{
              rowsNum= parser.update(query,tableData,cNames,cTypes);
            }
            break;
            case "delete":{
               rowsNum= parser.delete(query,tableData,cNames,cTypes);
            }
            break;
            case "alter":{
                rowsNum= parser.alter(query,cNames,cTypes,tableData);
            }
            break;
        }
        fileManagement.writeInFile(tableName,tableColumns,tableData,currentDatabase);
        Gui.success="";
        return rowsNum;
    }



    /*public static void  main(String[] args) throws SQLException {
        Main a =new Main();
        try {
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();
            s+="\\Databases";
            File file = new File(s);
            boolean flag = file.mkdir();
            // System.out.print("Directory created? " + flag);
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Hello World");
        System.out.println("Hello World");
        System.out.println("Hello World");
        HashMap<String,Object> row = new HashMap<String,Object>();
        row.put("subject","Maths");
        row.put("age","1");
        row.put("name","compu");
        a.tableData.add(row);
        HashMap<String,Object> row1 = new HashMap<String,Object>();
        row1.put("subject","Cv");
        row1.put("age","19");
        row1.put("name","omar");
        a.tableData.add(row1);
        a.tableColumns.put("subject","string");
        a.tableColumns.put("age","int");
        a.tableColumns.put("name","string");
        a.createDatabase("w", false);
        a.writeInFile("fine");
        System.out.println("first done");
        HashMap<String,Object> row2 = new HashMap<String,Object>();
        row2.put("subject","digital");
        row2.put("age","21");
        row2.put("name","mo");
        a.tableData.add(row2);
        a.writeInFile("fine");
        //a.readFile("fine");
        a.executeQuery("Select * from fine where name != 'omar'");
    }*/
}

