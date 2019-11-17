package eg.edu.alexu.csd.oop.cs71.db;

import java.sql.SQLException;
import java.util.ArrayList;

class Parser {
    private Main engine = new Main();
    boolean validateQuery(String q)
    {
        String q2=q;
        q=q.toLowerCase();
       String[] command=q.split(" ");
       String[] command2=q2.split(" ");
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
                    default:return false;
                }
            }
            break;
            case "use":
            {
                if(command.length>2)
                    return false;
                else {
                    if(engine.databases.contains(command2[1]))
                    {
                        engine.currentDatabase=command2[1];
                        engine.gui.currentDb.setText("Database:"+command2[1]);
                    }
                }
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
        } else if (checker.contains("CREATE") || checker.contains("DROP")){
            if (secondChecker.contains("DATABASE")) {
                if(engine.databases.contains(command[2]))
                engine.createDatabase(query,true);
                else
                    engine.createDatabase(query,false);
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
        }
    }
}
