package eg.edu.alexu.csd.oop.cs71.db;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main implements Database{
    ArrayList<String> databases=new ArrayList<>();
    String currentDatabase= "";
    Gui gui=new Gui();
    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
        return null;
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException {
        return false;
    }

    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        return new Object[0][];
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        return 0;
    }
    public static void  main(String[] args){
        System.out.println("Hello World");
        System.out.println("Hello World");
        System.out.println("Hello World");
        System.out.println("Hello World");
    }
}
