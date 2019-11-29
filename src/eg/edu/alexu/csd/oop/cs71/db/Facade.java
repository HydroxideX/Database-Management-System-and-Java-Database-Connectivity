package eg.edu.alexu.csd.oop.cs71.db;

import java.sql.SQLException;

class Facade {
    public SQLDatabase engine;
    public Facade (){
        engine = SQLDatabase.getInstance();
    }

    Object parse(String query) {
        String checker;
        String[] command=query.split(" ");
        String query2=query;
        query2 += "NullValueToPassUse";
        checker = query2.substring(0,8);
        checker = checker.toUpperCase();
        String secondChecker = query.toUpperCase();
        if (checker.contains("UPDATE") || checker.contains("INSERT")
                || checker.contains("DELETE")||checker.contains("ALTER")) {
            try {
                return engine.executeUpdateQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (checker.contains("CREATE")){
            if (secondChecker.contains("DATABASE")) {
               return engine.createDatabase(command[2],true);
            } else {
                try {
                    return  engine.executeStructureQuery(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }else if(checker.contains("DROP"))
        {
            try {
               return engine.executeStructureQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(checker.contains("SELECT")){
            try {
               return engine.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(checker.contains("USE"))
        {
            return engine.createDatabase(command[1],false);
        }
        return null;
    }
}
