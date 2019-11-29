package eg.edu.alexu.csd.oop.cs71.db;

import java.util.ArrayList;
import java.util.HashMap;

public class IntegerConditionalParser implements ConditionalParser {
    public void operationPerformer(String columnName, ArrayList<HashMap<String, Object>> table,ArrayList<String> pp,ArrayList<String> answers,String operation) {
        for (int i = 0; i < table.size(); i++) {
            String ans = "";
            HashMap<String, Object> t = table.get(i);
            for (int ppIterator = 0; ppIterator < pp.size(); ppIterator++)
                pp.set(ppIterator, pp.get(ppIterator).replaceAll("'", ""));
            if (t.get(columnName).toString().toLowerCase().equals("null")) continue;
            switch (operation) {
                case "between":
                    if (Integer.parseInt(t.get(columnName).toString()) >= Integer.parseInt(pp.get(0)) && Integer.parseInt(t.get(columnName).toString()) <= Integer.parseInt(pp.get(1))) {
                        ans += "1";
                    } else ans += "0";
                    break;
                case "in":
                    Boolean x = false;
                    for (int a = 0; a < pp.size(); a++) {
                        if (Integer.valueOf(t.get(columnName).toString()).equals(Integer.valueOf(pp.get(a)))) {
                            ans += "1";
                            x = true;
                            break;
                        }
                    }
                    if (x) break;
                    else ans += "0";
                    break;
                case "=":
                    if (Integer.valueOf(t.get(columnName).toString()).equals(Integer.valueOf(pp.get(0)))) ans += "1";
                    else ans += "0";
                    break;
                case ">=":
                    String sr = t.get(columnName).toString();
                    if (Integer.parseInt(t.get(columnName).toString()) >= Integer.parseInt(pp.get(0))) ans += "1";
                    else ans += "0";
                    break;
                case "<=":
                    if (Integer.parseInt(t.get(columnName).toString()) <= Integer.parseInt(pp.get(0))) ans += "1";
                    else ans += "0";
                    break;
                case ">":
                    if (Integer.parseInt(t.get(columnName).toString()) > Integer.parseInt(pp.get(0))) ans += "1";
                    else ans += "0";
                    break;
                case "<":
                    if (Integer.parseInt(t.get(columnName).toString()) < Integer.parseInt(pp.get(0))) ans += "1";
                    else ans += "0";
                    break;
                case "!=":
                case "<>":
                    if (!Integer.valueOf(t.get(columnName).toString()).equals(Integer.valueOf(pp.get(0)))) ans += "1";
                    else ans += "0";
                    break;
            }
            answers.set(i, answers.get(i) + ans);
        }
    }
}
