package eg.edu.alexu.csd.oop.cs71.jdbc.src;



import java.io.File;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class YahiasAwesomeDriverManager101 {

    public static Object QueryHandle(String query) throws SQLException {
        Scanner input = new Scanner(System.in);
        Driver sqldriver = new SQLDriver();
        String queryConnection = "jdbc:xmldb://localhost";
        Object object;
        if (sqldriver.acceptsURL(queryConnection)) {
            Properties info = new Properties();
            File dbDir = new File("");
            info.put("path", dbDir.getAbsoluteFile());
            Connection c = (Connection) sqldriver.connect(queryConnection, info);
            Statement finalStatement = c.createStatement();
            if (query.contains("select")) {
                assert finalStatement != null;
                Resultset rs = finalStatement.executeQuery(query);
                Object[][] table = convertResultSetTOArray(rs);
                return table;
            } else if (query.contains("create") || query.contains("drop") || query.contains("use")) {
                object = finalStatement.execute(query);
                return object;
            } else if (query.contains("update") || query.contains("insert") || query.contains("delete")) {
                int x = finalStatement.executeUpdate(query);
                return x;
            }
        }
        return null;
    }


    private static Object[][] convertResultSetTOArray(Resultset rs) throws SQLException {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int cols = resultSetMetaData.getColumnCount();
            int rows = 0;
            while(!rs.isAfterLast())  {
                rows++;
                rs.next();
            }
            String[][] table = new String[rows+1][cols];
            while(!rs.isFirst())  {
                rows--;
                rs.previous();
            }
            for(int i = 0; i < cols; i++){
                table[rows][i] = resultSetMetaData.getColumnLabel(i+1);
            }
            rows++;
            while(true){
                if(rs.isAfterLast()) break;
                for(int i = 0; i < cols;i++){
                    Object temp = rs.getObject(i+1);
                    if(temp.toString().toLowerCase().equals("null")) table[rows][i] = "";
                    table[rows][i] = temp.toString();
                }
                rs.next();
                rows++;
            }
            return table;
    }
}



