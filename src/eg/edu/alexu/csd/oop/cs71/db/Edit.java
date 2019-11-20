package eg.edu.alexu.csd.oop.cs71.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Edit {
    ArrayList <String> columnNames= new ArrayList<String>() , operationNames = new ArrayList<String>() , logicOperator =  new ArrayList<String>() ;
    ArrayList <ArrayList<String >> oPParameters = new ArrayList<ArrayList<String>>();

    private void operationParser (String query) {
        String [] operation = query.split("(?i)(\\s*(and|or|not)\\s*)");
        for(int i=0 ;i<operation.length; i++){
            operation[i] = operation[i].replaceAll("\\s+|\\(+|\\)|\\,|\\'|\\\"|\\;"," ");
            String [] parameters = operation[i].split(" ");
            String cN =parameters[0] , oN = parameters[1];
            columnNames.add(cN);operationNames.add(oN.toLowerCase());
            ArrayList<String> temp = new ArrayList<String>();
           for(int j=2;j<parameters.length;j++){
            if(!parameters[j].equals(""))temp.add(parameters[j]);
           }
           oPParameters.add(temp);
        }
        for(int i=0 ;i<columnNames.size();i++){
            System.out.print(columnNames.get(i) + " " + operationNames.get(i) + " ");
            for(int j =0 ;j<oPParameters.get(i).size();j++)System.out.print(oPParameters.get(i).get(j)+" ");
            System.out.println();
        }
    }
    public void operationPerformer (ArrayList<HashMap<String,Object>>table ){
        for(int i=0 ;i<table.size() ; i++){
            String ans  ="";
            HashMap<String,Object> t = table.get(i);
            for(int j=0 ;j<operationNames.size(); j++){
                ArrayList<String> pp = oPParameters.get(j);
                String columnName = columnNames.get(j);
                switch (operationNames.get(j)) {
                    case "between":
                            if ((t.get(columnName)).toString().compareTo(pp.get(0)) >= 0&&(t.get(columnName)).toString().compareTo(pp.get(1)) <= 0) {
                                ans += "1";
                            }
                            else ans +="0";
                        break;
                    case "in" :
                        Boolean x = false ;
                        for(int a=0 ;a<pp.size() ;a++){
                            if((t.get(columnName)).toString().equals(pp.get(a))) {
                                ans += "1";
                                x = true;
                                break;
                            }
                        }
                        if(x) continue;
                        else ans+="0";
                        break;
                    case "=" :
                        if(t.get(columnName).toString().equals(pp.get(0)))ans+="1";
                        else ans+="0";
                }
        }
            System.out.println(ans);
        }
    }
    public void update (ArrayList<HashMap<String,Object>>table ,String query ){
        int sz = table.size();
        query = "UPDATE table_name SET column1 = value1, column2 = value2 WHERE subject IN ('Maths', 'Science') and age between ('15', '18') or name = 'compu';";
        query = query.split("(\\w+\\s*)+(?i)(set)\\s*")[1];
        String ch = query.split("\\s*(?i)(where)\\s*")[0];
        String []changes = ch.split("(\\s*\\=\\s*)|(\\s*\\,\\s*)");
        query = query.split("\\s*(?i)(where)\\s*")[1];
        operationParser(query);
        operationPerformer(table);
        query.split(" ");
    }
    public static void  main(String[] args){
        Edit e = new Edit();
        ArrayList<HashMap<String,Object>> ex =new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> row = new HashMap<String,Object>();
        row.put("subject","Maths");
        row.put("age","1");
        row.put("name","compu");
        ex.add(row);
        row = new HashMap<String,Object>();
        row.put("subject","Cv");
        row.put("age","19");
        row.put("name","compu");
        ex.add(row);
        e.update(ex,new String());
    }
}
