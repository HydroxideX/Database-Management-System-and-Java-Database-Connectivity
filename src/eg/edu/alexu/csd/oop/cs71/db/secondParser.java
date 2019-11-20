package eg.edu.alexu.csd.oop.cs71.db;

import java.util.ArrayList;

public class secondParser {

    public static void main(String[] args) {
        System.out.println(parseSelect("SELECT AVG(Price) FROM Products"));
    }

    public static ArrayList<String> parseSelect (String query) {
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

    public static ArrayList<String> getTableAndColumnNames (String query) {
        String q = query.toUpperCase();
        q = q.replaceAll("[\\=,>,<,\\(,\\),;]", " ");
        q = q.substring(7,q.length());
        String[] collection = q.split(" ");
        ArrayList<String> ans = new ArrayList<String>();
        for(int i = 0;i<collection.length;i++){
            if(collection[i].equals("SELECT")||collection[i].equals("FROM")
                    ||collection[i].equals("*")||collection[i].equals("WHERE")
                    || collection[i].equals("AND") || collection[i].equals("NOT")
                    ||collection[i].contains("'") ||collection[i].equals("OR")
                    ||collection[i].equals("ORDER")||collection[i].equals("BY")
                    || collection[i].equals("COUNT")||collection[i].equals("AS")
                    ||collection[i].contains("LIKE") ||collection[i].contains("BETWEEN")
                    ||collection[i].contains("DESC") ||collection[i].contains("NULL")
                    ||collection[i].equals("IS") || collection[i].equals("SUM")
                    || collection[i].equals("AVG") ||collection[i].equals("MAX")
                    ||collection[i].equals("MIN") ||collection[i].equals("")){
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
}
