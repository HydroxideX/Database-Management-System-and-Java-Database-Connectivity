package eg.edu.alexu.csd.oop.cs71.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLParser {

    private ArrayList<String> columnNames;
    private ArrayList<String> operationNames;
    private ArrayList<String> logicOperator;
    private ArrayList<String> answers;
    private ArrayList<ArrayList<String>> oPParameters;
    ConditionalParser stringConditionalParser;
    ConditionalParser integerConditionalParser;

    private static SQLParser uniqueSQLParserInstance;
    private SQLParser() {
        columnNames = new ArrayList<>();
        operationNames = new ArrayList<>();
        logicOperator = new ArrayList<>();
        answers = new ArrayList<>();
        oPParameters = new ArrayList<>();
        stringConditionalParser = new StringConditionalParser();
        integerConditionalParser = new IntegerConditionalParser();
    }
    public static synchronized SQLParser getInstance() {
        if (uniqueSQLParserInstance == null) {
            uniqueSQLParserInstance = new SQLParser();
        }
        return uniqueSQLParserInstance;
    }

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
            ValidateColumnNames(columnNames, oPParameters, colNames, colTypes);
            for(int iterator = 0;iterator<table.size();iterator++){
                answers.add("");
            }
            for (int j = 0; j < operationNames.size(); j++) {
                ArrayList<String> pp = oPParameters.get(j);
                String columnName = columnNames.get(j);
                String type = getColumnType(columnName, colNames, colTypes);
                if(type.equals("varchar")){
                    stringConditionalParser.operationPerformer(columnName, table,pp,answers,operationNames.get(j));
                } else if (type.equals("int")){
                    integerConditionalParser.operationPerformer(columnName, table,pp,answers,operationNames.get(j));
                }
            }

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
        String[] changes = ch.split("(\\s*\\=\\s*)|(\\s*\\,\\s*)");
        ArrayList<String> chColumn = new ArrayList<String>();
        ArrayList<ArrayList<String>> chValue = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < changes.length; i += 2) {
            ArrayList<String> temp = new ArrayList<String>();
            chColumn.add(changes[i].toLowerCase());
            temp.add(changes[i + 1]);
            chValue.add(temp);
        }
        ValidateColumnNames(chColumn, chValue, colNames, colTypes);
        if (query.toLowerCase().contains("where")) {
            query = query.split("\\s*(?i)(where)\\s*")[1];
            operationParser(query, colNames, colTypes, table);
            ValidateColumnNames(columnNames, oPParameters, colNames, colTypes);
            for(int iterator = 0;iterator<table.size();iterator++){
                answers.add("");
            }
            for (int j = 0; j < operationNames.size(); j++) {
                ArrayList<String> pp = oPParameters.get(j);
                String columnName = columnNames.get(j);
                String type = getColumnType(columnName, colNames, colTypes);
                if(type.equals("varchar")){
                    stringConditionalParser.operationPerformer(columnName, table,pp,answers,operationNames.get(j));
                } else if (type.equals("int")){
                    integerConditionalParser.operationPerformer(columnName, table,pp,answers,operationNames.get(j));
                }
            }
            for (int i = 0; i < table.size(); i++) {
                if (isTrue(answers.get(i), colNames, colTypes, table)) {
                    HashMap<String, Object> row = table.get(i);
                    for (int j = 0; j < chColumn.size(); j++) row.put(chColumn.get(j), chValue.get(j).get(0));
                    counter++;

                }
            }
        } else
        {
            for (int i = 0; i < table.size(); i++) {
                HashMap<String, Object> row = table.get(i);
                for (int j = 0; j < chColumn.size(); j++) row.put(chColumn.get(j), chValue.get(j).get(0));
                counter++;

            }
        }
        return counter;
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
            query = query.split("\\s*(?i)(where)\\s*")[1];
            operationParser(query, colNames, colTypes, table);
            ValidateColumnNames(columnNames, oPParameters, colNames, colTypes);
            for(int iterator = 0;iterator<table.size();iterator++){
                answers.add("");
            }
            for (int j = 0; j < operationNames.size(); j++) {
                ArrayList<String> pp = oPParameters.get(j);
                String columnName = columnNames.get(j);
                String type = getColumnType(columnName, colNames, colTypes);
                if(type.equals("varchar")){
                    stringConditionalParser.operationPerformer(columnName, table,pp,answers,operationNames.get(j));
                } else if (type.equals("int")){
                    integerConditionalParser.operationPerformer(columnName, table,pp,answers,operationNames.get(j));
                }
            }
            for (int i = 0; i < table.size(); i++) {
                if (isTrue(answers.get(i), colNames, colTypes, table)) {
                    answers.remove(i);
                    table.remove(i);
                    counter++;
                    i--;
                }
            }
        } else
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
        ArrayList<String> insColNames = new ArrayList<String>();
        ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
        HashMap<String, Object> columnMap = new HashMap<String, Object>();
        String[] temp_1;
        for (int i = 0; i < colNames.size(); i++) columnMap.put(colNames.get(i).toLowerCase(), "NULL");
        temp_1 = s[1].replaceAll("\\(|\\)|\\,|\\s+|\\;", " ").split("\\s+");
        for (int i = 0; i < temp_1.length; i++) {
            if (temp_1[i].equals("")) continue;
            ArrayList<String> t = new ArrayList<String>();
            t.add(temp_1[i]);
            values.add(t);
        }
        if (s[0].contains("(")) {
            temp_1 = s[0].replaceAll("\\(|\\)|\\,|\\s+|\\;", " ").split("\\s+");
            for (int i = 0; i < temp_1.length; i++) {
                if (temp_1[i].equals("") /*|| columnSet.contains((Object) temp_1[i])*/) continue;
                insColNames.add(temp_1[i].toLowerCase());
            }

        } else {
            for (int i = 0; i < values.size(); i++) insColNames.add(colNames.get(i));
        }
        if (insColNames.size() > colNames.size())
            throw new NullPointerException("Columns in query greater than Columns that exist , Can't You count right ?");
        if (insColNames.size() != values.size())
            throw new NullPointerException("No. of Values and No of Columns aren't equal , They have to be the same number DumbAss !");
       // ArrayList<String> colTypesTemp = new ArrayList<String>();
       // for (int i = 0; i < values.size(); i++) colTypesTemp.add("varchar");
        ValidateColumnNames(insColNames, values, colNames, colTypes);
        columnMap.clear();
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
        return 1;

    }

    String getColumnType(String columnName, ArrayList<String> colNames, ArrayList<String> colTypes) {
        for (int i = 0; i < colNames.size(); i++) {
            if (columnName.equals(colNames.get(i))) return colTypes.get(i);
        }
        return "";
    }

    public int alter(String query, ArrayList<String> cNames, ArrayList<String> cTypes, ArrayList<HashMap<String, Object>> tableData) {

        return 0;
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

    private void ValidateColumnNames(ArrayList<String> Column, ArrayList<ArrayList<String>> Value, ArrayList<String> XDS_1, ArrayList<String> XDS_2) {
        Pattern P1 = Pattern.compile("('\\-*\\w+')");
        Matcher M1;
        Pattern P2 = Pattern.compile("(\\-*\\d+)");
        Matcher M2;
        for (int i = 0; i < Column.size(); i++) {
            int idx = XDS_1.indexOf(Column.get(i).toLowerCase());
            if (idx == -1) throw new NullPointerException("Column Name : " + Column.get(i) + " Doesn't Exist ,WTF ?! ");
            for (int j = 0; j < Value.get(i).size(); j++) {
                M1 = P1.matcher(Value.get(i).get(j));
                M2 = P2.matcher(Value.get(i).get(j));
                if (XDS_2.get(idx).toLowerCase().equals("int") && (M1.matches() || !M2.matches()))
                    throw new RuntimeException( "Column " + Value.get(i).get(j) + " isn't an Int");
                if (XDS_2.get(idx).toLowerCase().equals("varchar") && !M1.matches())
                    throw new RuntimeException("Column " + Value.get(i).get(j) + " isn't a Varchar ");
                Value.get(i).set(j, Value.get(i).get(j).replaceAll("\\'", ""));

            }
        }
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

}
