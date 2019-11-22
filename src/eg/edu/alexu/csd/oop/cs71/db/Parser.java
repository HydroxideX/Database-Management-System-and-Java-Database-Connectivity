package eg.edu.alexu.csd.oop.cs71.db;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {

    private ArrayList <String> columnNames= new ArrayList<>();
    private ArrayList <String> operationNames = new ArrayList<>();
    private ArrayList <String>  logicOperator =  new ArrayList<>();
    private ArrayList <String> answers = new ArrayList<>();
    private ArrayList <ArrayList<String >> oPParameters = new ArrayList<>();

    /*
    private String[] keywordArrayEquals = {"SELECT","FROM","*","WHERE","AND","OR","NOT", "'","ORDER", "BY",
            "COUNT","BY","AS","LIKE", "BETWEEN","DESC","NULL","IS","SUM","AVG","MAX","MIN",""};
    private String[] keywordArrayContains = {"'"};

    public ArrayList<String> getColumnNames (String query, ArrayList<String > colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table) {
        String q = query.replaceAll("[\\=,>,<,\\(,\\),;]", " ");
        q = q.substring(7,q.length());
        String[] collection = q.split(" ");
        ArrayList<String> ans = new ArrayList<String>();
        for (int i = 0;i<collection.length;i++) {
            if(In(collection[i])){
                continue;
            } else {
                if(collection[i-1].toUpperCase().equals("FROM")){
                    ans.add("TABLE:"+collection[i]);
                    continue;
                }
                ans.add(collection[i]);
            }
        }
        return ans;
    }

    private boolean In (String s) {
        for(int i = 0;i<keywordArrayEquals.length;i++){
            if(s.equals(keywordArrayEquals[i])) return true;
        }
        for(int i = 0;i<keywordArrayContains.length;i++){
            if(s.contains(keywordArrayContains[i])) return true;
        }
        return false;

       private String getColumnType(String columnName,ArrayList<String > colNames, ArrayList<String> colTypes){
        for(int i = 0;i <colNames.size();i++){
            if(columnName.equals(colNames.get(i))) return colTypes.get(i);
        }
        return "";
        }
    }*/

    public ArrayList<ArrayList<String>> select(String query, ArrayList<String > colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table) {
        logicOperator.clear();
        operationNames.clear();
        answers.clear();
        columnNames.clear();
        oPParameters.clear();
        ArrayList<String> printColumns = new ArrayList<>();
        ArrayList<String> selectedRows = new ArrayList<>();
        ArrayList<String> orderColumns = new ArrayList<>();
        query.replaceAll("<>","!=");
        String q = query.substring(7,query.length());
        String[] collection = q.split(" ");
        ArrayList<String> ans = new ArrayList<String>();
        int i = 0;
        if (q.contains("*")) {
            ans.add("*");
            ans.add(collection[1]);
            ans.add(collection[2]);
            i = 3;
            printColumns.add("*");
        } else {
            while (!collection[i].toUpperCase().equals("FROM")) {
                if (collection[i].charAt(collection[i].length() - 1) == ',') {
                    printColumns.add(collection[i].substring(0, collection[i].length() - 1));
                } else {
                    printColumns.add(collection[i]);
                }
                i++;
            }
            i+=2;
        }
        if (i <collection.length && collection[i].toUpperCase().contains("WHERE")) {
            ans.add(collection[i++]);
            String s = "";
            while(i < collection.length && !collection[i].toUpperCase().contains("ORDER")){
                s = s + " " +collection[i];
                i++;
            }
            operationParser(s.substring(1,s.length()),colNames,colTypes,table);
            operationPerformer(colNames,colTypes,table);
            for (int row = 0; row < table.size(); row++) {
                if (isTrue(answers.get(row),colNames,colTypes,table)) {
                    selectedRows.add(String.valueOf(row));
                }
            }
        } else {
            selectedRows.add("-1");
        }

        if(i<collection.length && collection[i].toUpperCase().contains("ORDER")){
            i+=2;
            while (i<collection.length) {
                if (collection[i].charAt(collection[i].length() - 1) == ',') {
                    orderColumns.add(collection[i].substring(0, collection[i].length() - 1));
                } else {
                    orderColumns.add(collection[i]);
                }
                i++;
            }
        } else {
            orderColumns.add("noOrder");
        }

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(printColumns);
        result.add(selectedRows);
        result.add(orderColumns);
        return result;
    }

    public void update (String query, ArrayList<String > colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table ) {
        logicOperator.clear();
        operationNames.clear();
        answers.clear();
        columnNames.clear();
        oPParameters.clear();
        int sz = table.size();
        query = query.split("(\\w+\\s*)+(?i)(set)\\s*")[1];
        String ch = query.split("\\s*(?i)(where)\\s*")[0];
        String[] changes = ch.split("(\\s*\\=\\s*)|(\\s*\\,\\s*)");
        if (query.toLowerCase().contains("where")) {
            query = query.split("\\s*(?i)(where)\\s*")[1];
            operationParser(query,colNames,colTypes,table);
            operationPerformer(colNames,colTypes,table);
            for (int i = 0; i < table.size(); i++) {
                if (isTrue(answers.get(i),colNames,colTypes,table)) {
                    HashMap<String, Object> row = table.get(i);
                    for (int j = 0; j < changes.length; j += 2) row.put(changes[j], changes[j + 1]);
                }
            }
        } else
            for (int i = 0; i < table.size(); i++) {
                HashMap<String, Object> row = table.get(i);
                for (int j = 0; j < changes.length; j += 2) row.put(changes[j], changes[j + 1]);
            }
    }

    public void delete (String query, ArrayList<String > colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table ){
        logicOperator.clear();
        operationNames.clear();
        answers.clear();
        columnNames.clear();
        oPParameters.clear();
        if(query.toLowerCase().contains("where")) {
            query = query.split("\\s*(?i)(where)\\s*")[1];
            operationParser(query,colNames,colTypes,table);
            operationPerformer(colNames,colTypes,table);
            for (int i = 0; i < table.size(); i++) {
                if (isTrue(answers.get(i),colNames,colTypes,table)) {
                    answers.remove(i);
                    table.remove(i);
                    i--;
                }
            }
        }
        else
            for (int i = 0; i < table.size(); i++) {
                table.remove(i);
                i--;
            }
    }

    private  void logicOperatorParser (String query, ArrayList<String > colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table){
        String [] operation = query.split(" ");
        for(int i=0 ;i<operation.length;i++){String s = operation[i].toLowerCase();
            if(s.equals("and")||s.equals("or")||s.equals("not")) {
                logicOperator.add(s);
            }
        }
    }
    private void operationParser (String query, ArrayList<String > colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table) {
        query = query .replaceAll("\\s+|\\(+|\\)|\\,|\\'|\\\"|\\;"," ");
        query = query .replaceAll("\\!\\s*(?!\\=)","not ");
        query = query .replaceAll("(\\&\\s*\\&) ","and");
        query = query .replaceAll("(\\|\\s*\\|) ","or");
        logicOperatorParser(query,colNames,colTypes,table);
        query = query .replaceAll("(?i)(not)\\s*","");
        query = query .replaceAll("\\s+"," ");
        String [] operation = query.split("(?i)(\\s*(and|or)\\s*)");
        for (int i=0 ;i<operation.length; i++) {
            String [] parameters = operation[i].split("\\s+");
            String cN =parameters[0] , oN = parameters[1];
            columnNames.add(cN);operationNames.add(oN.toLowerCase());
            ArrayList<String> temp = new ArrayList<String>();
            for (int j=2;j<parameters.length;j++) {
                if(!parameters[j].equals(""))temp.add(parameters[j]);
            }
            oPParameters.add(temp);
        }
    }
    private void operationPerformer (ArrayList<String > colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table) {
        for (int i = 0; i < table.size(); i++) {
            String ans = "";
            HashMap<String, Object> t = table.get(i);
            for (int j = 0; j < operationNames.size(); j++) {
                ArrayList<String> pp = oPParameters.get(j);
                String columnName = columnNames.get(j);
                String type = getColumnType(columnName,colNames,colTypes);
                if (type.equals("varchar")) {
                    switch (operationNames.get(j)) {
                        case "between":
                            if ((t.get(columnName)).toString().compareTo(pp.get(0)) >= 0 && (t.get(columnName)).toString().compareTo(pp.get(1)) <= 0) {
                                ans += "1";
                            } else ans += "0";
                            break;
                        case "in":
                            Boolean x = false;
                            for (int a = 0; a < pp.size(); a++) {
                                if ((t.get(columnName)).toString().equals(pp.get(a))) {
                                    ans += "1";
                                    x = true;
                                    break;
                                }
                            }
                            if (x) continue;
                            else ans += "0";
                            break;
                        case "=":
                            if (t.get(columnName).toString().equals(pp.get(0))) ans += "1";
                            else ans += "0";
                            break;
                        case ">=":
                            String sr = t.get(columnName).toString();
                            if (t.get(columnName).toString().compareTo(pp.get(0)) >= 0) ans += "1";
                            else ans += "0";
                            break;
                        case "<=":
                            if (t.get(columnName).toString().compareTo(pp.get(0)) <= 0) ans += "1";
                            else ans += "0";
                            break;
                        case ">":
                            if (t.get(columnName).toString().compareTo(pp.get(0)) > 0) ans += "1";
                            else ans += "0";
                            break;
                        case "<":
                            if (t.get(columnName).toString().compareTo(pp.get(0)) < 0) ans += "1";
                            else ans += "0";
                            break;
                        case "!=":
                        case "<>":
                            if (t.get(columnName).toString().compareTo(pp.get(0)) != 0) ans += "1";
                            else ans += "0";
                            break;
                    }
                } else {
                    switch (operationNames.get(j)) {
                        case "between":
                            if (Integer.valueOf(t.get(columnName).toString()) >= Integer.valueOf(pp.get(0)) && Integer.valueOf(t.get(columnName).toString()) <= Integer.valueOf(pp.get(1))) {
                                ans += "1";
                            } else ans += "0";
                            break;
                        case "in":
                            Boolean x = false;
                            for (int a = 0; a < pp.size(); a++) {
                                if (Integer.valueOf(t.get(columnName).toString()) == Integer.valueOf(pp.get(a))) {
                                    ans += "1";
                                    x = true;
                                    break;
                                }
                            }
                            if (x) continue;
                            else ans += "0";
                            break;
                        case "=":
                            if (Integer.valueOf(t.get(columnName).toString()) == Integer.valueOf(pp.get(0))) ans += "1";
                            else ans += "0";
                            break;
                        case ">=":
                            String sr = t.get(columnName).toString();
                            if (Integer.valueOf(t.get(columnName).toString()) >= Integer.valueOf(pp.get(0))) ans += "1";
                            else ans += "0";
                            break;
                        case "<=":
                            if (Integer.valueOf(t.get(columnName).toString()) <= Integer.valueOf(pp.get(0))) ans += "1";
                            else ans += "0";
                            break;
                        case ">":
                            if (Integer.valueOf(t.get(columnName).toString()) > Integer.valueOf(pp.get(0))) ans += "1";
                            else ans += "0";
                            break;
                        case "<":
                            if (Integer.valueOf(t.get(columnName).toString()) < Integer.valueOf(pp.get(0))) ans += "1";
                            else ans += "0";
                            break;
                        case "!=":
                        case "<>":
                            if (Integer.valueOf(t.get(columnName).toString()) != Integer.valueOf(pp.get(0))) ans += "1";
                            else ans += "0";
                            break;
                    }
                }
            }
            answers.add(ans);
        }
    }

    private String getColumnType(String columnName,ArrayList<String > colNames, ArrayList<String> colTypes){
        for(int i = 0;i <colNames.size();i++){
            if(columnName.equals(colNames.get(i))) return colTypes.get(i);
        }
        return "";
    }

    private Boolean isTrue (String ans, ArrayList<String > colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table) {
        String a = "",b = "" ;
        boolean negative;
        int j = 0 ;
        String callable ="";
        for (int i=0 ;i<logicOperator.size(); i++){
            negative = false;
            String s = logicOperator.get(i);
            if (s.equals("not"))
            {
                negative = !negative;
                i++;
                if(i != logicOperator.size()) s = logicOperator.get(i);
            }
            if(ans.charAt(j)=='1') a="TRUE";
            else a = "FALSE";
            j++;
            if(negative) {
                if(a == "TRUE") {
                    a = "FALSE";
                } else {
                    a = "TRUE";
                }
            }
            callable = callable + a;
            switch(s) {
                case "and":
                    callable = callable + " AND ";
                    break;
                case "or" :
                    callable = callable + " OR ";
                    break;
            }
        }
        if(j <ans.length()) {
            if (ans.charAt(j) == '1') callable += "TRUE";
            else callable += "FALSE";
        }
        return parseTrueAndFalse(callable);
    }

    public boolean parseTrueAndFalse(String expression){
        String[] stringsList = expression.split(" ");
        ArrayList<String> arrayListOfExpression = new ArrayList<>();
        for(int i = 0;i< stringsList.length;i++){
            arrayListOfExpression.add(stringsList[i]);
        }
        arrayListOfExpression = parseAnd(arrayListOfExpression);
        return parseOr(arrayListOfExpression);
    }

    private ArrayList<String> parseAnd(ArrayList<String> strings){
        ArrayList<String> answerList  = new ArrayList<String>();
        int temp = -1;
        for(int i = 0;i<strings.size();i++) {
            if(strings.get(i).equals("AND")) {
                if(answerList.get(temp).equals("TRUE") && strings.get(i+1).equals("TRUE")) {
                    answerList.add("TRUE");
                } else {
                    answerList.add("FALSE");
                }
                answerList.remove(temp);
                i++;
            } else {
                answerList.add(strings.get(i));
                temp++;
            }
        }
        return answerList;
    }

    private boolean parseOr(ArrayList<String> strings) {
        for (int i = 0;i<strings.size();i++) {
            if (strings.get(i).equals("TRUE")) return true;
        }
        return false;
    }

    /*
    public static void  main(String[] args) {
        Main2 e = new Main2();
        ArrayList<HashMap<String,Object>> table =new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> row = new HashMap<String, Object>();
        row.put("subject", "Maths");
        row.put("age", 1);
        row.put("name", "compu");
        table.add(row);
        row = new HashMap<String, Object>();
        row.put("subject", "Cv");
        row.put("age", 19);
        row.put("name", "omar");
        table.add(row);
        ArrayList<String > colNames = new ArrayList<>();
        ArrayList<String> colTypes = new ArrayList<>();
        colNames.add("subject");
        colNames.add("age");
        colNames.add("name");
        colTypes.add("varchar");
        colTypes.add("int");
        colTypes.add("varchar");
        e.select("seLect * from table1 where age >= 5 and name = 'omar' order by subject",colNames,colTypes,table);
        for (int i = 0; i < table.size(); i++) {
            System.out.println(table.get(i).get("name").toString() + " " + table.get(i).get("age").toString() + " " + table.get(i).get("subject").toString());
        }
        e.update("UPDATE table_name SET subject = value1, age = -2 WHERE not subject IN ('Maths', 'Science') and not age >= 15 or not name = 'compu'",colNames,colTypes,table);
        System.out.println("\nAfter Update_1");

        for (int i = 0; i < table.size(); i++) {
            System.out.println(table.get(i).get("name").toString() + " " + table.get(i).get("age").toString() + " " + table.get(i).get("subject").toString());
        }
        System.out.println("\nBefore Update_2");
        for (int i = 0; i < table.size(); i++) {
            System.out.println(table.get(i).get("name").toString() + " " + table.get(i).get("age").toString() + " " + table.get(i).get("subject").toString());
        }
        e.update("UPDATE table_name SET subject = value1, age = -2",colNames,colTypes,table);
        System.out.println("\nAfter Update_2");

        for (int i = 0; i < table.size(); i++) {
            System.out.println(table.get(i).get("name").toString() + " " + table.get(i).get("age").toString() + " " + table.get(i).get("subject").toString());
        }
        e.delete("DELETE From table_name WHERE nOt subject IN ('Maths', 'Science') AnD not age between (15, 18) oR not name = 'compu'; ",colNames,colTypes,table);

        System.out.println(" \nAfter Delete");
        for (int i = 0; i < table.size(); i++) {
            System.out.println(table.get(i).get("name").toString() + " " + table.get(i).get("age").toString() + " " + table.get(i).get("subject").toString());
        }
    }

     */
}
