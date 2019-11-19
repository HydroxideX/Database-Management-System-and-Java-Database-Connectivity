package eg.edu.alexu.csd.oop.cs71.db;

import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

class Parser {
    private Main engine = new Main();
    boolean validateQuery(String q)
    {
        String q2=q;
        q=q.toLowerCase();
        String[] dataSplit=q.split("\\(");
       String[] command=q.split(" ");
       String[] command2=q2.split(" ");
       if(command.length<2)return false;
        switch (command[0])
        {
            case "create": case "drop":{
                switch (command[1])
                {
                    case "database": case "schema":{
                        if(command.length>3)
                            return false;
                    }
                    break;
                    case "table":
                    {
                        if(command.length<3)
                            return false;
                        if(command[0].equals("drop")&&command.length>3)return false;
                        if(command.length==3)return true;
                        if(!command[3].contains("("))
                            return false;
                        if(dataSplit.length !=2)
                            return false;
                        boolean regex= dataSplit[1].matches("(\\s?\\w+\\s(int|varchar)+\\s?\\,\\s?)*(\\s?\\w+\\s(int|varchar)+\\s?\\))");
                        if(!regex)
                        {
                            return false;
                        }
                    }
                    break;
                    default:return false;
                }
            }
            break;
            case "use":
            {
                if(command.length>2)
                    return false;
            }
            break;
            case "alter":
            {
                boolean regex=q.matches("(alter)\\s(table)\\s\\w+\\s(((add)\\s\\w+\\s(int|varchar))|((modify)\\s\\w+\\s(int|varchar))|((change)\\s\\w+\\s\\w+\\s(int|varchar))|((drop)\\s(column)\\s\\w+))");
                if(!regex)
                    return false;
            }
            break;
            case "delete":
            {

            }
            break;
            case "update":
            {

            }
            break;
            case "insert":
            {

            }
            break;
            case "select":
            {
                boolean regex=q.matches("(select)\\s(\\*\\s|(\\w+\\s?\\,\\s?)*(\\w+\\s))(from)\\s\\w+(\\s(where)\\s(not\\s)?\\w+\\s?(\\=|\\>\\=|\\<\\=|\\<\\>|\\<|\\>|\\!\\=|between|like|in)\\s?(\\'\\s?\\w+\\s?\\'|\\d+)(\\sand\\s(not\\s)?\\w+\\s?(\\=|\\>\\=|\\<\\=|\\<\\>|\\<|\\>|\\!\\=|between|like|in)\\s?(\\'\\s?\\w+\\s?\\'|\\d+))*(\\sor\\s(not\\s)?\\w+\\s?(\\=|\\>\\=|\\<\\=|\\<\\>|\\<|\\>|\\!\\=|between|like|in)\\s?(\\'\\s?\\w+\\s?\\'|\\d+))*)?|(\\sorder\\sby\\s(\\*\\s|(\\w+\\s?\\,\\s?(asc|desc)?)*(\\w+\\s(asc|desc)?)))");
                if(!regex)
                    return false;
            }
            break;
            default:return false;
        }
        return true;
    }

    void parse(String query) {
        String checker;
        String[] command=query.split(" ");
        checker = query.substring(0,8);
        checker = checker.toUpperCase();
        String secondChecker = query.toUpperCase();
        if (checker.contains("UPDATE") || checker.contains("INSERT")
                || checker.contains("DELETE")) {
            try {
                engine.executeUpdateQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (checker.contains("CREATE")){
            if (secondChecker.contains("DATABASE")) {
                engine.createDatabase(command[2],true);
            } else {
                try {
                    engine.executeStructureQuery(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if(checker.contains("SELECT")){
            try {
                engine.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(checker.contains("USE"))
        {
            engine.createDatabase(command[2],false);
        }
    }
}
