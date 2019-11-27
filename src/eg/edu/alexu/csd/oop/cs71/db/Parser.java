package eg.edu.alexu.csd.oop.cs71.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private ArrayList<String> columnNames = new ArrayList<>();
    private ArrayList<String> operationNames = new ArrayList<>();
    private ArrayList<String> logicOperator = new ArrayList<>();
    private ArrayList<String> answers = new ArrayList<>();
    private ArrayList<ArrayList<String>> oPParameters = new ArrayList<>();

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
    }*/

    public ArrayList<ArrayList<String>> select(String query, ArrayList<String> colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table) throws Exception {
        clearMemory();
        ArrayList<String> printColumns = new ArrayList<>();
        ArrayList<String> selectedRows = new ArrayList<>();
        ArrayList<String> orderColumns = new ArrayList<>();
        query.replaceAll("<>", "!=");
        String q = query.substring(7, query.length());
        String[] collection = q.split(" ");
        ArrayList<String> ans = new ArrayList<String>();
        int i = 0;
        if (q.contains("*")) {
            ans.add("*");
            ans.add(collection[1]);
            ans.add(collection[2]);
            i = 3;
            for (int col = 0; col < colNames.size(); col++) {
                printColumns.add(colNames.get(col));
            }
        } else {
            while (!collection[i].toUpperCase().equals("FROM")) {
                if (collection[i].charAt(collection[i].length() - 1) == ',') {
                    printColumns.add(collection[i].substring(0, collection[i].length() - 1));
                } else {
                    printColumns.add(collection[i]);
                }
                i++;
            }
            i += 2;
        }
        if (i < collection.length && collection[i].toUpperCase().contains("WHERE")) {
            ans.add(collection[i++]);
            String s = "";
            while (i < collection.length && !collection[i].toUpperCase().contains("ORDER")) {
                s = s + " " + collection[i];
                i++;
            }
            operationParser(s.substring(1, s.length()), colNames, colTypes, table);
            Validation_Tany_3shan_5ater_sh3rawy_2lvalidation_bta3th_mbt3mlsh_7aga(columnNames, oPParameters, colNames, colTypes);
            operationPerformer(colNames, colTypes, table);
            for (int row = 0; row < table.size(); row++) {
                if (isTrue(answers.get(row), colNames, colTypes, table)) {
                    selectedRows.add(String.valueOf(row));
                }
            }
        } else {
            for (int row = 0; row < table.size(); row++) {
                selectedRows.add(String.valueOf(row));
            }
        }

        if (i < collection.length && collection[i].toUpperCase().contains("ORDER")) {
            i += 2;
            while (i < collection.length) {
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
        checkColumnNames(colNames, printColumns);
        checkColumnNames(colNames, orderColumns);
        return result;
    }
    /**
     * Update Certain rows in table if it satisfies requirments
     * @param  query query taken from user
     * @param  table contains elements of the table .
     * @param  colNames Column Names in {@code table} .
     * @param  colTypes Column Types in {@code table} .
     *
     * @return no of elements in {@code table} after update.
     *
     * @throws NullPointerException {@code query} Syntax invalid
     */
    public int update(String query, ArrayList<HashMap<String, Object>> table, ArrayList<String> colNames, ArrayList<String> colTypes) {
        clearMemory();
        int counter = 0;
        query = query.split("(\\w+\\s*)+(?i)(set)\\s*")[1];
        String ch = query.split("\\s*(?i)(where)\\s*")[0];

        //Parses The new updates
        String[] changes = ch.split("(\\s*\\=\\s*)|(\\s*\\,\\s*)");
        ArrayList<String> chColumn = new ArrayList<String>();
        ArrayList<ArrayList<String>> chValue = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < changes.length; i += 2) {
            ArrayList<String> temp = new ArrayList<String>();
            chColumn.add(changes[i].toLowerCase());
            temp.add(changes[i + 1]);
            chValue.add(temp);
            // System.out.println(changes[i] + " " + changes[i + 1]);
        }
        // checks if Column names and values provided to be updated ae written correctly and all columns does exist
        Validation_Tany_3shan_5ater_sh3rawy_2lvalidation_bta3th_mbt3mlsh_7aga(chColumn, chValue, colNames, colTypes);

        //if where statement exist , parses requirements
        if (query.toLowerCase().contains("where")) {
            query = query.split("\\s*(?i)(where)\\s*")[1];

            operationParser(query, colNames, colTypes, table);
            Validation_Tany_3shan_5ater_sh3rawy_2lvalidation_bta3th_mbt3mlsh_7aga(columnNames, oPParameters, colNames, colTypes);
            operationPerformer(colNames, colTypes, table);

            //if row (i) satsifies requirements update it
            for (int i = 0; i < table.size(); i++) {
                if (isTrue(answers.get(i), colNames, colTypes, table)) {
                    HashMap<String, Object> row = table.get(i);
                    for (int j = 0; j < chColumn.size(); j++) row.put(chColumn.get(j), chValue.get(j).get(0));
                    counter++;

                }
            }
        } else // if not updates all rows
        {
            for (int i = 0; i < table.size(); i++) {
                HashMap<String, Object> row = table.get(i);
                for (int j = 0; j < chColumn.size(); j++) row.put(chColumn.get(j), chValue.get(j).get(0));
                counter++;

            }
        }
        return table.size();
    }
    /**
     * Deletes Certain rows in table if it satisfies requirments
     * @param  query query taken from user
     * @param  table contains elements of the table .
     * @param  colNames Column Names in {@code table} .
     * @param  colTypes Column Types in {@code table} .
     *
     * @return no of elements in {@code table} after delete.
     *
     * @throws NullPointerException {@code query} Syntax invalid .
     */
    public int delete(String query, ArrayList<HashMap<String, Object>> table, ArrayList<String> colNames, ArrayList<String> colTypes) {
        clearMemory();
        int counter = 0;
        if (query.toLowerCase().contains("where")) {

            query = query.split("\\s*(?i)(where)\\s*")[1]; // what matters is what exists after where

            operationParser(query, colNames, colTypes, table);
            Validation_Tany_3shan_5ater_sh3rawy_2lvalidation_bta3th_mbt3mlsh_7aga(columnNames, oPParameters, colNames, colTypes);
            operationPerformer(colNames, colTypes, table);

            //if row (i) satsifies requirements delete it
            for (int i = 0; i < table.size(); i++) {
                if (isTrue(answers.get(i), colNames, colTypes, table)) {
                    answers.remove(i);
                    table.remove(i);
                    counter++;
                    i--;
                }
            }
        } else // if where doesn't exist in query it deletes all rows
            for (int i = 0; i < table.size(); i++) {
                table.remove(i);
                counter++;
                i--;
            }
        return counter;
    }
    /**
     * insert new rows in table
     * @param  query query taken from user
     * @param  table contains elements of the table .
     * @param  colNames Column Names in {@code table} .
     * @param  colTypes Column Types in {@code table} .
     *
     * @return no of elements in {@code table} after insertion.
     *
     * @throws NullPointerException {@code query} Syntax invalid .
     */
    public int insert(String query, ArrayList<HashMap<String, Object>> table, ArrayList<String> colNames, ArrayList<String> colTypes) {
        query = query.split("(?i)(\\s*into\\s*\\w+)")[1];
        String[] s = query.split("(?i)(\\s*values\\s*)");
        ArrayList<String> insColNames = new ArrayList<String>(); // Saves Column Names in query
        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>(); // Saves values in query
        HashMap<String, Object> columnMap = new HashMap<String, Object>(); // Template Row that will be addded
        String[] temp_1;

        for (int i = 0; i < colNames.size(); i++) columnMap.put(colNames.get(i).toLowerCase(), "NULL");

        //Parsing Values
        temp_1 = s[1].replaceAll("\\(|\\)|\\,|\\s+|\\;", " ").split("\\s+");
        for (int i = 0; i < temp_1.length; i++) {
            if (temp_1[i].equals("")) continue;
            ArrayList<String> t = new ArrayList<String>();
            t.add(temp_1[i]);
            values.add(t);
        }
        //Parsing Column Names
        if (s[0].contains("(")) {
            temp_1 = s[0].replaceAll("\\(|\\)|\\,|\\s+|\\;", " ").split("\\s+");
            for (int i = 0; i < temp_1.length; i++) {
                if (temp_1[i].equals("") /*|| columnSet.contains((Object) temp_1[i])*/) continue;
                insColNames.add(temp_1[i].toLowerCase());
            }

        } else { // if Query doesn't have Column names use column names as much as values provided in query
            for (int i = 0; i < values.size(); i++) insColNames.add(colNames.get(i));
        }
        // if No. of Columns in query greater than those exist
        if (insColNames.size() > colNames.size())
            throw new NullPointerException("Columns in query greater than Columns that exist , Can't You count right ?");
        // if no of values and column in query not equal
        if (insColNames.size() != values.size())
            throw new NullPointerException("No. of Values and No of Columns aren't equal , They have to be the same number DumbAss !");

        //Because all Values in insert query must be in the form 'Value'
        ArrayList<String> colTypesTemp = new ArrayList<String>();
        for (int i = 0; i < values.size(); i++) colTypesTemp.add("varchar");

        Validation_Tany_3shan_5ater_sh3rawy_2lvalidation_bta3th_mbt3mlsh_7aga(insColNames, values, colNames, colTypesTemp);

        //Updating values in ColumnMap with what we parsed from query according to each column type
        for (int i = 0; i < values.size(); i++) {
            String type = getColumnType(insColNames.get(i), colNames, colTypes).toLowerCase();
            String iCN = insColNames.get(i).toLowerCase();
            if (values.get(i).get(0).toLowerCase().equals("null"))
                columnMap.put(iCN, values.get(i).get(0));
            else if (type.equals("varchar"))
                columnMap.put(iCN, values.get(i).get(0));
            else if (type.equals("int"))
                columnMap.put(iCN, Integer.parseInt(values.get(i).get(0)));
            else if (type.equals("float"))
                columnMap.put(iCN, Float.parseFloat(values.get(i).get(0)));
        }
        table.add(columnMap);
        return table.size();

    }

    public int alter(String query, ArrayList<String> cNames, ArrayList<String> cTypes, ArrayList<HashMap<String, Object>> tableData) {

        return 0;
    }

    private void logicOperatorParser(String query, ArrayList<String> colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table) {
        String[] operation = query.split(" ");
        for (int i = 0; i < operation.length; i++) {
            String s = operation[i].toLowerCase();
            if (s.equals("and") || s.equals("or") || s.equals("not")) {
                logicOperator.add(s);
            }
        }
    }

    private void operationParser(String query, ArrayList<String> colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table) {
        query = query.replaceAll("\\s+|\\(+|\\)|\\,|(?i)(and\\s+(?='|\\d))|\\;", " ");
        query = query.replaceAll("\\s*\\<\\>\\s*", " != ");
        query = query.replaceAll("\\s+(?=\\=)", "");
        Pattern P1 = Pattern.compile("\\A[\\s]*(\\w+)[\\s]*([><!]?\\s*[=])[\\s]*([']?\\w+[']?)[\\s]*\\z");
        Matcher M1;
        Pattern P2 = Pattern.compile("\\A[\\s]*(\\w+)[\\s]*((?i)between|in)([\\s]*([']?\\w+[']?)[\\s]*)+\\z");
        Matcher M2;
        logicOperatorParser(query, colNames, colTypes, table);
        query = query.replaceAll("(?i)(not)\\s*", "");
        String[] operation = query.split("(?i)(\\s*(and|or)\\s*)");
        for (int i = 0; i < operation.length; i++) {
            // System.out.println(operation[i]);
            M1 = P1.matcher(operation[i]);
            M2 = P2.matcher(operation[i]);
            String[] parameters;
            if (M1.matches()) {
                parameters = new String[3];
                for (int a = 1; a < 4; a++) parameters[a - 1] = M1.group(a);
            } else if (M2.matches()) {
                parameters = operation[i].split("\\s+");
            } else throw new RuntimeException("Query Form invalid");
            columnNames.add(parameters[0].toLowerCase());
            operationNames.add(parameters[1].toLowerCase());
            ArrayList<String> temp = new ArrayList<String>();
            for (int j = 2; j < parameters.length; j++) {
                if (!parameters[j].equals("")) temp.add(parameters[j]);
            }
            oPParameters.add(temp);

        }
       /* for (int i = 0; i < columnNames.size(); i++) {
            System.out.print(columnNames.get(i) + " " + operationNames.get(i) + " ");
            for (int j = 0; j < oPParameters.get(i).size(); j++) System.out.print(oPParameters.get(i).get(j) + " ");
            System.out.println();
        }*/
    }


    private void Validation_Tany_3shan_5ater_sh3rawy_2lvalidation_bta3th_mbt3mlsh_7aga(ArrayList<String> Column, ArrayList<ArrayList<String>> Value, ArrayList<String> XDS_1, ArrayList<String> XDS_2) {
        Pattern P1 = Pattern.compile("('\\-*\\w+')");
        Matcher M1;
        Pattern P2 = Pattern.compile("(\\-*\\d+)");
        Matcher M2;
        for (int i = 0; i < Column.size(); i++) {
            int idx = XDS_1.indexOf(Column.get(i).toLowerCase());
            if (idx == -1) throw new NullPointerException("Column Name : " + Column.get(i) + " Doesn't Exist ,WTF ?! ");
            for (int j = 0; j < Value.get(i).size(); j++) {
                /*System.out.println(i + " " + j);*/
                //System.out.println(Column.get(i) + " " + Value.get(i).get(j));
                M1 = P1.matcher(Value.get(i).get(j));
                M2 = P2.matcher(Value.get(i).get(j));
                if (XDS_2.get(idx).toLowerCase().equals("int") && (M1.matches() || !M2.matches()))
                    throw new RuntimeException("Query is Wrong You Fool " + Value.get(i).get(j) + " isn't an Int");
                if (XDS_2.get(idx).toLowerCase().equals("varchar") && !M1.matches())
                    throw new RuntimeException("Query is Wrong You Stubid , in " + Value.get(i).get(j) + " isn't a varchar ");
                Value.get(i).set(j, Value.get(i).get(j).replaceAll("\\'", ""));

            }
        }
    }

    private boolean checkColumnNames(ArrayList<String> columnNames, ArrayList<String> toCheckOn) throws Exception {
        boolean found = false;
        for (int i = 0; i < toCheckOn.size(); i++) {
            if (toCheckOn.get(i).toUpperCase().equals("DESC") || toCheckOn.get(i).toUpperCase().equals("ASC")
                    || toCheckOn.get(i).toUpperCase().equals("*") || toCheckOn.get(i).toUpperCase().equals("NOORDER"))
                continue;
            found = false;
            for (int j = 0; j < columnNames.size(); j++) {
                if (toCheckOn.get(i).toUpperCase().equals(columnNames.get(j).toUpperCase())) {
                    found = true;
                }
            }
            if (!found) throw new Exception("Wrong column name: " + toCheckOn.get(i).toString());
        }
        return true;
    }

    public void operationPerformer(ArrayList<String> colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table) {
        for (int i = 0; i < table.size(); i++) {
            String ans = "";
            HashMap<String, Object> t = table.get(i);
            for (int j = 0; j < operationNames.size(); j++) {
                ArrayList<String> pp = oPParameters.get(j);
                for (int ppIterator = 0; ppIterator < pp.size(); ppIterator++)
                    pp.set(ppIterator, pp.get(ppIterator).replaceAll("'", ""));
                String columnName = columnNames.get(j);
                String type = getColumnType(columnName, colNames, colTypes);
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
                            String s = t.get(columnName).toString();
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

    String getColumnType(String columnName, ArrayList<String> colNames, ArrayList<String> colTypes) {
        for (int i = 0; i < colNames.size(); i++) {
            if (columnName.equals(colNames.get(i))) return colTypes.get(i);
        }
        return "";
    }

    public Boolean isTrue(String ans, ArrayList<String> colNames, ArrayList<String> colTypes, ArrayList<HashMap<String, Object>> table) {
        String a = "", b = "";
        boolean negative;
        int j = 0;
        String callable = "";
        for (int i = 0; i < logicOperator.size(); i++) {
            negative = false;
            String s = logicOperator.get(i);
            if (s.equals("not")) {
                negative = !negative;
                i++;
                if (i != logicOperator.size()) s = logicOperator.get(i);
            }
            if (ans.charAt(j) == '1') a = "TRUE";
            else a = "FALSE";
            j++;
            if (negative) {
                if (a == "TRUE") {
                    a = "FALSE";
                } else {
                    a = "TRUE";
                }
            }
            callable = callable + a;
            switch (s) {
                case "and":
                    callable = callable + " AND ";
                    break;
                case "or":
                    callable = callable + " OR ";
                    break;
            }
        }
        if (j < ans.length()) {
            if (ans.charAt(j) == '1') callable += "TRUE";
            else callable += "FALSE";
        }
        return parseTrueAndFalse(callable);
    }

    public boolean parseTrueAndFalse(String expression) {
        String[] stringsList = expression.split(" ");
        ArrayList<String> arrayListOfExpression = new ArrayList<>();
        for (int i = 0; i < stringsList.length; i++) {
            arrayListOfExpression.add(stringsList[i]);
        }
        arrayListOfExpression = parseAnd(arrayListOfExpression);
        return parseOr(arrayListOfExpression);
    }

    private ArrayList<String> parseAnd(ArrayList<String> strings) {
        ArrayList<String> answerList = new ArrayList<String>();
        int temp = -1;
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).equals("AND")) {
                if (answerList.get(temp).equals("TRUE") && strings.get(i + 1).equals("TRUE")) {
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
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).equals("TRUE")) return true;
        }
        return false;
    }

    private void clearMemory() {
        logicOperator.clear();
        operationNames.clear();
        answers.clear();
        columnNames.clear();
        oPParameters.clear();
    }

    private void printTable(ArrayList<String> colNames, ArrayList<HashMap<String, Object>> table) {
        for (int i = 0; i < table.size(); i++) {
            System.out.println("\nRow No. " + i);
            for (int j = 0; j < colNames.size(); j++) {
                System.out.println(colNames.get(j) + " = " + table.get(i).get(colNames.get(j)).toString());
            }
        }
    }

    public static void main(String[] args) {
        Parser e = new Parser();
        ArrayList<HashMap<String, Object>> table = new ArrayList<HashMap<String, Object>>();
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
        ArrayList<String> colNames = new ArrayList<>();
        ArrayList<String> colTypes = new ArrayList<>();
        colNames.add("subject");
        colNames.add("age");
        colNames.add("name");
        colTypes.add("varchar");
        colTypes.add("int");
        colTypes.add("varchar");

        //e.select("seLect * from table1 where age >= '5' and name = omar order by subject",colNames,colTypes,table);
        System.out.println("Before Update_1");
        e.printTable(colNames, table);

        e.update("UPDATE table_name SET SUBJECT = 'value1', AGE = -2 WHERE not AGE between 15  and 20  ", table, colNames, colTypes);
        System.out.println("\nAfter Update_1");
        e.printTable(colNames, table);

        e.insert("INSERT INTO table_name (SubJect, AgE) VALUES ('Lovely_Statistics', '2');", table, colNames, colTypes);
        System.out.println("\nAfter Insert_1");
        e.printTable(colNames, table);

        e.insert("INSERT INTO table_name VALUES ( '-10');", table, colNames, colTypes);
        System.out.println("\nAfter Insert_2");
        e.printTable(colNames, table);

        e.update("UPDATE table_name SET subject = 'value1', age = -2", table, colNames, colTypes);
        System.out.println("\nAfter Update_2");
        e.printTable(colNames, table);

        e.delete("DELETE From table_name WHERE  subject IN ('Maths', 'Science') AnD age between 5 and 18 oR name='compu'; ", table, colNames, colTypes);
        System.out.println(" \nAfter Delete_1");
        e.printTable(colNames, table);

        e.delete("DELETE From table_name ; ", table, colNames, colTypes);
        System.out.println(" \nAfter Delete_2");
        e.printTable(colNames, table);

    }


}
