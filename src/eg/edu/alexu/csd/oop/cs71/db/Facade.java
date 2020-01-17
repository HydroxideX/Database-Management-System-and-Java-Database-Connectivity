package eg.edu.alexu.csd.oop.cs71.db;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

public class Facade {
    public SQLDatabase engine;
    public Facade (){
        engine = SQLDatabase.getInstance();
    }
    public ArrayList<String> getColumnTypes (){
        return engine.cTypes;
    }

    public String getTablePath(String query){
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        s+="\\Databases\\";
        s+=engine.currentDatabase;
        s+="\\";
        FileManagement a =new FileManagement();
        s+=a.getTableName(query);
        return s;
    }

    public Object parse(String query) throws SQLException {
        String z = query;
        SQLDatabase.startUp();
        String checker;
        z=z.replaceAll("\\s+"," ");
        String[] command=z.split(" ");
        String query2=z;
        query2 += "NullValueToPassUse";
        checker = query2.substring(0,8);
        checker = checker.toUpperCase();
        String secondChecker = z.toUpperCase();
        if (checker.contains("UPDATE") || checker.contains("INSERT")
                || checker.contains("DELETE")) {
                return engine.executeUpdateQuery(z);
        } else if (checker.contains("CREATE")){
            if (secondChecker.contains("DATABASE")) {
               return engine.createDatabase(command[2],true);
            } else {
                    return  engine.executeStructureQuery(z);
            }
        }else if(checker.contains("DROP"))
        {
               return engine.executeStructureQuery(z);
        }
        else if(checker.contains("SELECT")){
               return engine.executeQuery(z);
        }else if(checker.contains("USE"))
        {
            return engine.createDatabase(command[1],false);
        }
        return null;
    }

    public Object[][] getFullTable(Object[][] incompleteTable)
    {
        ArrayList<String> cNames=engine.cNames;
        Object[][] newTable = new Object[incompleteTable.length+1][cNames.size()];

        for(int i = 0;i<cNames.size();i++){
            newTable[0][i] = cNames.get(i);
        }
        for(int i=0;i<incompleteTable.length;i++)
        {
            for(int j=0;j<incompleteTable[i].length;j++)
            {
                newTable[i+1][j]=incompleteTable[i][j];
            }
        }
        return newTable;
    }
}
