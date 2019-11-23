package eg.edu.alexu.csd.oop.cs71.db;


import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class Main implements Database {
    Parser secondparser = new Parser();
    static ArrayList<String> databases=new ArrayList<>();
    String currentDatabase= "";

    ArrayList<HashMap<String,Object>> tableData = new ArrayList<HashMap<String,Object>>();
    HashMap<String,String> tableColumns = new HashMap<String, String>();
    ArrayList<String> cNames= new ArrayList<>();
    ArrayList<String> cTypes= new ArrayList<>();


    public static void startUp()
    {
         File file ;
        try {
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();
            s+="\\Databases";
            file = new File(s);
            boolean flag = file.mkdir();
            // System.out.print("Directory created? " + flag);
            file = new File("Databases/");
            for( File child : file.listFiles()) {
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
                Gui.currentDb.setText("Database: "+currentDatabase);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (!dropIfExists){
            boolean exist = false;
            for (int i=0; i<databases.size(); i++){
                if (databases.get(i).equals(databaseName)) {
                    currentDatabase=databaseName;
                    Gui.currentDb.setText("Database: "+currentDatabase);
                    break;
                }
            }
        }
        currentDatabase=databaseName;
        Path currentRelativePath = Paths.get("");
        String currentpath = currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + databaseName;

        return currentpath;
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
                    Gui.currentDb.setText("Database: ");
                    currentDatabase="";
                    if (flag)
                        System.out.println("dir Deleted ");
                }
                else {
                    Gui.success=false;
                    return false;
                }
            }
        }
        if (query.contains("(")&&checker.contains("CREATE")&&secondChecker.contains("TABLE")) {
            if(currentDatabase.equals(""))
            {
                Gui.success=false;
                return false;
            }
            String tableName = command[2];
            Path currentRelativePath = Paths.get("");
            String tablePath = currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + currentDatabase +"\\" + tableName + ".xml";

            String[] allcolumns = query.split("\\(");
            String columns = allcolumns[1];
            columns=columns.trim();
            columns=columns.replaceAll(" , ",",");
            columns=columns.replaceAll(", ",",");
            columns=columns.replaceAll(" ,",",");
            String[] splitcolumns = columns.split(",");
            String[] columnName = new String[splitcolumns.length];
            String[] columnType = new String[splitcolumns.length];
            for (int i=0; i<splitcolumns.length; i++){
                String[] temp = splitcolumns[i].split(" ");
                columnName[i]=temp[0];
                columnType[i]=temp[1];
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
                Gui.success=false;
                return false;
            }
        }

        Gui.success=true;
        return true;
    }

    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        return new Object[0][];
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        String[] commad=query.split(" ",2);
        commad[0]=commad[0].toLowerCase();
        Parser parser=new Parser();
        //Read file here using table name
        switch (commad[0]){
            case "insert":{
                parser.select(query,cNames,cTypes,tableData);
            }
            break;
            case "update":{
                parser.update(query,tableData,cNames,cTypes);
            }
            break;
            case "delete":{
                parser.delete(query,tableData,cNames,cTypes);
            }
            break;
            case "alter":{
                parser.alter(query,cNames,cTypes,tableData);
            }
            break;
            default:{
                Gui.success=false;
                return 0;
            }
        }
        Gui.success=true;
        return 0;
    }

    public void writeInFile (String tableName){
        String[] columnTypes =new String[tableColumns.size()];
        String[] columnNames =new String[tableColumns.size()];
        String[] columnContents =new String[tableColumns.size()];
        for (int i=0; i<columnContents.length; i++){
            columnContents[i]="";
        }
        int index = 0;
        for (String str : tableColumns.keySet()) {
            columnNames[index++] = str;
        }
        for (int i=0; i<tableColumns.size(); i++){
            columnTypes[i]=tableColumns.get(columnNames[i]).toString();
        }
        for (int i=0; i<tableData.size(); i++){
            for (int j=0; j<tableColumns.size(); j++){
                columnContents[j]+=tableData.get(i).get(columnNames[j]).toString()+" ";
            }
        }

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder =docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("table");
            for (int i=0; i<columnNames.length; i++){
                Element column =doc.createElement(columnNames[i]);
                column.setAttribute("DataType",columnTypes[i]);
                String temp = columnContents[i].trim();
                Text columnContent = doc.createTextNode(temp);
                column.appendChild(columnContent);
                root.appendChild(column);
            }
            doc.appendChild(root);
            // doc is created we need to output it and then attach it to the xml file
            OutputFormat outputFormat =new OutputFormat(doc);
            outputFormat.setIndenting(true);
            Path currentRelativePath = Paths.get("");
            String tablePath = currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + currentDatabase +"\\" + tableName + ".xml";
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

    public void  readFile (String tableName){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Path currentRelativePath = Paths.get("");
            String path = currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + currentDatabase +"\\" + tableName + ".xml";
            FileInputStream fis = new FileInputStream(new File(path));
            InputSource is = new InputSource(fis);
            Document doc = builder.parse(is);
            // get the first element
            Element element = doc.getDocumentElement();
            // get all child nodes
            NodeList nodes = element.getChildNodes();

            tableData.clear();
            tableColumns.clear();
            String[] columnTypes =new String[nodes.getLength()];
            String[] columnNames =new String[nodes.getLength()];
            String[] columnContents =new String[nodes.getLength()];
            for (int i=0; i<columnContents.length; i++){
                columnContents[i]="";
            }
            for (int i = 0; i < nodes.getLength(); i++) {
                columnContents[i]= nodes.item(i).getTextContent();
            }

            for (int i=0; i<nodes.getLength(); i++){
                Node p = nodes.item(i);
                if (p.getNodeType()==Node.ELEMENT_NODE){
                    Element column =(Element)p;
                    columnTypes[i] =column.getAttribute("DataType");
                    columnNames[i] =column.getTagName();
                }
            }
            cNames.clear();
            cTypes.clear();
            for (int i=0; i<columnNames.length; i++){
                cNames.add(columnNames[i]);
                cTypes.add(columnTypes[i]);
                tableColumns.put(columnNames[i],columnTypes[i]);
            }
            String[] lenColumn = columnContents[0].split(" ");
            int lengthOfColumn = lenColumn.length;
            for (int i=0; i<lengthOfColumn; i++){
                HashMap<String, Object> row = new HashMap<String, Object>();
                for (int j=0; j<columnContents.length; j++) {
                    String[] columnCon = columnContents[j].split(" ");
                    row.put(columnNames[j],columnCon[i]);
                }
                tableData.add(row);
            }

        } catch (ParserConfigurationException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void  main(String[] args){
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
        a.readFile("fine");


    }
}

