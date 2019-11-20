package eg.edu.alexu.csd.oop.cs71.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Edit {
    ArrayList <String> columnNames= new ArrayList<String>() , operationNames = new ArrayList<String>();
    ArrayList <ArrayList<String >> oPParameters = new ArrayList<ArrayList<String>>();
    private void operationParser (String query) {
        String [] operation = query.split("(?i)(\\s*(and|or|not)\\s*)");
        for(int i=0 ;i<operation.length; i++){
            operation[i] = operation[i].replaceAll("\\s+|\\(+|\\)|\\,|\\'|\\\""," ");
            String [] parameters = operation[i].split(" "/*(?<!\\,)\\s+|(\\s*\\(*\\s*\\'\\s*\\,*\\s*\\)?\\s*)"*/);
            String cN =parameters[0] , oN = parameters[1];
            columnNames.add(cN);operationNames.add(oN);
            ArrayList<String> temp = new ArrayList<String>();
           for(int j=2;j<parameters.length;j++){
            if(!parameters[j].equals(""))temp.add(parameters[j]);
           }
           oPParameters.add(temp);
        }
        operation = query.split("(?i)(\\s*(between\\s*\\(|or|not)\\s*)");
        for(int i=0 ;i<columnNames.size();i++){
            System.out.print(columnNames.get(i) + "  " + operationNames.get(i) + "  ");
            for(int j =0 ;j<oPParameters.get(i).size();j++)System.out.print(oPParameters.get(i).get(j)+" ");
            System.out.println();
        }
    }
    public void update (ArrayList<HashMap<String,Object>>table ,String query ){
        int sz = table.size();
        query = "UPDATE table_name SET column1 = value1, column2 = value2 WHERE subject IN ('Maths', 'Science') and subject between ('15', '18') or subject = 'compu';";
        query = query.split("(\\w+\\s*)+(?i)(set)\\s*")[1];
        String ch = query.split("\\s*(?i)(where)\\s*")[0];
        String []changes = ch.split("(\\s*\\=\\s*)|(\\s*\\,\\s*)");
        query = query.split("\\s*(?i)(where)\\s*")[1];
        operationParser(query);
        /*for(int i=0 ;i<n ;i++){

        }*/
    }
    public static void  main(String[] args){
        Edit e = new Edit();
        e.update(new ArrayList<HashMap<String,Object>>() ,new String());
    }
}
