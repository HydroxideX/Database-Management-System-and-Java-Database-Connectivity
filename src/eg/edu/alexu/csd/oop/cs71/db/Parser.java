package eg.edu.alexu.csd.oop.cs71.db;

import java.sql.SQLException;

public class Parser {
    Main engine = new Main();
    public boolean validateQuery(String q)
    {
        return false;
    }

    public void parse(String query) {
        String checker = new String();
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
                engine.createDatabase(query,true);
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
