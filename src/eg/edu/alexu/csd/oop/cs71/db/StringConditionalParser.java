package eg.edu.alexu.csd.oop.cs71.db;

import java.util.ArrayList;
import java.util.HashMap;

public class StringConditionalParser implements ConditionalParser{

    public void operationPerformer(String columnName, ArrayList<HashMap<String, Object>> table,ArrayList<String> pp,ArrayList<String> answers,String operation) {
        for (int i = 0; i < table.size(); i++) {
            String ans = "";
            HashMap<String, Object> t = table.get(i);
            for (int ppIterator = 0; ppIterator < pp.size(); ppIterator++)
                pp.set(ppIterator, pp.get(ppIterator).replaceAll("'", ""));
            if (t.get(columnName).toString().toLowerCase().equals("null")) continue;
            switch (operation) {
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
            answers.set(i, answers.get(i) + ans);
        }
    }



}


