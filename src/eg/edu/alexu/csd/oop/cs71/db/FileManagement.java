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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class FileManagement {
    void writeInFile(String tableName, HashMap<String, String> tableColumns, ArrayList<HashMap<String, Object>> tableData, String currentDatabase){
        String[] columnTypes =new String[tableColumns.size()];
        String[] columnNames =new String[tableColumns.size()];
        String[] columnContents =new String[tableColumns.size()];
        Arrays.fill(columnContents, "");
        int index = 0;
        for (String str : tableColumns.keySet()) {
            columnNames[index++] = str;
        }
        for (int i=0; i<tableColumns.size(); i++){
            columnTypes[i]= tableColumns.get(columnNames[i]);
        }
        for (HashMap<String, Object> tableDatum : tableData) {
            for (int j = 0; j < tableColumns.size(); j++) {
                columnContents[j] += tableDatum.get(columnNames[j]).toString() + " ";
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
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    void  readFile(String tableName, HashMap<String, String> tableColumns, ArrayList<HashMap<String, Object>> tableData, String currentDatabase, ArrayList<String> cNames, ArrayList<String> cTypes){
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
            String[] columnTypes =new String[nodes.getLength()/2];
            String[] columnNames =new String[nodes.getLength()/2];
            String[] columnContents =new String[nodes.getLength()/2];
            Arrays.fill(columnContents, "");
            int index=0;
            for (int i = 0; i < nodes.getLength(); i++) {
                if(i%2!=0) {
                    columnContents[index]= nodes.item(i).getTextContent();
                    index++;
                }
            }
            index=0;
            for (int i=0; i<nodes.getLength(); i++){
                Node p = nodes.item(i);
                if (p.getNodeType()==Node.ELEMENT_NODE){
                    Element column =(Element)p;
                    columnTypes[index] =column.getAttribute("DataType");
                    columnNames[index] =column.getTagName();
                    index++;
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
                HashMap<String, Object> row = new HashMap<>();
                for (int j=0; j<columnContents.length; j++) {
                    String[] columnCon = columnContents[j].split(" ");
                    row.put(columnNames[j],columnCon[i]);
                }
                tableData.add(row);
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Gui.success="Table doesn't exist!";
        }

    }

    String getTableName(String query) {
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
