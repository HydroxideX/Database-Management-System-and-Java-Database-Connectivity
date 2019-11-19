package eg.edu.alexu.csd.oop.cs71.db;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.File;
public class Main implements Database{
    static ArrayList<String> databases=new ArrayList<>();
    String currentDatabase= "";
    public static void startUp()
    {
         File file = new File("Databases/");
        for( File child : file.listFiles()) {
            databases.add(child.getName());
        }
        try {
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();
            s+="\\Databases";
            file = new File(s);
            boolean flag = file.mkdir();
            // System.out.print("Directory created? " + flag);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public String createDatabase(String databaseName, boolean dropIfExists) {
        if (dropIfExists) {
            try {
                executeStructureQuery("drop database "+databaseName);
                executeStructureQuery("create database "+databaseName);
                currentDatabase=databaseName;
                Gui.currentDb.setText("Database: "+currentDatabase);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (!dropIfExists){
            boolean exist = false;
            for (int i=0; i<databases.size(); i++){
                if (databases.get(i).equals(databaseName)) {
                    exist = true;
                    break;
                }
            }
            if (!exist){
                try {
                    executeStructureQuery("create database "+databaseName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else {
                currentDatabase=databaseName;
                Gui.currentDb.setText("Database: "+currentDatabase);
            }

        }
        currentDatabase=databaseName;
        Path currentRelativePath = Paths.get("");
        String currentpath = currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" + databaseName;

        return currentpath;
    }

    @Override
    public boolean executeStructureQuery(String query) throws SQLException {
        String[] command=query.split(" ");
        String checker = query.substring(0, 8);
        checker = checker.toUpperCase();
        String secondChecker = command[1].toUpperCase();
        if (checker.contains("CREATE")) {
            if (secondChecker.contains("DATABASE")) {
                String s="";
                try {
                    Path currentRelativePath = Paths.get("");
                    s = currentRelativePath.toAbsolutePath().toString();
                    s+="\\Databases\\";
                    s+=command[2];
                    File file = new File(s);
                    boolean flag = file.mkdir();
                    databases.add(command[2]);
                    System.out.print("Directory created? " + flag);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (checker.contains("DROP")) {
            if (secondChecker.contains("DATABASE")) {
                boolean exist = false;
                int foundat=0;
                for (int i = 0; i < databases.size(); i++) {
                    if (databases.get(i).equals(command[2])) {
                        exist = true;
                        foundat=i;
                    }
                }
                if (exist) {
                    Path currentRelativePath = Paths.get("");
                    File dir = new File(currentRelativePath.toAbsolutePath().toString() + "\\Databases\\" +command[2]);
                    File[] listFiles = dir.listFiles();
                    for (File file : listFiles) {
                        //System.out.println("Deleting " + file.getName());
                        file.delete();
                    }
                    boolean flag=dir.delete();
                    databases.remove(foundat);
                    Gui.currentDb.setText("Database: ");
                    if (flag)
                        System.out.println("dir Deleted ");
                }
            }
        }

        return true;
    }

    @Override
    public Object[][] executeQuery(String query) throws SQLException {
        return new Object[0][];
    }

    @Override
    public int executeUpdateQuery(String query) throws SQLException {
        return 0;
    }
}
