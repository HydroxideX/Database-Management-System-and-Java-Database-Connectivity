package eg.edu.alexu.csd.oop.cs71.db;

import java.util.ArrayList;

public class secondParser {

    private String[] keywordArrayEquals = {"SELECT","FROM","*","WHERE","AND","OR","NOT", "'","ORDER", "BY",
            "COUNT","BY","AS","LIKE", "BETWEEN","DESC","NULL","IS","SUM","AVG","MAX","MIN",""};
    private String[] keywordArrayContains = {"'"};

    public ArrayList<String> getSelectNames (String query) {
        String q = query.toUpperCase();
        q = q.replaceAll("[\\=,>,<,\\(,\\),;]", " ");
        q = q.substring(7,q.length());
        String[] collection = q.split(" ");
        ArrayList<String> ans = new ArrayList<String>();
        for (int i = 0;i<collection.length;i++) {
            if(In(collection[i])){
                continue;
            } else {
                if(collection[i-1].equals("FROM")){
                    ans.add("TABLE:"+collection[i]);
                    continue;
                }
                ans.add(collection[i]);
            }
        }
        return ans;
    }
    private boolean In(String s){
        for(int i = 0;i<keywordArrayEquals.length;i++){
            if(s.equals(keywordArrayEquals[i])) return true;
        }
        for(int i = 0;i<keywordArrayContains.length;i++){
            if(s.contains(keywordArrayContains[i])) return true;
        }
        return false;
    }

    public ArrayList<String> parseSelect (String query) {
        query.replaceAll("<>","!=");
        String q = query.toUpperCase();
        q = q.substring(7,q.length());
        String[] collection = q.split(" ");
        ArrayList<String> ans = new ArrayList<String>();
        int i = 0;
        if (q.contains("*")) {
            ans.add("*");
            ans.add(collection[1]);
            ans.add(collection[2]);
            i = 3;
        } else {
            while (!collection[i].contains("FROM")) {
                if (collection[i].charAt(collection[i].length() - 1) == ',') {
                    ans.add(collection[i].substring(0, collection[i].length() - 1));
                } else {
                    ans.add(collection[i]);
                }
                i++;
            }
            ans.add(collection[i]);
            i++;
            ans.add(collection[i]);
        }
        if (i <collection.length && collection[i].contains("WHERE")){
            ans.add(collection[i++]);
            String s = "";
            while(i < collection.length && !collection[i].contains("ORDER")){
                s = s + " " +collection[i];
                i++;
            }
            ans.add(s);
        }
        if(i<collection.length && collection[i].contains("ORDER")){
            ans.add("ORDERBY");
            i+=2;
            while (i<collection.length) {
                if (collection[i].charAt(collection[i].length() - 1) == ',') {
                    ans.add(collection[i].substring(0, collection[i].length() - 1));
                } else {
                    ans.add(collection[i]);
                }
                i++;
            }
        }
        return ans;
    }


    public boolean parseTrueAndFalse(String expression){
        String[] stringsList = expression.split(" ");
        ArrayList<String> arrayListOfExpression = new ArrayList<>();
        for(int i = 0;i< stringsList.length;i++){
            arrayListOfExpression.add(stringsList[i]);
        }
        arrayListOfExpression = parseNot(arrayListOfExpression);
        arrayListOfExpression = parseAnd(arrayListOfExpression);
        return parseOr(arrayListOfExpression);
    }

    private ArrayList<String> parseNot(ArrayList<String> strings){
        ArrayList<String> answerList  = new ArrayList<String>();
        for(int i = 0;i<strings.size();i++){
            if(strings.get(i).equals("NOT")){
                if(strings.get(i+1).equals("TRUE")){
                    answerList.add("FALSE");
                } else{
                    answerList.add("TRUE");
                }
                i++;
            } else {
                answerList.add(strings.get(i));
            }
        }
        return answerList;
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
}
